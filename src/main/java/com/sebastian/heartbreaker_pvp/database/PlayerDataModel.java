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
    private int stillInAFightFor = 0;
    private boolean timerPaused = false;

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

    public void setTimerPaused(boolean timerPaused) {
        this.timerPaused = timerPaused;
    }

    public boolean isTimerPaused() {
        return timerPaused;
    }

    public void setStillInAFightFor(int stillInAFightFor) {
        this.stillInAFightFor = stillInAFightFor;
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

    public void setInAFight(boolean inAFight) {
        this.inAFight = inAFight;
    }

    public boolean hasTimeLeft() {
        if(!TimeLimitManager.GLOBAL_TIME_LIMIT_ENABLED) return true;
        if(wasResetYesterday()) {
            lastResetTimeLimit = LocalDateTime.now(ZoneId.of("UTC"));
            timeLimit = TimeLimitManager.GLOBAL_TIME_LIMIT;
        }
        return timeLimit > 0;
    }

    //TODO: Need to fix this:
    /*
        [21:44:22 WARN]: [HeartBreaker] No DataModel, returning new PlayerDataModel.
[21:44:22 ERROR]: Could not pass event ServerTickEndEvent to HeartbreakerPvP v1.0-BETA
java.lang.NullPointerException: Cannot invoke "java.lang.Integer.intValue()" because "com.sebastian.heartbreaker_pvp.time_limit.TimeLimitManager.GLOBAL_TIME_LIMIT" is null
	at Heartbreaker PvP-1.0-BETA.jar/com.sebastian.heartbreaker_pvp.database.PlayerDataModel.<init>(PlayerDataModel.java:16) ~[Heartbreaker PvP-1.0-BETA.jar:?]
	at Heartbreaker PvP-1.0-BETA.jar/com.sebastian.heartbreaker_pvp.database.DataBase.getPlayerData(DataBase.java:18) ~[Heartbreaker PvP-1.0-BETA.jar:?]
	at Heartbreaker PvP-1.0-BETA.jar/com.sebastian.heartbreaker_pvp.EventListeners.onServerTickEnd(EventListeners.java:80) ~[Heartbreaker PvP-1.0-BETA.jar:?]
	at co.aikar.timings.TimedEventExecutor.execute(TimedEventExecutor.java:80) ~[paper-api-1.21.4-R0.1-SNAPSHOT.jar:?]
	at org.bukkit.plugin.RegisteredListener.callEvent(RegisteredListener.java:70) ~[paper-api-1.21.4-R0.1-SNAPSHOT.jar:?]
	at io.papermc.paper.plugin.manager.PaperEventManager.callEvent(PaperEventManager.java:54) ~[paper-1.21.4.jar:1.21.4-138-5395ae3]
	at io.papermc.paper.plugin.manager.PaperPluginManagerImpl.callEvent(PaperPluginManagerImpl.java:131) ~[paper-1.21.4.jar:1.21.4-138-5395ae3]
	at org.bukkit.plugin.SimplePluginManager.callEvent(SimplePluginManager.java:628) ~[paper-api-1.21.4-R0.1-SNAPSHOT.jar:?]
	at org.bukkit.event.Event.callEvent(Event.java:45) ~[paper-api-1.21.4-R0.1-SNAPSHOT.jar:?]
	at net.minecraft.server.MinecraftServer.tickServer(MinecraftServer.java:1566) ~[paper-1.21.4.jar:1.21.4-138-5395ae3]
	at net.minecraft.server.MinecraftServer.runServer(MinecraftServer.java:1251) ~[paper-1.21.4.jar:1.21.4-138-5395ae3]
	at net.minecraft.server.MinecraftServer.lambda$spin$2(MinecraftServer.java:310) ~[paper-1.21.4.jar:1.21.4-138-5395ae3]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[?:?]

    -> Probably add null check to server end tick method...
     */

    public void decreaseTimeAndSave(Player owner) {
        timeLimit--;
        DataBase.savePlayerData(owner, this);
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