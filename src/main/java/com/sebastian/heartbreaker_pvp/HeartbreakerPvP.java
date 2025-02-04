package com.sebastian.heartbreaker_pvp;

import com.sebastian.heartbreaker_pvp.command.HeartbreakerPVPCommand;
import com.sebastian.heartbreaker_pvp.database.DataFileComunicator;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class HeartbreakerPvP extends JavaPlugin {

    public static Logger logger;
    public static File dataFolder;

    @Override
    public void onEnable() {
        logger = getLogger();
        dataFolder = getDataFolder();
        getServer().getPluginManager().registerEvents(new EventListeners(), (Plugin)this);
        ConfigReader.Configuration.init_reload();
        DataFileComunicator.init(dataFolder);
        LifecycleEventManager<Plugin> manager = getLifecycleManager();
        manager.registerEventHandler((LifecycleEventType) LifecycleEvents.COMMANDS, event -> {
            Commands commands = (Commands)event;
            commands.register("heartbreaker_pvp", "Plugin's command!", (BasicCommand)new HeartbreakerPVPCommand());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
