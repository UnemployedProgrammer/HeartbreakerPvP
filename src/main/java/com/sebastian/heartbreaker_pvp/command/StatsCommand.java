package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.PlayerStats;
import com.sebastian.heartbreaker_pvp.StatsGui;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import com.sebastian.heartbreaker_pvp.translations.Language;
import com.sebastian.heartbreaker_pvp.translations.Translations;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StatsCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> heroStatsCommand = Commands.literal("stats")
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
                                OfflinePlayer target = resolvePlayer(ctx);
                                if(target == null) return 1;
                                if(ctx.getSource().getExecutor() instanceof Player executor) {
                                    StatsGui.create(executor, target).open();
                                }
                                return 1;
                            })
            );

    private static OfflinePlayer resolvePlayer(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String selector = StringArgumentType.getString(ctx, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(selector);
        if (target == null || !target.isWhitelisted()) {
            Language lang = Translations.FALLBACK;
            if(ctx.getSource().getExecutor() instanceof Player executor) {
                lang = DataBase.getPlayerData(executor).getLanguage();
            }

            ctx.getSource().getSender().sendMessage(
                    Component.text(Translations.getString(lang, "player_not_found_or_not_whitelisted"), NamedTextColor.RED)
            );
        }
        return target;
    }

    public static void register(HeartbreakerPvP plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(heroStatsCommand.build());
        });
    }
}