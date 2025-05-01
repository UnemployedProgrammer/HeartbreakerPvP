package com.sebastian.heartbreaker_pvp.mod_compat;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.PlayerStats;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
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
    private static final String STATS_CHANNEL = "heroes:player_stats";
    private static final String OPEN_SCREEN_STATS_CHANNEL = "heroes:open_stats";
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
        plugin.getServer().getMessenger().registerIncomingPluginChannel(
                plugin,
                OPEN_SCREEN_STATS_CHANNEL, // Must match the client's channel
                this::handleOpenStats
        );
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, WHITELIST_CHANNEL);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, STATS_CHANNEL);
    }

    private void handleOpenStats(@NotNull String channel, @NotNull Player player, byte[] bytes) {
        if (!channel.equals("heroes:mod_installed")) return;
        for (OfflinePlayer whitelistedPlayer : Bukkit.getWhitelistedPlayers()) {
            sendPlayerStatsPacket(player, new PlayerStats(whitelistedPlayer.getName(), whitelistedPlayer));
        }
    }

    public PlayerStats collectStats(OfflinePlayer player) {
        int heroHearts = 3;
        if(player.getPlayer() == null) return new PlayerStats(player.getName(), 0, 0, 0, 0, 0, heroHearts, 0, 0, 0, 0F, 0);
        return new PlayerStats(
                player.getName(),
                (int) player.getPlayer().getHealth(),
                (int) player.getPlayer().getAbsorptionAmount(),
                (int) player.getPlayer().getMaxHealth(),
                player.getPlayer().getStatistic(Statistic.KILL_ENTITY),        // Your custom method
                player.getPlayer().getStatistic(Statistic.DEATHS),       // Your custom method
                heroHearts,   // Your custom system
                player.getPlayer().getStatistic(Statistic.MINE_BLOCK),
                player.getPlayer().getStatistic(Statistic.BLOCK),
                getPlayerKills(player),
                player.getExp(),
                player.getLevel()
        );
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

    public void sendPlayerStatsPacket(Player player, PlayerStats stats) {
        if (!playersWithMod.contains(player)) return;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            // Write all fields in EXACTLY the same order as the CODEC
            dos.writeUTF(stats.username());
            dos.writeInt(stats.health());
            dos.writeInt(stats.absorptionHealth());
            dos.writeInt(stats.maxHealth());
            dos.writeInt(stats.kills());
            dos.writeInt(stats.deaths());
            dos.writeInt(stats.heroHearts());
            dos.writeInt(stats.blocksBroken());
            dos.writeInt(stats.blocksPlaced());
            dos.writeInt(stats.playerKills());
            dos.writeFloat(stats.experienceProgress());
            dos.writeInt(stats.experienceLevel());

            player.sendPluginMessage(plugin, STATS_CHANNEL, bos.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to send player stats packet", e);
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
