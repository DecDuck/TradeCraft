package com.decduck3.tradecraft.utils;

import com.decduck3.tradecraft.TradeCraft;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bukkit.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetUnpacker {
    public static boolean UNPACK_READY = false;
    private Server server;
    private File unpackTarget;

    public AssetUnpacker(Server server) {
        this.server = server;
        unpackTarget = new File(TradeCraft.dataFolder(), "pack");
    }

    public String getUnpackVersion(){
        // Load settings
        String version = TradeCraft.config().getString("forceUnpackVersion");
        if (version == null) {
            version = server.getMinecraftVersion();
        }
        return version;
    }

    public void prepareUnpack(){
        if(unpackExists()){
            UNPACK_READY = true;
            return;
        }
        startUnpack();
    }

    public boolean unpackExists() {
        String version = getUnpackVersion();
        File localUnpackTarget = new File(unpackTarget, version);
        return localUnpackTarget.exists();
    }

    public void startUnpack() {
        // Load settings
        String version = getUnpackVersion();

        File localUnpackTarget = new File(unpackTarget, version);
        localUnpackTarget.mkdirs();

        String clientJarLocation;
        try (CloseableHttpClient http = HttpClientBuilder.create().build()) {
            // Load version manifest
            HttpGet vmRq = new HttpGet("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            vmRq.addHeader("content-type", "application/json"); // Just in case!
            HttpResponse vmRsp = http.execute(vmRq);

            // Parse JSON
            Gson gson = new Gson();
            VersionManifestResponse vmResponse = gson.fromJson(EntityUtils.toString(vmRsp.getEntity(), "UTF-8"),
                    VersionManifestResponse.class);

            // Find version entry that we care about
            String finalVersion = version;
            // Filter, grab first, cast
            VersionManifestResponse.VersionManifestEntry entry = (VersionManifestResponse.VersionManifestEntry) vmResponse.versions.stream().filter(e -> Objects.equals(e.id, finalVersion)).toArray()[0];
            if(entry == null){
                TradeCraft.logger().severe("Could not find viable assets for current version (or override version)");
                return;
            }

            HttpGet vdRq = new HttpGet(entry.url);
            vdRq.addHeader("content-type", "application/json"); // Just in case!
            HttpResponse vdRsp = http.execute(vdRq);

            VersionDigestResponse vdResponse = gson.fromJson(EntityUtils.toString(vdRsp.getEntity(), "UTF-8"), VersionDigestResponse.class);
            clientJarLocation = vdResponse.downloads.client.url;
        } catch (IOException ex) {
            TradeCraft.logger().severe(ex.getLocalizedMessage());
            return;
        }

        TradeCraft.logger().info("Found client.jar location for Minecraft, now downloading & unpacking.");

        // Create a buffer to improve copy performance later.
        byte[] buffer = new byte[2048];

        // ZipInputStream the download
        try (CloseableHttpClient http = HttpClientBuilder.create().build()) {
            HttpGet clientJarRq = new HttpGet(clientJarLocation);
            HttpResponse clientJarResp = http.execute(clientJarRq);

            ZipInputStream jar = new ZipInputStream(clientJarResp.getEntity().getContent());

            ZipEntry entry;
            while((entry = jar.getNextEntry()) != null){
                if(entry.isDirectory()){
                    continue;
                }
                if(!(entry.getName().startsWith("assets/minecraft/lang"))){
                    continue;
                }
                File target = new File(localUnpackTarget, entry.getName().substring("assets/minecraft".length()));
                target.getParentFile().mkdirs();
                target.createNewFile();
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(target);
                    int len;
                    while((len = jar.read(buffer)) > 0){
                        output.write(buffer, 0, len);
                    }
                    // TradeCraft.logger().info(String.format("Exported %s", entry.getName()));
                }finally {
                    if(output != null){
                        output.close();
                    }
                }
            }
        }catch (IOException ex){
            TradeCraft.logger().severe(ex.getLocalizedMessage());
            return;
        }

        TradeCraft.logger().info("Completed asset unpack!");

        UNPACK_READY = true;
    }

    // Only values we care about - there's more
    private class VersionManifestResponse {
        public List<VersionManifestEntry> versions;

        @Override
        public String toString() {
            return String.format("versions: {%s}", String.join(", ", versions.stream().map(Object::toString).toList()));
        }

        // Only two values we care about
        private class VersionManifestEntry {
            public String id;
            public String url;

            @Override
            public String toString() {
                return String.format("(%s)", id);
            }
        }
    }

    // Only values we care about - there's more
    private class VersionDigestResponse {
        public VersionDigestDownloads downloads;

        public class VersionDigestDownloads {
            public VersionDigestDownload client;
            public VersionDigestDownload client_mappings;
            public VersionDigestDownload server;
            public VersionDigestDownload server_mappings;
            public class VersionDigestDownload {
                public String sha1;
                public int size;
                public String url;
            }
        }

    }

    public File getUnpackTarget() {
        return unpackTarget;
    }

    public static Boolean unpackReady() {
        return UNPACK_READY;
    }
}
