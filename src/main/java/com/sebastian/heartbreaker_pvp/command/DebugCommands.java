package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class DebugCommands {
    private static LiteralArgumentBuilder<CommandSourceStack> heroDebugCommand = Commands.literal("herodebug")
            .requires(sender -> sender.getSender().hasPermission("developer.herodebug") || sender.getSender().isOp())
            .then(Commands.argument("player", ArgumentTypes.player())
                    .then(Commands.literal("timeLimit")
                            .then(Commands.argument("seconds", IntegerArgumentType.integer(1)) // Minimum of 1 second
                                    .executes(ctx -> {
                                        PlayerSelectorArgumentResolver selector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                        Player target = selector.resolve(ctx.getSource()).getFirst();

                                        if (target == null) {
                                            ctx.getSource().getSender().sendMessage(Component.text("Player not found!", NamedTextColor.RED));
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        int seconds = IntegerArgumentType.getInteger(ctx, "seconds");

                                        if(seconds == -2) {
                                            PacketSender.getInstance().sendTimeLeftPacket(target.getPlayer(), seconds); //Implement Later
                                        } else {
                                            PacketSender.getInstance().sendTimeLeftPacket(target.getPlayer(), seconds);
                                        }

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("inAFight")
                            .then(Commands.argument("state", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        PlayerSelectorArgumentResolver selector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                        Player target = selector.resolve(ctx.getSource()).getFirst();

                                        if (target == null) {
                                            ctx.getSource().getSender().sendMessage(Component.text("Player not found!", NamedTextColor.RED));
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        boolean state = BoolArgumentType.getBool(ctx, "state");

                                        PacketSender.getInstance().sendInFightStatus(target.getPlayer(), state);

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

    public static void register(HeartbreakerPvP plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(heroDebugCommand.build()); // Register the new command
        });
    }

}
