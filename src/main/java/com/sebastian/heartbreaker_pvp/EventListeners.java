package com.sebastian.heartbreaker_pvp;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.DataFileComunicator;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListeners implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerDataModel dataModel = DataBase.getPlayerData(event.getPlayer());
        dataModel.setHearts(dataModel.getHearts() - 1);
        if(dataModel.getHearts() == 0) {
            if(ConfigReader.Configuration.configuration != null) {
                if(ConfigReader.Configuration.configuration.getZero_hearts_handling().equals("kick")) {
                    if(ConfigReader.Configuration.configuration.getKick_msg().isEmpty()) {
                        event.getPlayer().kick(MiniMessage.miniMessage().deserialize(ConfigReader.Configuration.configuration.getKick_msg()));
                    } else {
                        event.getPlayer().kick(MiniMessage.miniMessage().deserialize("<color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>"));
                    }
                } else {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DataBase.savePlayerData(event.getPlayer(), DataFileComunicator.readPlayerFile(event.getPlayer()));

        PlayerDataModel dataModel = DataBase.getPlayerData(event.getPlayer());
        dataModel.setHearts(dataModel.getHearts() - 1);
        if(dataModel.getHearts() == 0) {
            if(ConfigReader.Configuration.configuration != null) {
                if(ConfigReader.Configuration.configuration.getZero_hearts_handling().equals("kick")) {
                    if(ConfigReader.Configuration.configuration.getKick_msg().isEmpty()) {
                        event.getPlayer().kick(MiniMessage.miniMessage().deserialize(ConfigReader.Configuration.configuration.getKick_msg()));
                    } else {
                        event.getPlayer().kick(MiniMessage.miniMessage().deserialize("<color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>"));
                    }
                } else {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DataBase.removeEntryAndSaveToFile(event.getPlayer());
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerDataModel dataModel = DataBase.getPlayerData(onlinePlayer);

        }
    }
}
