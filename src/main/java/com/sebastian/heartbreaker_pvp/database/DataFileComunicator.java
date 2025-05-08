package com.sebastian.heartbreaker_pvp.database;

import com.sebastian.heartbreaker_pvp.HeartbreakerPvP;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;

public class DataFileComunicator {

    public static void init(File datafolder) {
        HeartbreakerPvP.logger.info(new File(datafolder, "db").mkdirs() ? "Created DataBase." : "DataBase is already there, good!");
    }

    public static void savePlayerFile(Player player, PlayerDataModel data) {
        File dataFolder = HeartbreakerPvP.dataFolder;
        // File path where the file will be created or written to
        File filePath = new File(new File(dataFolder, "db"), player.getUniqueId().toString() + ".json");
        // The content you want to write into the file
        String content = data.toJson();

        try {

            filePath.delete();

            // Create FileWriter object (true for append mode)
            FileWriter writer = new FileWriter(filePath, false);
            // Write content to the file
            writer.write(content);
            // Close the writer
            writer.close();

            HeartbreakerPvP.logger.info("Saved data for player successfully: " + player.getName() + ":" + player.getUniqueId().toString());
        } catch (IOException e) {
            HeartbreakerPvP.logger.warning(e.toString());
        }
    }

    public static void savePlayerFile(OfflinePlayer player, PlayerDataModel data) {
        File dataFolder = HeartbreakerPvP.dataFolder;
        // File path where the file will be created or written to
        File filePath = new File(new File(dataFolder, "db"), player.getUniqueId().toString() + ".json");
        // The content you want to write into the file
        String content = data.toJson();

        try {

            filePath.delete();

            // Create FileWriter object (true for append mode)
            FileWriter writer = new FileWriter(filePath, false);
            // Write content to the file
            writer.write(content);
            // Close the writer
            writer.close();

            HeartbreakerPvP.logger.info("Saved data for player successfully: " + player.getName() + ":" + player.getUniqueId().toString());
        } catch (IOException e) {
            HeartbreakerPvP.logger.warning(e.toString());
        }
    }

    public static PlayerDataModel readPlayerFile(Player player) {
        try {
            // Create a FileReader and BufferedReader to read the file
            File dataFolder = HeartbreakerPvP.dataFolder;
            File filePath = new File(new File(dataFolder, "db"), player.getUniqueId().toString() + ".json");

            if(!filePath.exists()) {
                HeartbreakerPvP.logger.info("Player joins first time, returning empty PlayerDataModel! Player: " + player.getName() + ":" + player.getUniqueId().toString());
                return new PlayerDataModel();
            }

            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            StringBuilder content = new StringBuilder();
            // Read each line until the end of the file
            while ((line = bufferedReader.readLine()) != null) {
                content.append("\n").append(line);
            }

            // Close the readers
            bufferedReader.close();

            return PlayerDataModel.fromJSON(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PlayerDataModel();
    }

    public static PlayerDataModel readPlayerFile(OfflinePlayer player) {
        try {
            // Create a FileReader and BufferedReader to read the file
            File dataFolder = HeartbreakerPvP.dataFolder;
            File filePath = new File(new File(dataFolder, "db"), player.getUniqueId().toString() + ".json");

            if(!filePath.exists()) {
                HeartbreakerPvP.logger.info("Player joins first time, returning empty PlayerDataModel! Player: " + player.getName() + ":" + player.getUniqueId().toString());
                return new PlayerDataModel();
            }

            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            StringBuilder content = new StringBuilder();
            // Read each line until the end of the file
            while ((line = bufferedReader.readLine()) != null) {
                content.append("\n").append(line);
            }

            // Close the readers
            bufferedReader.close();

            return PlayerDataModel.fromJSON(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PlayerDataModel();
    }
}
