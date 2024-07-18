package com.decduck3.tradecraft;

import com.decduck3.tradecraft.commands.AuthorizedWebCommand;
import com.decduck3.tradecraft.db.DatabaseManager;
import com.decduck3.tradecraft.inventory.VirtualInventoryListener;
import com.decduck3.tradecraft.security.AccountLinkManager;
import com.decduck3.tradecraft.utils.AssetUnpacker;
import com.decduck3.tradecraft.utils.BundledAssetsDownloader;
import com.decduck3.tradecraft.web.WebServer;
import com.decduck3.tradecraft.web.data.ItemStackSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public final class TradeCraft extends JavaPlugin {
    private WebServer webServer;
    private static Logger logger;
    private static FileConfiguration configuration;
    private static File dataFolder;


    private static AssetUnpacker assetUnpacker;
    private static BundledAssetsDownloader bundledAssetsDownloader;
    private static AccountLinkManager accountLinkManager;
    private static DatabaseManager databaseManager;
    private static TradeCraft singleton;

    public TradeCraft() {
        singleton = this;
        logger = this.getLogger();
        dataFolder = getDataFolder();
        assetUnpacker = new AssetUnpacker(getServer());
        bundledAssetsDownloader = new BundledAssetsDownloader(getServer());
        accountLinkManager = new AccountLinkManager();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        configuration = getConfig();

        // Initialise database
        databaseManager = new DatabaseManager();

        // Setup listener
        getServer().getPluginManager().registerEvents(new VirtualInventoryListener(), this);

        // Start unpacking
        assetUnpacker.prepareUnpack();
        // Download bundle
        bundledAssetsDownloader.prepareBundle();

        // Start web server after config loading and unpacking assets
        ItemStackSerializer.init();
        webServer = new WebServer();

        // At this point, we're technically up for business


        // Start account link manager cleanup
        new BukkitRunnable(){
            @Override
            public void run() {
                accountLinkManager.cleanup();
            }
        }.runTaskTimer(this, 0, 20 * 60 * 5);

        // Register authorize command
        getServer().getCommandMap().register("authorize", "tc", new AuthorizedWebCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (webServer != null) {
            webServer.destroy();
        }
        databaseManager.close();
    }

    @NotNull
    public static Logger logger() {
        return logger;
    }

    public static FileConfiguration config() {
        return configuration;
    }

    public static AssetUnpacker unpacker() {
        return assetUnpacker;
    }

    public static File dataFolder() {
        return dataFolder;
    }

    public static TradeCraft singleton() {
        return singleton;
    }

    public static AccountLinkManager accountLinkManager() {
        return accountLinkManager;
    }

    public static DatabaseManager database() {return databaseManager;}
}
