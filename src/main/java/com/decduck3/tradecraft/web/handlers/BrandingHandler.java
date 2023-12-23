package com.decduck3.tradecraft.web.handlers;

import com.decduck3.tradecraft.TradeCraft;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.*;

public class BrandingHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String path = httpServerExchange.getRequestPath().substring("/api/v1/branding".length());
        InputStream finalStream = null;
        // Handle banner
        if (path.startsWith("/banner")) {
            File userProvided = new File(TradeCraft.dataFolder(), "web/banner.png");
            if (userProvided.exists()) {
                finalStream = new FileInputStream(userProvided);
            }else{
                // Send the default one
                finalStream = TradeCraft.singleton().getResource("web/banner.png");
            }
        }
        // Handle favicon
        if (path.startsWith("/favicon")) {
            File userProvided = new File(TradeCraft.dataFolder(), "web/favicon.png");
            if (userProvided.exists()) {
                finalStream = new FileInputStream(userProvided);
            }else{
                // Send the default one
                finalStream = TradeCraft.singleton().getResource("web/favicon.png");
            }
        }
        // Handle web config
        if (path.startsWith("/config")) {
            File userProvided = new File(TradeCraft.dataFolder(), "web/config.json");
            if (userProvided.exists()) {
                finalStream = new FileInputStream(userProvided);
            }else{
                // Send the default one
                finalStream = TradeCraft.singleton().getResource("web/config.json");
            }
            httpServerExchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "application/json");
        }
        
        if(finalStream == null){
            httpServerExchange.setStatusCode(404);
            return;
        }

        final OutputStream outputStream = httpServerExchange.getOutputStream();

        byte[] buf = new byte[8192];
        int c;
        while ((c = finalStream.read(buf, 0, buf.length)) > 0) {
            outputStream.write(buf, 0, c);
            outputStream.flush();
        }

        outputStream.close();
        finalStream.close();
    }
}
