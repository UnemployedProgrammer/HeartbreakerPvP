package com.sebastian.heartbreaker_pvp.fight;

import com.sebastian.heartbreaker_pvp.Pair;
import com.sebastian.heartbreaker_pvp.database.DataBase;
import com.sebastian.heartbreaker_pvp.database.PlayerDataModel;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class FightManager {

    public static String IN_FIGHT_LEFT = "<gold>Helden »</gold> <yellow>%s</yellow> war im Kampf und hat das Spiel wärenddessen verlassen! <red>(-1 ❤)</red>";
    public static String IN_FIGHT_DIED = "<gold>Helden »</gold> <yellow>%s</yellow> ist im Kampf gestorben! <red>(-1 ❤)</red>";
    public static String NOW_IN_FIGHT = "<gold>Helden »</gold> <red>Du bist jetzt im Kampf!</red>"; //Needs Change
    public static String NOT_IN_FIGHT_ANYMORE = "<gold>Helden »</gold> <green>Du bist jetzt nicht mehr im Kampf!</green>"; //Needs Change

    private static int secondClock;
    public static void serverTick() {
        secondClock++;
        if(secondClock >= 20) {
            secondClock = 0;
            //Calls every sec
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerDataModel plr = DataBase.getPlayerData(onlinePlayer);
                if (plr.isInAFight()) {
                    if (plr.getStillInAFightFor() <= 0) {
                        plr.setInAFight(false);
                        plr.setStillInAFightForAndSave(30, onlinePlayer);
                        PacketSender.getInstance().sendInFightStatus(onlinePlayer, false);
                        onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize(NOT_IN_FIGHT_ANYMORE));
                    }
                    plr.setStillInAFightForAndSave(plr.getStillInAFightFor() - 1, onlinePlayer);
                }
            }
        }
    }

    public static void playerQuit(PlayerQuitEvent quitEvent) {
        PlayerDataModel playerDataModel = DataBase.getPlayerData(quitEvent.getPlayer());
        if(playerDataModel.isInAFight()) {
            playerDataModel.setHearts(playerDataModel.getHearts() - 1, quitEvent.getPlayer());
            playerDataModel.setFightingAndSave(quitEvent.getPlayer(), false);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(IN_FIGHT_LEFT.replace("%s", quitEvent.getPlayer().getName())));
        }
    }

    public static void playerDies(PlayerDeathEvent event) {
        PlayerDataModel playerDataModel = DataBase.getPlayerData(event.getPlayer());
        if(playerDataModel.isInAFight()) {
            playerDataModel.setHearts(playerDataModel.getHearts() - 1, event.getPlayer());
            playerDataModel.setFightingAndSave(event.getPlayer(), false);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(IN_FIGHT_DIED.replace("%s", event.getPlayer().getName())));
        }
    }

    public static void playerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player damagedPlayer) {
            Pair<Boolean, Player> damagedByAnotherEntity = isDamagedByAnotherPlayer(event);
            if (damagedByAnotherEntity.a()) {
                // Damaged player logic
                PlayerDataModel playerDataModel = DataBase.getPlayerData(damagedPlayer);
                if (!playerDataModel.isInAFight()) {
                    damagedPlayer.sendMessage(MiniMessage.miniMessage().deserialize(NOW_IN_FIGHT));
                }
                playerDataModel.setStillInAFightFor(30, damagedPlayer);
                playerDataModel.setFightingAndSave(damagedPlayer, true); // Correct for damaged player

                // Attacker (enemy) logic
                Player attacker = damagedByAnotherEntity.b();
                PlayerDataModel enemyPlayerDataModel = DataBase.getPlayerData(attacker);
                if (!enemyPlayerDataModel.isInAFight()) {
                    attacker.sendMessage(MiniMessage.miniMessage().deserialize(NOW_IN_FIGHT)); // Send message to attacker
                }
                enemyPlayerDataModel.setStillInAFightFor(30, attacker);
                enemyPlayerDataModel.setFightingAndSave(attacker, true); // Correct for attacker
            }
        }
    }


    //UTIL
    public static Pair<Boolean, Player> isDamagedByAnotherPlayer(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player damagedPlayer)) {
            return new Pair<>(false, null);
        }

        // 1. Check direct damage causes
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            Entity damager = entityEvent.getDamager();

            // 1a. Direct player attack
            if (damager instanceof Player killer && !killer.equals(damagedPlayer)) {
                return new Pair<>(true, killer);
            }

            // 1b. Projectiles (arrows, snowballs, etc)
            if (damager instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player shooter
                        && !shooter.equals(damagedPlayer)) {
                    return new Pair<>(true, shooter);
                }
            }

            // 1c. TNT explosions
            if (damager instanceof TNTPrimed tnt) {
                Entity source = tnt.getSource();
                if (source instanceof Player igniter && !igniter.equals(damagedPlayer)) {
                    return new Pair<>(true, igniter);
                }
            }

            // 1d. Other explosion sources (fireballs, wither skulls)
            if (damager instanceof Explosive explosive) {
                if (explosive instanceof Fireball fireball) {
                    if (fireball.getShooter() instanceof Player shooter
                            && !shooter.equals(damagedPlayer)) {
                        return new Pair<>(true, shooter);
                    }
                }
            }
        }
        /*
        // 2. Check for explosion damage (when damager isn't directly available)
        else if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            // Try to get the killer from death message (fallback)
            String deathMessage = damagedPlayer.getLastDamageCause().getMess;
            if (deathMessage != null) {
                // Parse "Player was blown up by Player2" pattern
                if (deathMessage.contains("blown up by")) {
                    String[] parts = deathMessage.split("blown up by");
                    if (parts.length > 1) {
                        String killerName = parts[1].trim();
                        Player killer = damagedPlayer.getServer().getPlayer(killerName);
                        if (killer != null && !killer.equals(damagedPlayer)) {
                            return new Pair<>(true, killer);
                        }
                    }
                }
            }
        }
         */

        return new Pair<>(false, null);
    }
}
