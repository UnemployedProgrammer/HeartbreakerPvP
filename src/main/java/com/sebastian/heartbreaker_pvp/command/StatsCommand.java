package com.sebastian.heartbreaker_pvp.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.PlayerStats;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StatsCommand {

    private static NamespacedKey PREFER_MOD_STATS_KEY = new NamespacedKey("heroes", "prefer_mod_stats");

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

    public static class StatsInventoryHolder implements InventoryHolder {
        private Inventory inv;

        public StatsInventoryHolder(Inventory inv) {
            this.inv = inv;
        }

        public StatsInventoryHolder() {}

        @Override
        public @NotNull Inventory getInventory() {
            return inv;
        }

        public void setInv(Inventory inv) {
            this.inv = inv;
        }
    }

    private static void openGui(Player executor, OfflinePlayer targetPlayer) {
        //Read data
        PlayerDataModel executorData = DataBase.getPlayerData(executor);
        Language executorLanguage = executorData.getLanguage();
        PlayerStats targetPlayerStats = PacketSender.getInstance().collectStats(targetPlayer);

        //Create Gui
        StatsInventoryHolder holder = new StatsInventoryHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text(Translations.getString(executorLanguage, "stats_for_player", targetPlayer.getName())));
        holder.setInv(inv);

        //Header
        inv.setItem(4, getHeadWithSecretData(targetPlayer));

        //Stats
        inv.setItem(10, getStatItemThreeValues(executorLanguage, "current_health", 1, targetPlayerStats.health(), targetPlayerStats.maxHealth(), targetPlayerStats.absorptionHealth(), targetPlayer));
        inv.setItem(11, getStatItem(executorLanguage, "hero_hearts", 2, targetPlayerStats.heroHearts(), targetPlayer));
        inv.setItem(12, getStatItemTwoValues(executorLanguage, "kills", 3, targetPlayerStats.kills(), targetPlayerStats.playerKills(), targetPlayer));
        inv.setItem(13, getStatItem(executorLanguage, "deaths", 4, targetPlayerStats.deaths(), targetPlayer));
        inv.setItem(14, getStatItemTwoValues(executorLanguage, "experience", 5, targetPlayerStats.experienceLevel(), (int) (targetPlayerStats.experienceProgress() * 100), targetPlayer));
        inv.setItem(15, getStatItem(executorLanguage, "blocks_placed", 6, targetPlayerStats.blocksPlaced(), targetPlayer));
        inv.setItem(16, getStatItem(executorLanguage, "blocks_broken", 7, targetPlayerStats.blocksBroken(), targetPlayer));

        //Footer
        inv.setItem(22, getCloseButton(executorLanguage));

        executor.openInventory(inv);
    }

    private static void cancelMove() {

    }

    public static ItemStack getHeadWithSecretData(OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta)item.getItemMeta();
        skull.displayName(Component.text(player.getName() != null ? player.getName() : "???"));
        skull.setOwningPlayer(player);
        skull.getPersistentDataContainer().set(PREFER_MOD_STATS_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack getCloseButton(Language language) {
        ItemStack item = ItemStack.of(Material.ARROW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text(Translations.getString(language, "close")));
        itemMeta.lore(List.of());
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getStatItem(Language language, String id, int iconId, int value, OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(iconId);
        itemMeta.displayName(Translations.getComponent(language, id));
        itemMeta.lore(List.of(Translations.getComponent(language, id + ".desc", player.getName() != null ? player.getName() : "???", value)));
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getStatItemTwoValues(Language language, String id, int iconId, int value, int value2, OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(iconId);
        itemMeta.displayName(Translations.getComponent(language, id));
        itemMeta.lore(List.of(Translations.getComponent(language, id + ".desc", player.getName() != null ? player.getName() : "???", value, value2)));
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getStatItemThreeValues(Language language, String id, int iconId, int value, int value2, int value3, OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(iconId);
        itemMeta.displayName(Translations.getComponent(language, id));
        itemMeta.lore(List.of(Translations.getComponent(language, id + ".desc", player.getName() != null ? player.getName() : "???", value, value2, value3)));
        item.setItemMeta(itemMeta);
        return item;
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