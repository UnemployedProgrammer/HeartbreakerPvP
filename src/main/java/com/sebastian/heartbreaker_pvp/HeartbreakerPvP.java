package com.sebastian.heartbreaker_pvp;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class HeartbreakerPvP extends JavaPlugin {

    public static Logger logger;
    public static File dataFolder;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        dataFolder = getDataFolder();
        getServer().getPluginManager().registerEvents(new EventListeners(), this);
        ConfigReader.Configuration.init_reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
