package com.sebastian.heartbreaker_pvp.time_limit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Settings {
    private static File configFile;
    private static FileConfiguration config;

    public static void load(JavaPlugin plugin) {
        try {
            // Ensure data folder exists
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            // Initialize config file
            configFile = new File(plugin.getDataFolder(), "settings.yml");
            if (!configFile.exists()) {
                plugin.saveResource("settings.yml", false);
            }

            // Load configuration
            config = YamlConfiguration.loadConfiguration(configFile);

            // Read values or set defaults
            TimeLimitManager.GLOBAL_TIME_LIMIT = config.getInt("global-time-limit", 300);
            TimeLimitManager.GLOBAL_TIME_LIMIT_ENABLED = config.getBoolean("global-time-limit-enabled", true);

            plugin.getLogger().info("Loaded configuration settings");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load configuration: " + e.getMessage());
        }
    }

    public static void save(JavaPlugin plugin) {
        try {
            // Set current values
            config.set("global-time-limit", TimeLimitManager.GLOBAL_TIME_LIMIT);
            config.set("global-time-limit-enabled", TimeLimitManager.GLOBAL_TIME_LIMIT_ENABLED);

            // Save to file
            config.save(configFile);
            plugin.getLogger().info("Saved configuration settings");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save configuration: " + e.getMessage());
        }
    }
}