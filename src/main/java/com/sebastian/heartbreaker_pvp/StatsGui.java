package com.sebastian.heartbreaker_pvp;

import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import com.sebastian.heartbreaker_pvp.translations.Language;
import com.sebastian.heartbreaker_pvp.translations.Translations;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StatsGui implements InventoryHolder {

    private static final NamespacedKey PREFER_MOD_STATS_KEY = new NamespacedKey("heroes", "prefer_mod_stats");

    private Inventory inv;
    private final Player player;
    private InventoryView view;

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    private StatsGui(Player plr) {
        this.player = plr;
    }

    public static StatsGui create(Player player, OfflinePlayer target) {
        final StatsGui statsGui = new StatsGui(player);
        statsGui.inv = createGui(player, target, statsGui);
        return statsGui;
    }

    public StatsGui open() {
        if(player != null) {
            view = player.openInventory(inv);
        }
        return this;
    }

    public InventoryView getInventoryView() {
        return view;
    }

    private static Inventory createGui(Player executor, OfflinePlayer targetPlayer, StatsGui holder) {
        //Read data
        PlayerDataModel executorData = DataBase.getPlayerData(executor);
        Language executorLanguage = executorData.getLanguage();
        PlayerStats targetPlayerStats = PacketSender.getInstance().collectStats(targetPlayer);

        //Create Gui
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text(Translations.getString(executorLanguage, "stats_for_player", targetPlayer.getName())));

        //Header
        inv.setItem(4, getHeadWithSecretData(targetPlayer));

        //Stats
        inv.setItem(10, getStatItemThreeValues(executorLanguage, "current_health", 1, targetPlayerStats.health(), targetPlayerStats.maxHealth(), targetPlayerStats.absorptionHealth(), targetPlayer));
        inv.setItem(11, getStatItem(executorLanguage, "hero_hearts", 2, targetPlayerStats.heroHearts(), targetPlayer));
        inv.setItem(14, getStatItemTwoValues(executorLanguage, "experience", 3, targetPlayerStats.experienceLevel(), (int) (targetPlayerStats.experienceProgress() * 100), targetPlayer));
        inv.setItem(12, getStatItemTwoValues(executorLanguage, "kills", 4, targetPlayerStats.kills(), targetPlayerStats.playerKills(), targetPlayer));
        inv.setItem(13, getStatItem(executorLanguage, "deaths", 5, targetPlayerStats.deaths(), targetPlayer));
        inv.setItem(15, getStatItem(executorLanguage, "blocks_placed", 6, targetPlayerStats.blocksPlaced(), targetPlayer));
        inv.setItem(16, getStatItem(executorLanguage, "blocks_destroyed", 7, targetPlayerStats.blocksBroken(), targetPlayer));

        //Footer
        inv.setItem(22, getCloseButton(executorLanguage));

        return inv;
    }

    public static void cancelMove(InventoryClickEvent clickEvent) {
        Inventory invTmp = clickEvent.getInventory();
        if(invTmp.getHolder() != null && invTmp.getHolder() instanceof StatsGui inventory) {
            clickEvent.setCancelled(true);
            if(clickEvent.getSlot() == 22) {
                inventory.getInventoryView().close();
                Sound advSound = Sound.sound(Key.key("minecraft", "entity.item_frame.remove_item"),
                        Sound.Source.MASTER, 1.0f, 1.0f);
                inventory.player.playSound(advSound);
            }
        }
    }

    private static ItemStack getHeadWithSecretData(OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta)item.getItemMeta();
        skull.displayName(Component.text(player.getName() != null ? player.getName() : "???"));
        skull.setOwningPlayer(player);
        skull.lore(List.of());
        skull.getPersistentDataContainer().set(PREFER_MOD_STATS_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(skull);
        return item;
    }

    private static ItemStack getCloseButton(Language language) {
        ItemStack item = ItemStack.of(Material.ARROW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Translations.getComponent(language, "close"));
        itemMeta.lore(List.of());
        item.setItemMeta(itemMeta);
        return item;
    }

    private static ItemStack getStatItem(Language language, String id, int iconId, int value, OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString(String.valueOf(iconId))
                .build()
        );
        itemMeta.displayName(Translations.getComponent(language, id));
        itemMeta.lore(Translations.getComponents(language, id + ".desc", player.getName() != null ? player.getName() : "???", value));
        item.setItemMeta(itemMeta);
        return item;
    }

    private static ItemStack getStatItemTwoValues(Language language, String id, int iconId, int value, int value2, OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString(String.valueOf(iconId))
                .build()
        );
        itemMeta.displayName(Translations.getComponent(language, id));
        itemMeta.lore(Translations.getComponents(language, id + ".desc", player.getName() != null ? player.getName() : "???", value, value2));
        item.setItemMeta(itemMeta);
        return item;
    }

    private static ItemStack getStatItemThreeValues(Language language, String id, int iconId, int value, int value2, int value3, OfflinePlayer player) {
        ItemStack item = ItemStack.of(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString(String.valueOf(iconId))
                .build()
        );
        itemMeta.displayName(Translations.getComponent(language, id));
        itemMeta.lore(Translations.getComponents(language, id + ".desc", player.getName() != null ? player.getName() : "???", value, value2, value3));
        item.setItemMeta(itemMeta);
        return item;
    }

    public static class Utils {
        public static String translateTitle(String val) {
            return "<yellow><b>" + val +"</b></yellow>";
        }

        public static ItemStack getStatImageDebugItem(int iconId) {
            ItemStack item = ItemStack.of(Material.BARRIER);
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                    .addString(String.valueOf(iconId))
                    .build()
            );
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.displayName(Component.text("ABC"));
            itemMeta.lore(List.of(Component.text("ABCDEFGHIJKLMNOPQRSTUVWXYZ")));
            item.setItemMeta(itemMeta);
            return item;
        }
    }
}