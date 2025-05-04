package com.sebastian.heartbreaker_pvp.time_limit;

import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TimeLimitManager {
    public static Integer GLOBAL_TIME_LIMIT = 60 * 60;
    public static Boolean GLOBAL_TIME_LIMIT_ENABLED = true;

    private static int secondClock;
    public static void serverTick() {
        secondClock++;
        if(secondClock >= 20) {
            secondClock = 0;
            //Calls Every sec
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerDataModel playerDataModel = DataBase.getPlayerData(onlinePlayer);

                if(playerDataModel.isTimerPaused()) {
                    //Timer Paused
                    PacketSender.getInstance().sendTimeLeftPacket(onlinePlayer, -1);
                    continue;
                }

                if(!playerDataModel.hasTimeLeft()) {

                    if(playerDataModel.getTimeLimit() == -1) {
                        playerDataModel.setTimeLimit(GLOBAL_TIME_LIMIT);
                    } else {
                        onlinePlayer.kick(MiniMessage.miniMessage().deserialize("<red>No time left, see you tomorrow!</red>"));
                    }

                } else {

                    if(!GLOBAL_TIME_LIMIT_ENABLED) {
                        playerDataModel.setTimeLimit(-1);
                    } else {
                        playerDataModel.setTimeLimit(playerDataModel.getTimeLimit() - 1);
                    }

                    PacketSender.getInstance().sendTimeLeftPacket(onlinePlayer, playerDataModel.getTimeLimit());
                }

                DataBase.savePlayerData(onlinePlayer, playerDataModel);
            }
        }
    }
}
