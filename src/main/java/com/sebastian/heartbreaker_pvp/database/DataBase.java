package com.sebastian.heartbreaker_pvp.database;

import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class DataBase {
    public static HashMap<String, PlayerDataModel> playerData = new HashMap();

    public static PlayerDataModel getPlayerData(Player player) {
        if (!playerData.containsKey(player.getUniqueId().toString())) {
            HeartbreakerPvP.logger.warning("No DataModel, returning new PlayerDataModel.");
            return new PlayerDataModel();
        } else {
            return (PlayerDataModel)playerData.get(player.getUniqueId().toString());
        }
    }

    public static void savePlayerData(Player player, PlayerDataModel data) {
        PlayerDataModel old = getPlayerData(player).copy();
        if (playerData.containsKey(player.getUniqueId().toString())) {
            playerData.replace(player.getUniqueId().toString(), data);
        } else {
            playerData.put(player.getUniqueId().toString(), data);
        }
    }

    public static void removeEntryAndSaveToFile(Player plr) {
        DataFileComunicator.savePlayerFile(plr, getPlayerData(plr));
        playerData.remove(plr.getUniqueId().toString());
    }
}
