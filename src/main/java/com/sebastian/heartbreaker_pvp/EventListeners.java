package com.sebastian.heartbreaker_pvp;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.sebastian.heartbreaker_pvp.command.HeartbreakerPVPCommand;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.DataFileComunicator;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import com.sebastian.heartbreaker_pvp.time_limit.TimeLimitManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;

public class EventListeners implements Listener {
    public static void triggerDeath(Player player) {
        PlayerDataModel dataModel = DataBase.getPlayerData(player);
        dataModel.setHearts(dataModel.getHearts() - 1, player);
        DataBase.savePlayerData(player, dataModel);
        if (dataModel.getHearts() <= 0 && ConfigReader.Configuration.configuration != null) {
            if (ConfigReader.Configuration.configuration.getZero_hearts_handling().equals("kick")) {
                if (ConfigReader.Configuration.configuration.getKick_msg().isEmpty()) {
                    player.kick(MiniMessage.miniMessage().deserialize(ConfigReader.Configuration.configuration.getKick_msg()));
                } else {
                    player.kick(MiniMessage.miniMessage().deserialize("<color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>"));
                }
            } else {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player) {
            triggerDeath(event.getPlayer());
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DataBase.savePlayerData(event.getPlayer(), DataFileComunicator.readPlayerFile(event.getPlayer()));
        PlayerDataModel dataModel = DataBase.getPlayerData(event.getPlayer());
        PacketSender.getInstance().sendHeartsDecreasedPacket(event.getPlayer(), dataModel.getHearts());
        if (dataModel.getHearts() <= 0 && ConfigReader.Configuration.configuration != null) {
            if (ConfigReader.Configuration.configuration.getZero_hearts_handling().equals("kick")) {
                if (ConfigReader.Configuration.configuration.getKick_msg().isEmpty()) {
                    event.getPlayer().kick(MiniMessage.miniMessage().deserialize(ConfigReader.Configuration.configuration.getKick_msg()));
                } else {
                    event.getPlayer().kick(MiniMessage.miniMessage().deserialize("<color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>"));
                }
            } else {
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DataBase.removeEntryAndSaveToFile(event.getPlayer());
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        Iterator var2 = Bukkit.getOnlinePlayers().iterator();

        while(var2.hasNext()) {
            Player onlinePlayer = (Player)var2.next();
            PlayerDataModel dataModel = DataBase.getPlayerData(onlinePlayer);

            if(!PacketSender.playersWithMod.contains(onlinePlayer)) {
                onlinePlayer.sendActionBar(ActionBarMessageParser.getParsedActionBarMessage(dataModel.getHearts()));
            }

            if (dataModel.getHearts() <= 0 && ConfigReader.Configuration.configuration != null) {
                if (ConfigReader.Configuration.configuration.getZero_hearts_handling().equals("kick")) {
                    if (ConfigReader.Configuration.configuration.getKick_msg().isEmpty()) {
                        onlinePlayer.kick(MiniMessage.miniMessage().deserialize(ConfigReader.Configuration.configuration.getKick_msg()));
                    } else {
                        onlinePlayer.kick(MiniMessage.miniMessage().deserialize("<color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>"));
                    }
                } else {
                    onlinePlayer.setGameMode(GameMode.SPECTATOR);
                }
            }
        }

        TimeLimitManager.serverTick();
    }

    @EventHandler
    public void onInventoryTake(InventoryClickEvent clickEvent) {
        HeartbreakerPVPCommand.cancelMove(clickEvent);
    }
}