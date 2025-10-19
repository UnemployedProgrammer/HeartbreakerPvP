package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.StatsGui;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DebugCommands {
    private static LiteralArgumentBuilder<CommandSourceStack> heroDebugCommand = Commands.literal("herodebug")
            .requires(sender -> sender.getSender().hasPermission("developer.herodebug") || sender.getSender().isOp())
            .then(
                    Commands.literal("getStatsItem")
                            .then(
                                    Commands.argument("id", IntegerArgumentType.integer(1, 100)).executes(ctx -> {
                                        int id = IntegerArgumentType.getInteger(ctx, "id");
                                        if(ctx.getSource().getExecutor() instanceof Player player) {
                                            ItemStack inHand = player.getInventory().getItemInMainHand();
                                            boolean handEmpty = (inHand == null || inHand.getType() == Material.AIR);

                                            if (!handEmpty) {
                                                player.sendMessage("Main Hand not empty");
                                                return 1;
                                            }

                                            // Setze in die Haupt-Hand
                                            ItemStack item = StatsGui.Utils.getStatImageDebugItem(id);
                                            player.getInventory().setItemInMainHand(item);

                                            // Droppe das Item aus der Hand (wir entfernen es aus MainHand, lassen es fallen)
                                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                                            player.getWorld().dropItemNaturally(player.getLocation(), item);

                                            // Setze Pitch auf -45° (Blick nach oben). Yaw belasse wie gehabt
                                            Location loc = player.getLocation();
                                            float yaw = loc.getYaw();
                                            float pitch = -45.0f;  // negativ = Blick nach oben
                                            Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), yaw, pitch);
                                            player.teleport(newLoc);

                                            // Führe einen Command aus – z. B. als Spieler selbst
                                            String cmd = "/data get entity @e[type=minecraft:item, sort=nearest, limit=1, distance=..5]";
                                            Bukkit.dispatchCommand(player, cmd);
                                        }
                                        return 1;
                                    })
                            )
            )
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
