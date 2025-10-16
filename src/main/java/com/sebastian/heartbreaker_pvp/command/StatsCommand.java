package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.time_limit.TimeLimitManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StatsCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> heroStatsCommand = Commands.literal("hero_stats")
            .then(
                    Commands.argument("player", StringArgumentType.word())
                            .suggests((CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) -> {
                                for (OfflinePlayer p : Bukkit.getWhitelistedPlayers()) {
                                    if (p.getName() != null && p.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                        builder.suggest(p.getName());
                                    }
                                }
                                return CompletableFuture.completedFuture(builder.build());
                            })
                            .executes(ctx -> {
                                OfflinePlayer player = resolvePlayer(ctx);
                                if(player == null) return 1;



                                return 1;
                            })
            );

    private static OfflinePlayer resolvePlayer(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String selector = StringArgumentType.getString(ctx, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(selector);
        if (target == null || !target.isWhitelisted()) {
            ctx.getSource().getSender().sendMessage(
                    Component.text("Player not found or whitelisted!", NamedTextColor.RED)
            );
        }
        return target;
    }

    private static void openGui(Player executor, Player targetPlayer) {

    }

    private static void sendSuccess(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().getSender().sendMessage(
                Component.text(message, NamedTextColor.GREEN)
        );
    }

    public static void register(HeartbreakerPvP plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(heroStatsCommand.build());
        });
    }
}