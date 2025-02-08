package com.sebastian.heartbreaker_pvp.database;

import com.google.gson.Gson;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;


public class PlayerDataModel {
    private int hearts = 3;

    public int getHearts() {
        return this.hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = Math.clamp((long)hearts, 0, 3);
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
}