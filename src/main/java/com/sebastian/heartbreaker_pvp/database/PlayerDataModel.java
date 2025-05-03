package com.sebastian.heartbreaker_pvp.database;

import com.google.gson.Gson;
import com.sebastian.heartbreaker_pvp.GsonUtils;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import com.sebastian.heartbreaker_pvp.time_limit.TimeLimitManager;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;


public class PlayerDataModel {
    private int hearts = 3;
    private int timeLimit = TimeLimitManager.GLOBAL_TIME_LIMIT; //60 sec * 60 min = 1h
    private LocalDateTime lastResetTimeLimit = LocalDateTime.now(ZoneId.of("UTC"));
    private boolean inAFight = false;

    public int getHearts() {
        return this.hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = Math.clamp((long)hearts, 0, 3);
    }

    public void setHearts(int hearts, Player player) {
        this.setHearts(hearts);
        PacketSender.getInstance().sendHeartsDecreasedPacket(player, hearts);
    }

    private PlayerDataModel(int hearts) {
        this.hearts = hearts;
    }

    public PlayerDataModel() {
    }

    public boolean wasResetYesterday() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        return lastResetTimeLimit.toLocalDate().isBefore(now.toLocalDate());
    }

    public boolean hasTimeLeft() {
        if(!TimeLimitManager.GLOBAL_TIME_LIMIT_ENABLED) return true;
        if(wasResetYesterday()) {
            lastResetTimeLimit = LocalDateTime.now(ZoneId.of("UTC"));
            timeLimit = TimeLimitManager.GLOBAL_TIME_LIMIT;
        }
        return timeLimit > 0;
    }

    public void decreaseTimeAndSave(Player owner) {
        timeLimit--;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PlayerDataModel{");
        sb.append("hearts=").append(hearts);
        sb.append(", timeLimit=").append(timeLimit);
        sb.append(", lastResetTimeLimit=").append(lastResetTimeLimit);
        sb.append(", inAFight=").append(inAFight);
        sb.append('}');
        return sb.toString();
    }

    public static PlayerDataModel fromJSON(String json) {
        PlayerDataModel temp = GsonUtils.GSON.fromJson(json, PlayerDataModel.class);
        HeartbreakerPvP.logger.info("Loaded PlayerDataModel, data: " + temp.toString());
        return temp;
    }

    public String toJson() {
        return GsonUtils.GSON.toJson(this);
    }

    public PlayerDataModel copy() {
        return new PlayerDataModel(this.hearts);
    }
}