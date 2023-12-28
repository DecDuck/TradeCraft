package com.decduck3.tradecraft.web.handlers;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class MarketHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        Handlers.routing()
                // Fetch all listings (with pagination and stuff)
                .add("GET", "/l/all", exchange -> {

                })
                // Fetch specific listing, with more information
                .add("GET", "/l/fetch", exchange -> {

                })
                // Fetch all vendors (with pagination again)
                .add("GET", "/v/all", exchange -> {

                })
                // Fetch specific vendor
                .add("GET", "/v/fetch", exchange -> {

                })
                // 404 handler
                .setFallbackHandler(exchange -> {
                    exchange.setStatusCode(404);
                    exchange.getResponseSender().close();
                })
                .handleRequest(httpServerExchange);
    }
}
