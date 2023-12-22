package com.decduck3.tradecraft.web;

import com.decduck3.tradecraft.TradeCraft;
import io.undertow.Undertow;

public class WebServer {
    private final Undertow server;

    public WebServer() {
        String host = TradeCraft.config().getString("network.host");
        int port = TradeCraft.config().getInt("network.port");

        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(Router.router())
                .build();
        server.start();
        TradeCraft.logger().info(String.format("Started TradeCraft web server on %s:%s", host, port));
    }

    public void destroy() {
        server.stop();
    }
}
