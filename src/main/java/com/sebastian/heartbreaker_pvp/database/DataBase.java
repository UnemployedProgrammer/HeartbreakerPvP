package com.sebastian.heartbreaker_pvp.database;

import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import java.util.HashMap;
import java.util.function.Consumer;

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
        if (playerData.containsKey(player.getUniqueId().toString())) {
            playerData.replace(player.getUniqueId().toString(), data);
        } else {
            playerData.put(player.getUniqueId().toString(), data);
        }
    }

    public static void modifyValueEvenIfOffline(OfflinePlayer plr, Consumer<PlayerDataModel> dataModelConsumer) {
        if(playerData.containsKey(plr.getUniqueId().toString())) {
            //Player is online
            PlayerDataModel model = playerData.get(plr.getUniqueId().toString());
            dataModelConsumer.accept(model);
            if (playerData.containsKey(plr.getUniqueId().toString())) {
                playerData.replace(plr.getUniqueId().toString(), model);
            } else {
                playerData.put(plr.getUniqueId().toString(), model);
            }
        } else {
            //Player is offline
            PlayerDataModel model = DataFileComunicator.readPlayerFile(plr);
            dataModelConsumer.accept(model);
            DataFileComunicator.savePlayerFile(plr, model);
        }
    }

    public static void modifyValueEvenIfOffline(Player plr, Consumer<PlayerDataModel> dataModelConsumer) {
        if(playerData.containsKey(plr.getUniqueId().toString())) {
            //Player is online
            PlayerDataModel model = playerData.get(plr.getUniqueId().toString());
            dataModelConsumer.accept(model);
            if (playerData.containsKey(plr.getUniqueId().toString())) {
                playerData.replace(plr.getUniqueId().toString(), model);
            } else {
                playerData.put(plr.getUniqueId().toString(), model);
            }
        } else {
            //Player is offline
            PlayerDataModel model = DataFileComunicator.readPlayerFile(plr);
            dataModelConsumer.accept(model);
            DataFileComunicator.savePlayerFile(plr, model);
        }
    }

    public static void removeEntryAndSaveToFile(Player plr) {
        DataFileComunicator.savePlayerFile(plr, getPlayerData(plr));
        playerData.remove(plr.getUniqueId().toString());
    }
}
