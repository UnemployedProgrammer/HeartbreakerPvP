package com.sebastian.heartbreaker_pvp.mod_compat;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.PlayerStats;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.DataFileComunicator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        if (!channel.equals(OPEN_SCREEN_STATS_CHANNEL)) return;
        plugin.getLogger().info("Player opened stats screen, sending info!");
        for (OfflinePlayer whitelistedPlayer : Bukkit.getWhitelistedPlayers()) {
            sendPlayerStatsPacket(player, collectStats(whitelistedPlayer));
        }
    }

    public PlayerStats collectStats(OfflinePlayer offlinePlayer) {
        String username = offlinePlayer.getName();
        UUID playerId = offlinePlayer.getUniqueId();

        // Initialize default values
        float experienceProgress = 0;
        int experienceLevel = 0;
        int health = 0;
        int absorption = 0;
        int maxHealth = 20;

        // Load persistent data that works even when offline
        int kills = offlinePlayer.getStatistic(Statistic.MOB_KILLS);
        int deaths = offlinePlayer.getStatistic(Statistic.DEATHS);
        int blocksBroken = calculateTotalBlocksMined(offlinePlayer);
        int blocksPlaced = calculateTotalBlocksPlaced(offlinePlayer);
        int playerKills = offlinePlayer.getStatistic(Statistic.PLAYER_KILLS);

        // Get live data if player is online
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            experienceProgress = player.getExp();
            experienceLevel = player.getLevel();
            health = (int) player.getHealth();
            absorption = (int) player.getAbsorptionAmount();
            maxHealth = (int) player.getMaxHealth();
        }

        return new PlayerStats(
                username,
                health,
                absorption,
                maxHealth,
                kills,
                deaths,
                getHeroHearts(offlinePlayer),  // Your custom method
                blocksBroken,
                blocksPlaced,
                playerKills,
                experienceProgress,
                experienceLevel
        );
    }

    private int calculateTotalBlocksMined(OfflinePlayer player) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .mapToInt(material -> player.getStatistic(Statistic.MINE_BLOCK, material))
                .sum();
    }

    private int getHeroHearts(OfflinePlayer plr) {
        if(plr.isOnline() && plr.getPlayer() != null) {
            return DataBase.getPlayerData(plr.getPlayer()).getHearts();
        }

        return DataFileComunicator.readPlayerFile(plr).getHearts();
    }

    private int calculateTotalBlocksPlaced(OfflinePlayer player) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .mapToInt(material -> player.getStatistic(Statistic.USE_ITEM, material))
                .sum();
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

            // Write username with VarInt length encoding
            byte[] usernameBytes = stats.username().getBytes(StandardCharsets.UTF_8);
            writeVarInt(dos, usernameBytes.length);
            dos.write(usernameBytes);

            // Write other fields in EXACT order
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

            player.sendPluginMessage(plugin, "heroes:player_stats", bos.toByteArray());
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
