package com.decduck3.tradecraft.web.handlers;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.security.AccountLinkManager;
import com.decduck3.tradecraft.web.Router;
import com.decduck3.tradecraft.web.session.ArbitraryDataSecurityContext;
import com.decduck3.tradecraft.web.session.SessionStorage;
import com.decduck3.tradecraft.web.session.storages.MemorySessionStorage;
import com.google.gson.Gson;
import io.undertow.Handlers;
import io.undertow.io.Receiver;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.HttpString;
import org.bukkit.OfflinePlayer;

public class AuthenticationHandler implements HttpHandler {
    private record AuthFetchUser (String name, String uuid){}

    private SessionStorage storage;
    public AuthenticationHandler(){
        String sessionType = TradeCraft.config().getString("session.type");
        if(sessionType == null){
            storage = new MemorySessionStorage();
        }else{
            switch(sessionType){
                case "memory":
                    storage = new MemorySessionStorage();
                    break;
                // Add more as needed
            }
        }

        if(storage == null){
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
                    String playerUUID = storage.fetch(sessionToken, "playerUUID");
                    if(playerUUID == null){
                        exchange.setStatusCode(403);
                        exchange.getResponseSender().close();
                        return;
                    }
                    OfflinePlayer player = TradeCraft.singleton().getServer().getOfflinePlayer(playerUUID);
                    AuthFetchUser user = new AuthFetchUser(player.getName(), playerUUID);
                    Gson gson = new Gson();

                    exchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(gson.toJson(user));
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
                            if(link == null){
                                exchange.setStatusCode(400);
                                exchange.getResponseSender().send("Code expired or not activated. Please refresh the page and try again.");
                                exchange.getResponseSender().close();
                                return;
                            }
                            storage.save(sessionToken, "playerUUID", link.getUuid().toString());
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

    public HttpHandler getMiddleware(HttpHandler next){
        return httpServerExchange -> {
            ArbitraryDataSecurityContext data = new ArbitraryDataSecurityContext();
            httpServerExchange.setSecurityContext(data);
            if(storage != null){
                Cookie session = httpServerExchange.getRequestCookie("session");
                if(session == null){
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
