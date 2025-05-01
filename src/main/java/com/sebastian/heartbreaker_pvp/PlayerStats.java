package com.sebastian.heartbreaker_pvp;

public record PlayerStats(String username, Integer health, Integer absorptionHealth, Integer maxHealth, Integer kills, Integer deaths, Integer heroHearts, Integer blocksBroken, Integer blocksPlaced, Integer playerKills, Float experienceProgress, Integer experienceLevel) {
}
