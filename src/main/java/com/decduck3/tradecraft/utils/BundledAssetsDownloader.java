package com.decduck3.tradecraft.utils;


import com.decduck3.tradecraft.TradeCraft;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bukkit.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BundledAssetsDownloader {
    public static boolean BUNDLE_READY = false;
    private Server server;
    private File bundleTarget;

    public BundledAssetsDownloader(Server server) {
        this.server = server;
        bundleTarget = new File(TradeCraft.dataFolder(), "pack");
    }

    public String getBundleVersion() {
        // Load settings
        String version = TradeCraft.config().getString("forceUnpackVersion");
        if (version == null) {
            version = server.getMinecraftVersion();
        }
        return version;
    }

    public void prepareBundle() {
        if (bundleExists()) {
            TradeCraft.logger().info("Skipped unpack - exists");
            BUNDLE_READY = true;
            return;
        }
        startUnpack();
    }

    public File getBundleTarget() {
        String version = getBundleVersion();
        return new File(bundleTarget, version + "/render");
    }

    public boolean bundleExists() {
        return getBundleTarget().exists();
    }

    public void startUnpack() {
        TradeCraft.logger().info("Downloading render bundle...");
        File localBundleTarget = getBundleTarget();

        String bundleLocation = TradeCraft.config().getString("renderBundleUrl");

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            if(bundleLocation == null){
                bundleLocation = "https://nightly.link/TradeCraftMC/minecraft-render/workflows/ci/master/test-result.zip";
            }
            HttpGet webBundle = new HttpGet(bundleLocation);
            HttpResponse webBundleResponse = client.execute(webBundle);

            ZipEntry entry;
            byte[] buffer = new byte[2048];
            ZipInputStream zip = new ZipInputStream(webBundleResponse.getEntity().getContent());
            while((entry = zip.getNextEntry()) != null){
                if(entry.isDirectory()){
                    continue;
                }
                // Doesn't really catch anything, but better safe than sorry yk
                if(!entry.getName().endsWith(".png")){
                    continue;
                }
                File target = new File(localBundleTarget, entry.getName());
                target.getParentFile().mkdirs();
                target.createNewFile();
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(target);
                    int len;
                    while((len = zip.read(buffer)) > 0){
                        output.write(buffer, 0, len);
                    }
                    // TradeCraft.logger().info(String.format("Exported %s", entry.getName()));
                }finally {
                    if(output != null){
                        output.close();
                    }
                }
            }

        } catch (IOException e) {
            TradeCraft.logger().severe(e.getLocalizedMessage());
            return;
        }

        BUNDLE_READY = true;
    }

    public record Artifact(int id, String archive_download_url, Date created_at) {
    }

    public record ArtifactListResponse(List<Artifact> artifacts, int total_count) {
    }
}
