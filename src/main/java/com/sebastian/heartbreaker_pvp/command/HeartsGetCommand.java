package com.sebastian.heartbreaker_pvp.command;

import com.sebastian.heartbreaker_pvp.database.DataBase;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

public class HeartsGetCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if(strings.length == 1) {
            if(strings[0].equalsIgnoreCase("raw")) {
                if(commandSourceStack.getExecutor() instanceof Player plr) {
                    commandSourceStack.getSender().sendMessage(String.valueOf(DataBase.getPlayerData(plr).getHearts()));
                }
            }
        } else {
            if(commandSourceStack.getExecutor() instanceof Player plr) {
                commandSourceStack.getSender().sendMessage(String.valueOf(DataBase.getPlayerData(plr).getHearts()));
            }
        }
    }
}
