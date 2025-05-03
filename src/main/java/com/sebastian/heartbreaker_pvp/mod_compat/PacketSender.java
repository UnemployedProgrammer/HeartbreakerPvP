package com.sebastian.heartbreaker_pvp.mod_compat;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.PlayerStats;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.DataFileComunicator;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
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
    private static List<Material> BLOCK_MATERIALS;

    private static final String CHANNEL = "heroes:main";
    private static final String WHITELIST_CHANNEL = "heroes:whitelist";
    private static final String STATS_CHANNEL = "heroes:player_stats";
    private static final String OPEN_SCREEN_STATS_CHANNEL = "heroes:open_stats";
    private static final String TIME_LEFT_CHANNEL = "heroes:time_update";
    private static final String IN_A_FIGHT_CHANNEL = "heroes:heroes:in_fight";
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
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, TIME_LEFT_CHANNEL);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, IN_A_FIGHT_CHANNEL);

        BLOCK_MATERIALS = Arrays.stream(Material.values())
                .filter(material ->
                        material.isBlock() &&
                                !material.isLegacy()
                )
                .collect(Collectors.toList());

        plugin.getLogger().info("Loaded " + BLOCK_MATERIALS.size() + " non-legacy block materials.");
    }

    private void handleOpenStats(@NotNull String channel, @NotNull Player player, byte[] bytes) {
        if (!channel.equals(OPEN_SCREEN_STATS_CHANNEL)) return;

        plugin.getLogger().info("Player opened stats screen, sending info!");
        List<OfflinePlayer> whitelistedPlayers = new ArrayList<>(Bukkit.getWhitelistedPlayers());

        // Asynchron verarbeiten
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            for (OfflinePlayer whitelisted : whitelistedPlayers) {
                PlayerStats stats = collectStats(whitelisted);

                // Zurück zum Hauptthread für Plugin Messages
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sendPlayerStatsPacket(player, stats);
                });

                // Kurze Pause zur Lastverteilung (10ms)
                try { Thread.sleep(10); }
                catch (InterruptedException e) {}
            }
        });
    }

    public PlayerStats collectStats(OfflinePlayer offlinePlayer) {
        String username = offlinePlayer.getName();
        UUID playerId = offlinePlayer.getUniqueId();

        // LIVE-Daten (wenn Spieler online ist)
        Player onlinePlayer = offlinePlayer.isOnline() ? offlinePlayer.getPlayer() : null;

        // ### XP & Level ###
        float experienceProgress = 0;
        int experienceLevel = 0;
        if (onlinePlayer != null) {
            // Echtzeit-Daten vom Online-Spieler
            experienceProgress = onlinePlayer.getExp();
            experienceLevel = onlinePlayer.getLevel();
        } else {
            experienceProgress = -1;
            experienceLevel = -1;
        }

        // ### Gesundheit & Absorption ###
        int health = 0;
        int absorption = 0;
        int maxHealth = 20;
        if (onlinePlayer != null) {
            health = (int) onlinePlayer.getHealth();
            absorption = (int) onlinePlayer.getAbsorptionAmount();
            maxHealth = (int) onlinePlayer.getMaxHealth();
        }

        // ### Statistiken ###
        int kills = onlinePlayer != null
                ? onlinePlayer.getStatistic(Statistic.MOB_KILLS)
                : offlinePlayer.getStatistic(Statistic.MOB_KILLS);

        int deaths = onlinePlayer != null
                ? onlinePlayer.getStatistic(Statistic.DEATHS)
                : offlinePlayer.getStatistic(Statistic.DEATHS);

        int blocksBroken = onlinePlayer != null
                ? calculateTotalBlocksMined(onlinePlayer)
                : calculateTotalBlocksMined(offlinePlayer);

        int blocksPlaced = onlinePlayer != null
                ? calculateTotalBlocksPlaced(onlinePlayer)
                : calculateTotalBlocksPlaced(offlinePlayer);

        int playerKills = onlinePlayer != null
                ? onlinePlayer.getStatistic(Statistic.PLAYER_KILLS)
                : offlinePlayer.getStatistic(Statistic.PLAYER_KILLS);

        // ### Hero Hearts ###
        int heroHearts = getHeroHearts(offlinePlayer); // Eigene Methode mit Offline-Support

        return new PlayerStats(
                username,
                health,
                absorption,
                maxHealth,
                kills,
                deaths,
                heroHearts,
                blocksBroken,
                blocksPlaced,
                playerKills,
                experienceProgress,
                experienceLevel
        );
    }

    private int calculateTotalBlocksMined(OfflinePlayer player) {
        return BLOCK_MATERIALS.stream()
                .mapToInt(material -> player.getStatistic(Statistic.MINE_BLOCK, material))
                .sum();
    }

    private int getHeroHearts(OfflinePlayer plr) {
        // Echtzeit-Daten, wenn online
        if (plr.isOnline() && plr.getPlayer() != null) {
            return DataBase.getPlayerData(plr.getPlayer()).getHearts();
        }

        // Offline-Daten aus gespeichertem Zustand
        PlayerDataModel cachedData = DataFileComunicator.readPlayerFile(plr);
        return cachedData != null ? cachedData.getHearts() : 0;
    }

    private int calculateTotalBlocksPlaced(OfflinePlayer player) {
        return BLOCK_MATERIALS.stream()
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

    //TIME LIMIT

    public void sendTimeLeftPacket(Player player, int timeLeft) {
        if (!playersWithMod.contains(player)) {
            return;
        }

        plugin.getLogger().info("Sending time left packet to " + player.getName());

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(timeLeft); // Supports full int range (-1 to 2,147,483,647)
        player.sendPluginMessage(plugin, TIME_LEFT_CHANNEL, buffer.array());
    }

    //FIGHT MECHANIC

    public void sendInFightStatus(Player player, boolean inCombat) {
        if (!playersWithMod.contains(player)) return;

        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) (inCombat ? 1 : 0)); // Boolean → byte conversion
        player.sendPluginMessage(plugin, IN_A_FIGHT_CHANNEL, buffer.array());
    }
}
