package com.sebastian.heartbreaker_pvp.mod_compat;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PacketSender {

    public static List<Player> playersWithMod = new ArrayList<>();

    private static final String CHANNEL = "heroes:main";
    private static final String WHITELIST_CHANNEL = "heroes:whitelist";
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
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, WHITELIST_CHANNEL);
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

    public void sendWhitelistPacket(Player player) {
        if (!playersWithMod.contains(player)) return;

        List<String> whitelist = Bukkit.getWhitelistedPlayers().stream()
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            // Write list size as VarInt
            writeVarInt(dos, whitelist.size());

            // Write each name properly
            for (String name : whitelist) {
                byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
                writeVarInt(dos, nameBytes.length);
                dos.write(nameBytes);
            }

            player.sendPluginMessage(plugin, WHITELIST_CHANNEL, bos.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to send whitelist packet", e);
        }
    }

    // Proper VarInt implementation for Paper
    private void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0L) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value & 0x7F);
    }

    private void handleModInstalledMessage(String channel, Player player, byte[] message) {
        if (!channel.equals("heroes:mod_installed")) return;

        playersWithMod.add(player);

        player.sendActionBar(Component.text("Activated HEROES Mod!"));
        plugin.getLogger().info("Player has the HEROES Mod installed!");

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            sendHeartsDecreasedPacket(player, DataBase.getPlayerData(player).getHearts());
            sendWhitelistPacket(player);
        }, 20);
    }
}
