package com.sebastian.heartbreaker_pvp.database;

import com.google.gson.Gson;
import com.sebastian.heartbreaker_pvp.GsonUtils;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import com.sebastian.heartbreaker_pvp.time_limit.TimeLimitManager;
import com.sebastian.heartbreaker_pvp.translations.Language;
import com.sebastian.heartbreaker_pvp.translations.Translations;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;


public class PlayerDataModel {
    private int hearts = 3;
    private int timeLimit = TimeLimitManager.GLOBAL_TIME_LIMIT; //60 sec * 60 min = 1h
    private LocalDateTime lastResetTimeLimit = LocalDateTime.now(ZoneId.of("UTC"));
    private boolean inAFight = false;
    private int stillInAFightFor = 0;
    private boolean timerPaused = false;
    private String language = "not_set";

    public int getHearts() {
        return this.hearts;
    }

    private void setHearts(int hearts) {
        this.hearts = Math.clamp((long)hearts, 0, 3);
    }

    public void setHearts(int hearts, Player player) {
        this.setHearts(hearts);
        PacketSender.getInstance().sendHeartsDecreasedPacket(player, hearts);
    }

    public void setLanguage(Language language) {
        this.language = language.getCode();
    }

    public Language getLanguage() {
        return Translations.getLanguageFromCode(language);
    }

    public void setTimerPaused(boolean timerPaused) {
        this.timerPaused = timerPaused;
    }

    //TODO: Finish translating

    public boolean isTimerPaused() {
        return timerPaused;
    }

    public void setStillInAFightFor(int stillInAFightFor, Player owner) {
        this.stillInAFightFor = stillInAFightFor;
        PacketSender.getInstance().sendFightTimeLeftPacket(owner, stillInAFightFor, 30);
    }

    public void setStillInAFightForAndSave(int stillInAFightFor, Player owner) {
        this.stillInAFightFor = stillInAFightFor;
        PacketSender.getInstance().sendFightTimeLeftPacket(owner, stillInAFightFor, 30);
        DataBase.savePlayerData(owner, this);
    }

    public int getStillInAFightFor() {
        return stillInAFightFor;
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

    public int getTimeLimit() {
        return timeLimit;
    }

    public LocalDateTime getLastResetTimeLimit() {
        return lastResetTimeLimit;
    }

    public void setLastResetTimeLimit(LocalDateTime lastResetTimeLimit) {
        this.lastResetTimeLimit = lastResetTimeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isInAFight() {
        return inAFight;
    }

    public void setInAFight(boolean inAFight, Player owner) {
        this.inAFight = inAFight;
        PacketSender.getInstance().sendInFightStatus(owner, inAFight);
    }

    public boolean hasTimeLeft() {
        if(!TimeLimitManager.GLOBAL_TIME_LIMIT_ENABLED) return true;
        if(wasResetYesterday()) {
            lastResetTimeLimit = LocalDateTime.now(ZoneId.of("UTC"));
            timeLimit = TimeLimitManager.GLOBAL_TIME_LIMIT;
        }
        return timeLimit > 0;
    }

    public void setTimeAndSave(Player owner, Integer time) {
        timeLimit = time;
        DataBase.savePlayerData(owner, this);
    }

    public Integer modifyTimeAndSave(Player owner, Integer delta) {
        timeLimit += delta;
        DataBase.savePlayerData(owner, this);
        return timeLimit;
    }

    public void setFightingAndSave(Player owner, Boolean inAFight) {
        this.inAFight = inAFight;
        PacketSender.getInstance().sendInFightStatus(owner, inAFight);
        DataBase.savePlayerData(owner, this);
    }

    public void setTimerPausedAndSave(Player owner, Boolean timerPaused) {
        this.timerPaused = timerPaused;
        DataBase.savePlayerData(owner, this);
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