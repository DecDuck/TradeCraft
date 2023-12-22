package com.decduck3.tradecraft.web;

import com.decduck3.tradecraft.TradeCraft;
import io.undertow.Handlers;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class Router {
    public static HttpHandler router() {
        return Handlers.path(exchange -> {
                    // Forward to SPA
                })
                .addPrefixPath("/api/v1/asset",
                        new AssetHandler()
                )
                .addPrefixPath("/api/v1/auth",
                        Handlers.routing()
                        // Add auth routes here
                )
                .addPrefixPath("/api/v1",
                        Handlers.routing()
                )
                ;
    }
}
