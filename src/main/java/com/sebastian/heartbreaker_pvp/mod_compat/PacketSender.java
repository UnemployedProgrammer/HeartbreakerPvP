package com.sebastian.heartbreaker_pvp.mod_compat;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PacketSender {

    public static List<Player> playersWithMod = new ArrayList<>();

    private static final String CHANNEL = "heroes:main";
    private static PacketSender INSTANCE;
    private HeartbreakerPvP plugin;

    public static void init(HeartbreakerPvP plugin) {
        INSTANCE = new PacketSender();
        INSTANCE.plugin = plugin;
        INSTANCE.init();
    }

    public void init() {
        //plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(
                plugin,
                "heroes:mod_installed", // Must match the client's channel
                this::handleModInstalledMessage
        );
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
    }

    public static PacketSender getInstance() {
        return INSTANCE;
    }

    public void sendHeartsDecreasedPacket(Player player, int newHearts) {
        if(!playersWithMod.contains(player)) {
            return;
        }
        plugin.getLogger().info("Sending player packet: current hearts to " + player.getName());
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(newHearts); // This uses big-endian by default
        player.sendPluginMessage(plugin, CHANNEL, buffer.array());
    }

    private void handleModInstalledMessage(String channel, Player player, byte[] message) {
        if (!channel.equals("heroes:mod_installed")) return;

        playersWithMod.add(player);

        player.sendActionBar(Component.text("Activated HEROES Mod!"));
        plugin.getLogger().info("Player has the HEROES Mod installed!");

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            sendHeartsDecreasedPacket(player, DataBase.getPlayerData(player).getHearts());
        }, 20);
    }
}
