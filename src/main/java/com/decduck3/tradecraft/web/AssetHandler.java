package com.decduck3.tradecraft.web;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.assetunpacker.AssetUnpacker;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.xnio.IoUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class AssetHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if(!AssetUnpacker.unpackReady()){
            httpServerExchange.setStatusCode(500);
            httpServerExchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "text/plain");
            httpServerExchange.getResponseSender().send("server is still unpacking assets");
            return;
        }

        String assetPath = httpServerExchange.getRequestPath().substring("/api/v1/asset".length());
        File assetFile = new File(String.valueOf(Path.of(TradeCraft.unpacker().getUnpackTarget().getAbsolutePath(), TradeCraft.unpacker().getUnpackVersion(), "assets/minecraft/textures", assetPath)));
        if (!assetFile.exists()) {
            httpServerExchange.setStatusCode(404);
            httpServerExchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "text/plain");
            httpServerExchange.getResponseSender().close();
            return;
        }

        HttpHandler assetHandler = exchange -> {
            try (FileInputStream inputStream = new FileInputStream(assetFile)) {
                BufferedImage img = ImageIO.read(inputStream);

                if(exchange.getQueryParameters().containsKey("size")){
                    // Hard limit of 4096x4096
                    int size = Math.min(Integer.parseInt(exchange.getQueryParameters().get("size").getFirst()), 4096);
                    img = toBufferedImage(img.getScaledInstance(size, size, Image.SCALE_FAST));
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(img, "png", output);
                output.writeTo(exchange.getOutputStream());
            }catch (NumberFormatException ex){
                exchange.setStatusCode(400);
                exchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "text/plain");
                exchange.getResponseSender().send("invalid size parameter");
            }
        };

        httpServerExchange.startBlocking();
        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(assetHandler);
        } else {
            assetHandler.handleRequest(httpServerExchange);
        }
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
