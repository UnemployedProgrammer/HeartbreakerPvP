package com.sebastian.heartbreaker_pvp;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.sebastian.heartbreaker_pvp.command.HeartbreakerPVPCommand;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.DataFileComunicator;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import com.sebastian.heartbreaker_pvp.fight.FightManager;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import com.sebastian.heartbreaker_pvp.time_limit.Settings;
import com.sebastian.heartbreaker_pvp.time_limit.TimeLimitManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.Iterator;

public class EventListeners implements Listener {
    /*
    public static void triggerHeartLoose(Player player) {
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
     */

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        FightManager.playerDies(event);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        FightManager.playerDamage(event);
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
        FightManager.playerQuit(event);
        DataBase.removeEntryAndSaveToFile(event.getPlayer());
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        TimeLimitManager.serverTick();
        FightManager.serverTick();
        Iterator var2 = Bukkit.getOnlinePlayers().iterator();

        while(var2.hasNext()) {
            Player onlinePlayer = (Player)var2.next();
            PlayerDataModel dataModel = DataBase.getPlayerData(onlinePlayer);

            if(!PacketSender.playersWithMod.contains(onlinePlayer)) {

                String timeString = formatSecondsToTime(dataModel.getTimeLimit());
                Component actionBarMessage = ActionBarMessageParser.getParsedActionBarMessage(dataModel.getHearts()).append(MiniMessage.miniMessage().deserialize(" <green>|</green> <dark_green>" + timeString + "</dark_green>"));
                if(dataModel.isInAFight()) {
                    actionBarMessage = actionBarMessage.append(MiniMessage.miniMessage().deserialize(" <green>|</green> <red> Kampf: " + dataModel.getStillInAFightFor() + "s </red>"));
                }
                onlinePlayer.sendActionBar(actionBarMessage);
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
    }

    /**
     * Converts seconds to a human-readable time format (e.g., "10h 10m 9s")
     * @param totalSeconds The total number of seconds to convert
     * @return Formatted string in h m s format (omits zero-value units)
     */
    public static String formatSecondsToTime(long totalSeconds) {
        if (totalSeconds < 0) {
            return "0s"; // or throw an exception if you prefer
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder builder = new StringBuilder();

        if (hours > 0) {
            builder.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) { // Show minutes if there are hours, even if 0
            builder.append(minutes).append("m ");
        }
        builder.append(seconds).append("s");

        return builder.toString().trim(); // trim in case of trailing space
    }

    @EventHandler
    public void onInventoryTake(InventoryClickEvent clickEvent) {
        HeartbreakerPVPCommand.cancelMove(clickEvent);
        StatsGui.cancelMove(clickEvent);
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent worldSaveEvent) {
        Settings.save(HeartbreakerPvP.instance);
    }
}