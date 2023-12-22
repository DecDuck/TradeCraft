package com.decduck3.tradecraft;

import com.decduck3.tradecraft.assetunpacker.AssetUnpacker;
import com.decduck3.tradecraft.web.WebServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class TradeCraft extends JavaPlugin {
    private WebServer webServer;
    private static Logger logger;
    private static FileConfiguration configuration;
    private static AssetUnpacker assetUnpacker;

    public TradeCraft() {
        logger = this.getLogger();
        assetUnpacker = new AssetUnpacker(getServer(), getDataFolder());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        configuration = getConfig();

        // Start unpacking
        if(!assetUnpacker.unpackExists()){
            assetUnpacker.startUnpack();
        }else{
            TradeCraft.logger().info("Skipping unpack (already exists)");
        }

        // Start web server after config loading
        webServer = new WebServer();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(webServer != null){
            webServer.destroy();
        }
    }

    @NotNull
    public static Logger logger() {
        return logger;
    }

    public static FileConfiguration config() {
        return configuration;
    }

    public static AssetUnpacker unpacker() {return assetUnpacker;}
}
