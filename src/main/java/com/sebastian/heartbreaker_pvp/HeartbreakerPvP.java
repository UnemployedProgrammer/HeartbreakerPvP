package com.sebastian.heartbreaker_pvp;

import com.sebastian.heartbreaker_pvp.command.DebugCommands;
import com.sebastian.heartbreaker_pvp.command.HeartbreakerPVPCommand;
import com.sebastian.heartbreaker_pvp.command.HeartsGetCommand;
import com.sebastian.heartbreaker_pvp.command.HeroTimeCommand;
import com.sebastian.heartbreaker_pvp.database.DataFileComunicator;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
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
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = (Commands)event.registrar();
            commands.register("heartbreaker_pvp", "Manage HeartbreakerPVP", new HeartbreakerPVPCommand());
            commands.register("pvp_hearts", "View your current heartbreaker hearts", new HeartsGetCommand());
        });
        PacketSender.init(this);
        DebugCommands.register(this);
        HeroTimeCommand.register(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
