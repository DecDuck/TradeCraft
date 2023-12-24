package com.decduck3.tradecraft.web.handlers;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.User;
import com.decduck3.tradecraft.security.AccountLinkManager;
import com.decduck3.tradecraft.security.PasswordUtilities;
import com.decduck3.tradecraft.web.session.ArbitraryDataSecurityContext;
import com.decduck3.tradecraft.web.session.SessionStorage;
import com.decduck3.tradecraft.web.session.storages.MemorySessionStorage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.undertow.Handlers;
import io.undertow.io.Receiver;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.HttpString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class AuthenticationHandler implements HttpHandler {
    private record AuthFetchUser(String name, String uuid, boolean loginConfigured) {
    }

    private SessionStorage storage;

    public AuthenticationHandler() {
        String sessionType = TradeCraft.config().getString("session.type");
        if (sessionType == null) {
            storage = new MemorySessionStorage();
        } else {
            switch (sessionType) {
                case "memory":
                    storage = new MemorySessionStorage();
                    break;
                // Add more as needed
            }
        }

        if (storage == null) {
            TradeCraft.logger().severe("Could not create session storage controller, web UI will NOT work correctly.");
        }
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        ArbitraryDataSecurityContext data = (ArbitraryDataSecurityContext) httpServerExchange.getSecurityContext();
        String sessionToken = data.getData("sessionToken");
        Handlers.routing()
                // User self-fetch
                .add("GET", "/fetch", exchange -> {
                    User user = requireUser(exchange, sessionToken);
                    if (user == null) return;

                    AuthFetchUser returnUser = new AuthFetchUser(user.getCUsername(), user.getPlayerUUID(), user.isAlternativeLogin());
                    Gson gson = new Gson();

                    exchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(gson.toJson(returnUser));
                })
                .add("POST", "/setupacc", exchange -> {
                    User user = requireUser(exchange, sessionToken);
                    if (user == null) return;

                    // Decided to use the same endpoint for creating/updating
                    /*
                    if (user.isAlternativeLogin()) {
                        exchange.setStatusCode(403);
                        exchange.getResponseSender().close();
                        return;
                    }
                     */

                    exchange.getRequestReceiver().receiveFullString((outExchange, bodyRaw) -> {
                        if (bodyRaw == null || bodyRaw.isBlank() || bodyRaw.isEmpty()) {
                            outExchange.setStatusCode(400);
                            outExchange.getResponseSender().close();
                            return;
                        }

                        Gson gson = new Gson();
                        UsernamePasswordBody body;
                        try {
                            body = gson.fromJson(bodyRaw, UsernamePasswordBody.class);
                        } catch (JsonSyntaxException e) {
                            outExchange.setStatusCode(400);
                            outExchange.getResponseSender().close();
                            return;
                        }

                        // Check username
                        User existingUser = User.findUserByUsername(body.username);
                        if(existingUser != null && !existingUser.getId().equals(user.getId())){
                            outExchange.setStatusCode(400);
                            outExchange.getResponseSender().send("Username already taken, try another.");
                            outExchange.getResponseSender().close();
                            return;
                        }

                        // Check password and hash
                        if(!PasswordUtilities.validPassword(body.password)){
                            outExchange.setStatusCode(400);
                            outExchange.getResponseSender().send("Invalid password, must be longer than 11 characters.");
                            outExchange.getResponseSender().close();
                            return;
                        }

                        String passwordHash = PasswordUtilities.hashPassword(body.password);

                        TradeCraft.database().getUsers().updateOne(eq("_id", user.getId()),
                                combine(
                                    set("alternativeLogin", true),
                                    set("alternativeUsername", body.username),
                                    set("alternativePasswordHash", passwordHash)
                                )
                        );

                        outExchange.setStatusCode(201);
                        outExchange.getResponseSender().close();
                    });
                })
                .add("POST", "/login", exchange -> {
                    exchange.getRequestReceiver().receiveFullString((outExchange, bodyRaw) -> {
                        if (bodyRaw == null || bodyRaw.isBlank() || bodyRaw.isEmpty()) {
                            outExchange.setStatusCode(400);
                            outExchange.getResponseSender().close();
                            return;
                        }

                        Gson gson = new Gson();
                        UsernamePasswordBody body;
                        try {
                            body = gson.fromJson(bodyRaw, UsernamePasswordBody.class);
                        } catch (JsonSyntaxException e) {
                            outExchange.setStatusCode(400);
                            outExchange.getResponseSender().close();
                            return;
                        }

                        User user = User.findUserByUsername(body.getUsername());
                        if(user == null){
                            outExchange.setStatusCode(403);
                            outExchange.getResponseSender().close();
                            return;
                        }

                        if(!PasswordUtilities.comparePasswords(body.getPassword(), user.getAlternativePasswordHash())){
                            outExchange.setStatusCode(403);
                            outExchange.getResponseSender().close();
                            return;
                        }

                        storage.save(sessionToken, "userID", user.getId().toString());
                        outExchange.setStatusCode(200);
                        outExchange.getResponseSender().close();
                    });
                })
                .add("GET", "/link/begin", exchange -> {
                    AccountLinkManager.PendingAccountLink link = TradeCraft.accountLinkManager().generate();
                    exchange.getResponseSender().send(link.getCode());
                })
                .add("POST", "/link/finish", exchange -> {
                    exchange.getRequestReceiver().receiveFullString(new Receiver.FullStringCallback() {
                        @Override
                        public void handle(HttpServerExchange h, String s) {
                            AccountLinkManager.PendingAccountLink link = TradeCraft.accountLinkManager().collect(s);
                            if (link == null) {
                                exchange.setStatusCode(400);
                                exchange.getResponseSender().send("Code expired or not activated. Please refresh the page and try again.");
                                exchange.getResponseSender().close();
                                return;
                            }

                            // Find user, otherwise create one
                            String playerUUID = link.getUuid().toString();
                            Player onlinePlayer = TradeCraft.singleton().getServer().getPlayer(UUID.fromString(playerUUID));
                            User user = User.findOrInitialiseUser(playerUUID, onlinePlayer);

                            // Send a message to a player if they're online
                            if (onlinePlayer != null) {
                                onlinePlayer.sendMessage(Component.text("Successfully linked your accounts!").color(TextColor.color(0, 255, 0)));
                            }

                            storage.save(sessionToken, "userID", user.getId().toString());
                            exchange.setStatusCode(200);
                            exchange.getResponseSender().close();
                        }
                    });
                })
                // 404 handler
                .setFallbackHandler(exchange -> {
                    exchange.setStatusCode(404);
                    exchange.getResponseSender().close();
                })
                .handleRequest(httpServerExchange);
    }

    private class UsernamePasswordBody {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private User requireUser(HttpServerExchange exchange, String sessionToken) {
        String userID = storage.fetch(sessionToken, "userID");
        if (userID == null) {
            exchange.setStatusCode(403);
            exchange.getResponseSender().close();
            return null;
        }

        User user = User.findUser(userID);
        if (user == null) {
            exchange.setStatusCode(403);
            exchange.getResponseSender().close();
            return null;
        }

        return user;
    }

    public HttpHandler getMiddleware(HttpHandler next) {
        return httpServerExchange -> {
            ArbitraryDataSecurityContext data = new ArbitraryDataSecurityContext();
            httpServerExchange.setSecurityContext(data);
            if (storage != null) {
                Cookie session = httpServerExchange.getRequestCookie("session");
                if (session == null) {
                    session = new CookieImpl("session");
                    session.setValue(storage.genID());
                    session.setMaxAge(TradeCraft.config().getInt("session.cookie.maxAge"));
                    session.setSecure(TradeCraft.config().getBoolean("session.cookie.secure"));
                    session.setPath("/");
                    httpServerExchange.setResponseCookie(session);
                }

                data.setData("sessionToken", session.getValue());
            }
            next.handleRequest(httpServerExchange);
        };
    }
}
