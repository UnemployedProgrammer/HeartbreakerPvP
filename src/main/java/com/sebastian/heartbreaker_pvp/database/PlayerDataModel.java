package com.sebastian.heartbreaker_pvp.database;

import com.google.gson.Gson;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import com.sebastian.heartbreaker_pvp.mod_compat.PacketSender;
import org.bukkit.entity.Player;


public class PlayerDataModel {
    private int hearts = 3;

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

    public String toString() {
        return "PlayerData{hearts:" + this.hearts + "}";
    }

    public static PlayerDataModel fromJSON(String json) {
        Gson gson = new Gson();
        PlayerDataModel temp = (PlayerDataModel)gson.fromJson(json, PlayerDataModel.class);
        HeartbreakerPvP.logger.info("Loaded PlayerDataModel, data: " + temp.toString());
        return temp;
    }

    public String toJson() {
        return "{\"hearts\":" + this.hearts + "}";
    }

    public PlayerDataModel copy() {
        return new PlayerDataModel(this.hearts);
    }
}