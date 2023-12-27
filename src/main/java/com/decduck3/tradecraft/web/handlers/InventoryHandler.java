package com.decduck3.tradecraft.web.handlers;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.User;
import com.decduck3.tradecraft.db.models.VirtualInventoryBack;
import com.decduck3.tradecraft.inventory.VirtualPlayerInventory;
import com.decduck3.tradecraft.web.Router;
import com.decduck3.tradecraft.web.data.WebItemStack;
import com.decduck3.tradecraft.web.session.ArbitraryDataSecurityContext;
import com.google.gson.Gson;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.UUID;

public class InventoryHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        ArbitraryDataSecurityContext data = (ArbitraryDataSecurityContext) httpServerExchange.getSecurityContext();
        String sessionToken = data.getData("sessionToken");
        User user = Router.authenticationHandler.requireUser(httpServerExchange, sessionToken);
        if (user == null) return;
        Handlers.routing()
                .add("GET", "/fetch", exchange -> {
                    VirtualInventoryBack backer = VirtualInventoryBack.findByPlayerUUID(user.getPlayerUUID());
                    OfflinePlayer player = TradeCraft.singleton().getServer().getOfflinePlayer(UUID.fromString(user.getPlayerUUID()));
                    assert backer != null;

                    // Don't modify the player
                    VirtualPlayerInventory inventory = new VirtualPlayerInventory(backer, player);
                    if (player.getPlayer() != null) {
                        inventory.syncBacker();
                    }

                    Gson gson = new Gson();
                    exchange.getResponseSender().send(gson.toJson(Arrays.stream(inventory.getStorageContents()).map(WebItemStack::new).toList()));
                    exchange.getResponseSender().close();
                })
                .add("POST", "/clear", exchange -> {

                })
                // 404 handler
                .setFallbackHandler(exchange -> {
                    exchange.setStatusCode(404);
                    exchange.getResponseSender().close();
                })
                .handleRequest(httpServerExchange);

    }
}
