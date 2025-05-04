package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import org.bukkit.entity.Player;

public class HeroTimeCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> heroTimeCommand = Commands.literal("herotime")
            .requires(sender -> sender.getSender().hasPermission("herotime.use") || sender.getSender().isOp())
            // Player-specific commands
            .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(sender -> sender.getSender().hasPermission("herotime.manage") || sender.getSender().isOp())
                    .then(Commands.literal("set")
                            .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                                    .executes(ctx -> {
                                        Player target = resolvePlayer(ctx);
                                        if (target == null) return Command.SINGLE_SUCCESS;

                                        int seconds = IntegerArgumentType.getInteger(ctx, "seconds");
                                        DataBase.getPlayerData(target).setTimeAndSave(target, seconds);
                                        sendSuccess(ctx, "Set " + target.getName() + "'s time to " + seconds + " seconds");
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("add")
                            .then(Commands.argument("seconds", IntegerArgumentType.integer())
                                    .executes(ctx -> {
                                        Player target = resolvePlayer(ctx);
                                        if (target == null) return Command.SINGLE_SUCCESS;

                                        int delta = IntegerArgumentType.getInteger(ctx, "seconds");
                                        int newTime = DataBase.getPlayerData(target).modifyTimeAndSave(target, delta);
                                        sendSuccess(ctx, "Added " + delta + " seconds to " + target.getName() + ". New time: " + newTime);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("remove")
                            .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                                    .executes(ctx -> {
                                        Player target = resolvePlayer(ctx);
                                        if (target == null) return Command.SINGLE_SUCCESS;

                                        int delta = IntegerArgumentType.getInteger(ctx, "seconds");
                                        int newTime = DataBase.getPlayerData(target).modifyTimeAndSave(target, -delta);
                                        sendSuccess(ctx, "Removed " + delta + " seconds from " + target.getName() + ". New time: " + newTime);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("query")
                            .executes(ctx -> {
                                Player target = resolvePlayer(ctx);
                                if (target == null) return Command.SINGLE_SUCCESS;

                                int current = DataBase.getPlayerData(target).getTimeLimit();
                                sendSuccess(ctx, target.getName() + "'s current time: " + current + " seconds");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .then(Commands.literal("pause")
                            .executes(ctx -> {
                                Player target = resolvePlayer(ctx);
                                if (target == null) return Command.SINGLE_SUCCESS;

                                DataBase.getPlayerData(target).setTimerPausedAndSave(target, true);
                                sendSuccess(ctx, "Paused " + target.getName() + "'s timer");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .then(Commands.literal("resume")
                            .executes(ctx -> {
                                Player target = resolvePlayer(ctx);
                                if (target == null) return Command.SINGLE_SUCCESS;

                                DataBase.getPlayerData(target).setTimerPausedAndSave(target, false);
                                sendSuccess(ctx, "Resumed " + target.getName() + "'s timer");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .then(Commands.literal("reset")
                            .executes(ctx -> {
                                Player target = resolvePlayer(ctx);
                                if (target == null) return Command.SINGLE_SUCCESS;

                                DataBase.getPlayerData(target).setTimeLimit(TimeLimitManager.GLOBAL_TIME_LIMIT);
                                sendSuccess(ctx, "Reset " + target.getName() + "'s timer!");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            )
            // Global commands
            .then(Commands.literal("global")
                    .requires(sender -> sender.getSender().hasPermission("herotime.global") || sender.getSender().isOp())
                    .then(Commands.literal("set")
                            .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                                    .executes(ctx -> {
                                        int seconds = IntegerArgumentType.getInteger(ctx, "seconds");
                                        TimeLimitManager.GLOBAL_TIME_LIMIT = seconds;
                                        sendSuccess(ctx, "Set global time limit to " + seconds + " seconds");
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("pause")
                            .executes(ctx -> {
                                TimeLimitManager.GLOBAL_TIME_LIMIT_ENABLED = false;
                                sendSuccess(ctx, "Paused global timer");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .then(Commands.literal("resume")
                            .executes(ctx -> {
                                TimeLimitManager.GLOBAL_TIME_LIMIT_ENABLED = true;
                                sendSuccess(ctx, "Resumed global timer, set every player to global time limit left.");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

    private static Player resolvePlayer(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PlayerSelectorArgumentResolver selector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        Player target = selector.resolve(ctx.getSource()).getFirst();
        if (target == null) {
            ctx.getSource().getSender().sendMessage(
                    Component.text("Player not found!", NamedTextColor.RED)
            );
        }
        return target;
    }

    private static void sendSuccess(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().getSender().sendMessage(
                Component.text(message, NamedTextColor.GREEN)
        );
    }

    public static void register(HeartbreakerPvP plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(heroTimeCommand.build());
        });
    }
}