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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class LanguageChooseGui implements InventoryHolder {

    private static final NamespacedKey LANGUAGE_KEY = new NamespacedKey("heroes", "language");

    private Inventory inv;
    private final Player player;
    private InventoryView view;

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    private LanguageChooseGui(Player plr) {
        this.player = plr;
    }

    public static LanguageChooseGui create(Player player) {
        final LanguageChooseGui languageGui = new LanguageChooseGui(player);
        languageGui.inv = createGui(languageGui);
        return languageGui;
    }

    public LanguageChooseGui open() {
        if(player != null) {
            view = player.openInventory(inv);
        }
        return this;
    }

    public InventoryView getInventoryView() {
        return view;
    }

    private static Inventory createGui(LanguageChooseGui holder) {
        //Create Gui
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text("language"));

        List<Language> languages = Translations.getLanguages().values().stream().toList();
        for (int i = 0; i < languages.size(); i++) {
            Language lang = languages.get(i);
            inv.setItem(i, getItemForLanguage(lang));
        }

        return inv;
    }

    public static void cancelMove(InventoryClickEvent clickEvent) {
        Inventory invTmp = clickEvent.getInventory();
        if(invTmp.getHolder() != null && invTmp.getHolder() instanceof LanguageChooseGui inventory) {
            clickEvent.setCancelled(true);
            ItemStack stack = inventory.inv.getItem(clickEvent.getSlot());
            if(stack == null || stack.isEmpty()) return;
            PlayerDataModel model = DataBase.getPlayerData(inventory.player);
            model.setLanguage(Translations.getLanguageFromCode(stack.getItemMeta().getPersistentDataContainer().get(LANGUAGE_KEY, PersistentDataType.STRING)));
            DataBase.savePlayerData(inventory.player, model);
        }
    }

    public static void cancelEsc(InventoryCloseEvent closeEvent) {
        Inventory invTmp = closeEvent.getInventory();
        if(invTmp.getHolder() != null && invTmp.getHolder() instanceof LanguageChooseGui inventory) {
            inventory.open();
        }
    }

    private static ItemStack getItemForLanguage(Language language) {
        ItemStack item = ItemStack.of(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString(String.valueOf(100))
                .build()
        );
        itemMeta.displayName(Component.text(language.getName()));
        itemMeta.lore(List.of(Component.text("Click to select language.")));
        itemMeta.getPersistentDataContainer().set(LANGUAGE_KEY, PersistentDataType.STRING, language.getCode());
        item.setItemMeta(itemMeta);
        return item;
    }
}