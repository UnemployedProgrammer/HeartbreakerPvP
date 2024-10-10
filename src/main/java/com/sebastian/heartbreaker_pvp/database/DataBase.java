package com.sebastian.heartbreaker_pvp.database;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class DataBase {

    public static HashMap<String,PlayerDataModel> playerData = new HashMap<>();

    public static PlayerDataModel getPlayerData(Player player) {
       if(!playerData.containsKey(player.getUniqueId().toString())) {
           return new PlayerDataModel();
       }

       return playerData.get(player.getUniqueId().toString());
    }

    public static void savePlayerData(Player player, PlayerDataModel data) {
        if(playerData.containsKey(player.getUniqueId().toString())) {
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
