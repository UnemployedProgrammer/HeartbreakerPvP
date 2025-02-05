package com.sebastian.heartbreaker_pvp.database;

import com.google.gson.Gson;
import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;

public class PlayerDataModel {
    private int hearts;

    public PlayerDataModel() {
        hearts = 3; //Start with three
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = Math.clamp(hearts, 0, 3);
    }

    @Override
    public String toString() {
        return "PlayerData{hearts:" + hearts + "}";
    }

    public static PlayerDataModel fromJSON(String json) {
        Gson gson = new Gson();
        PlayerDataModel temp = gson.fromJson(json, PlayerDataModel.class);
        HeartbreakerPvP.logger.info("Loaded PlayerDataModel, data: " + temp.toString());
        return temp;
    }

    public String toJson() {
        return "{\"hearts\":3}";
    }
}
