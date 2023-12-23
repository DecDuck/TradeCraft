package com.decduck3.tradecraft.web;

import com.decduck3.tradecraft.web.handlers.AssetHandler;
import com.decduck3.tradecraft.web.handlers.AuthenticationHandler;
import com.decduck3.tradecraft.web.handlers.BrandingHandler;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;

public class Router {
    public static AuthenticationHandler authenticationHandler = new AuthenticationHandler();
    public static HttpHandler router() {
        return authenticationHandler.getMiddleware(Handlers.path(exchange -> {
                    // Forward to SPA
                })
                .addPrefixPath("/api/v1/asset",
                        new AssetHandler()
                )
                .addPrefixPath("/api/v1/branding",
                        httpServerExchange -> {
                            // Force IO thread
                            BrandingHandler handler = new BrandingHandler();
                            httpServerExchange.startBlocking();
                            if (httpServerExchange.isInIoThread()) {
                                httpServerExchange.dispatch(handler);
                            } else {
                                handler.handleRequest(httpServerExchange);
                            }
                        }
                )
                .addPrefixPath("/api/v1/auth",
                        authenticationHandler
                        // Add auth routes here
                )
                .addPrefixPath("/api/v1",
                        Handlers.routing()
                ));
    }
}
