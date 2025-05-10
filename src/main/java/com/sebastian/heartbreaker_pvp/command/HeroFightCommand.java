package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class HeroFightCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> heroFightCommand = Commands.literal("herofight")
            .requires(sender -> sender.getSender().hasPermission("herofight.use") || sender.getSender().isOp())
            .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(sender -> sender.getSender().hasPermission("herofight.manage") || sender.getSender().isOp())
                    .then(Commands.literal("cancelfight")
                            .executes(ctx -> {
                                Player target = resolvePlayer(ctx);
                                if (target == null) return Command.SINGLE_SUCCESS;

                                // Add fight cancellation logic here

                                DataBase.modifyValueEvenIfOffline(target, model -> {
                                    model.setInAFight(false, target);
                                    model.setStillInAFightFor(30, target);
                                });

                                sendSuccess(ctx, "Cancelled fight for " + target.getName());
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(Commands.literal("fighting")
                            .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                                    .executes(ctx -> {
                                        Player target = resolvePlayer(ctx);
                                        if (target == null) return Command.SINGLE_SUCCESS;

                                        int duration = IntegerArgumentType.getInteger(ctx, "duration");
                                        // Add fight duration logic here

                                        DataBase.modifyValueEvenIfOffline(target, model -> {
                                            model.setStillInAFightFor(30, target);
                                            model.setInAFight(true, target);
                                        });

                                        sendSuccess(ctx, "Set " + target.getName() + "'s fight duration to " + duration + " seconds & set to fighting");
                                        return Command.SINGLE_SUCCESS;
                                    }))));

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
            commands.registrar().register(heroFightCommand.build());
        });
    }
}