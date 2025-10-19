package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.LanguageChooseGui;
import com.sebastian.heartbreaker_pvp.StatsGui;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;

public class LangaugeCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> languageCommand = Commands.literal("language")
            .executes(ctx -> {
                if(ctx.getSource().getExecutor() instanceof Player executor) {
                    LanguageChooseGui.create(executor).open();
                }
                return 1;
            });

    public static void register(HeartbreakerPvP plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(languageCommand.build());
        });
    }
}