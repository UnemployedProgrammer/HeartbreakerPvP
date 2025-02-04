package com.sebastian.heartbreaker_pvp.command;

import com.sebastian.heartbreaker_pvp.ConfigReader;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class HeartbreakerPVPCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (args[0].equals("ui")) {
            Entity entity = commandSourceStack.getExecutor();
            if (entity instanceof Player) {
                Player player = (Player)entity;
                showGUI(player);
            }
        } else if (args[0].equals("reload")) {
            ConfigReader.Configuration.init_reload();
        } else {
            commandSourceStack.getSender().sendMessage("Usage: /heartbreaker_pvp ui/reload_config");
        }
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("minecraft.command.data");
    }

    public static ItemStack getHead(Player player, boolean bedrock) {
        int lifePlayer = (int)player.getHealth();
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
        SkullMeta skull = (SkullMeta)item.getItemMeta();
        skull.setDisplayName(player.getName());
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Hearts: " + DataBase.getPlayerData(player).getHearts());
        lore.add(bedrock ? "Left Click: Add Heart | Shift Click: Remove Heart" : "Left Click: Add Heart | Right Click: Remove Heart");
        skull.setLore(lore);
        skull.setOwner(player.getName());
        item.setItemMeta((ItemMeta)skull);
        return item;
    }

    public static ItemStack getBedrockUIButton() {
        ItemStack item = new ItemStack(Material.BEDROCK, 1, (short)3);
        ItemMeta skull = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Click to show bedrock compatible UI.");
        skull.setLore(lore);
        item.setItemMeta((ItemMeta)skull);
        return item;
    }

    public static ItemStack getJavaUIButton() {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK, 1, (short)3);
        ItemMeta skull = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Click to show best use-able UI for java.");
        skull.setLore(lore);
        item.setItemMeta((ItemMeta)skull);
        return item;
    }

    public static void showGUI(Player plr) {
        Inventory inv = Bukkit.createInventory(null, Math.clamp(Bukkit.getOnlinePlayers().size(), 26, 80) + 1, "HeartbreakerPvP: Manage Hearts");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            inv.addItem(new ItemStack[] { getHead(onlinePlayer, false) });
        }
        inv.setItem(inv.getSize() - 1, getBedrockUIButton());
        plr.openInventory(inv);
    }

    public static void showBedrockGUI(Player plr) {
        Inventory inv = Bukkit.createInventory(null, Math.clamp(Bukkit.getOnlinePlayers().size(), 26, 80) + 1, "HeartbreakerPvP: Bedrock UI");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            inv.addItem(new ItemStack[] { getHead(onlinePlayer, true) });
        }
        inv.setItem(inv.getSize() - 1, getJavaUIButton());
        plr.openInventory(inv);
    }

    public static void cancelMove(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (view.getTitle().equals("HeartbreakerPvP: Manage Hearts")) {
            if(event.getCurrentItem().getType().equals(Material.BEDROCK)) {
                showBedrockGUI((Player) view.getPlayer());
                return;
            }
            if (event.isLeftClick()) {
                PlayerDataModel model = DataBase.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
                model.setHearts(model.getHearts() + 1);
                DataBase.savePlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()), model);
            }
            if (event.isRightClick()) {
                PlayerDataModel model = DataBase.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
                model.setHearts(model.getHearts() - 1);
                DataBase.savePlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()), model);
            }
            view.close();
            showGUI((Player)view.getPlayer());
            event.setCancelled(true);
        }
        if (view.getTitle().equals("HeartbreakerPvP: Bedrock UI")) {
            if(event.getCurrentItem().getType().equals(Material.GRASS_BLOCK)) {
                showGUI((Player) view.getPlayer());
                return;
            }
            if (event.isLeftClick()) {
                PlayerDataModel model = DataBase.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
                model.setHearts(model.getHearts() + 1);
                DataBase.savePlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()), model);
            }
            if (event.isShiftClick()) {
                PlayerDataModel model = DataBase.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
                model.setHearts(model.getHearts() - 1);
                DataBase.savePlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()), model);
            }
            view.close();
            showGUI((Player)view.getPlayer());
            event.setCancelled(true);
        }
    }
}