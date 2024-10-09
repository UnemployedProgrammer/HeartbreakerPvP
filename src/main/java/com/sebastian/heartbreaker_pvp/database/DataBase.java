package com.sebastian.heartbreaker_pvp.database;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class DataBase {

    public static HashMap<String,PlayerDataModel> playerData = new HashMap<>();

    public PlayerDataModel getPlayerData(Player player) {
       if(!playerData.containsKey(player.getUniqueId().toString())) {
           return new PlayerDataModel();
       }

       return playerData.get(player.getUniqueId().toString());
    }

    public void savePlayerData(Player player, PlayerDataModel data) {
        if(playerData.containsKey(player.getUniqueId().toString())) {
            playerData.replace(player.getUniqueId().toString(), data);
        } else {
            playerData.put(player.getUniqueId().toString(), data);
        }
    }

    public void removeEntryAndSaveToFile(Player plr) {

    }
}
