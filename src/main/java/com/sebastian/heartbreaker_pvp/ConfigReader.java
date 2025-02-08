package com.sebastian.heartbreaker_pvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ConfigReader {
    public static class ActionbarConfig {
        private String actionbar_mode;
        private String actionbar_msg;
        private String zero_hearts_handling;
        private String kick_msg;
        private List<String> custom_actionbar_text_information_keys;

        public String getActionbar_mode() {
            return this.actionbar_mode;
        }

        public void setActionbar_mode(String actionbar_mode) {
            this.actionbar_mode = actionbar_mode;
        }

        public String getActionbar_msg() {
            return this.actionbar_msg;
        }

        public void setActionbar_msg(String actionbar_msg) {
            this.actionbar_msg = actionbar_msg;
        }

        public String getZero_hearts_handling() {
            return this.zero_hearts_handling;
        }

        public void setZero_hearts_handling(String zero_hearts_handling) {
            this.zero_hearts_handling = zero_hearts_handling;
        }

        public String getKick_msg() {
            return this.kick_msg;
        }

        public void setKick_msg(String kick_msg) {
            this.kick_msg = kick_msg;
        }

        public List<String> getCustom_actionbar_text_information_keys() {
            return this.custom_actionbar_text_information_keys;
        }

        public void setCustom_actionbar_text_information_keys(List<String> custom_actionbar_text_information_keys) {
            this.custom_actionbar_text_information_keys = custom_actionbar_text_information_keys;
        }

        public String toString() {
            String var10000 = this.actionbar_mode;
            return "ActionbarConfig{actionbar_mode='" + var10000 + "', actionbar_msg='" + this.actionbar_msg + "', zero_hearts_handling='" + this.zero_hearts_handling + "', kick_msg='" + this.kick_msg + "', custom_actionbar_text_information_keys=" + String.valueOf(this.custom_actionbar_text_information_keys) + "}";
        }
    }

    public static class Configuration {
        public static ConfigReader.ActionbarConfig configuration;

        public static void init_reload() {
            File configFile = new File(HeartbreakerPvP.dataFolder, "config.jsonc");
            HeartbreakerPvP.dataFolder.mkdirs();
            if (!configFile.exists()) {
                writeDefaultConfig(configFile);
            }

            try {
                String jsonContent = new String(Files.readAllBytes(Paths.get(configFile.getPath())));
                String jsonWithoutComments = jsonContent.replaceAll("(?s)//.*?\n", "");
                Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                configuration = (ConfigReader.ActionbarConfig)gson.fromJson(jsonWithoutComments, ConfigReader.ActionbarConfig.class);
                HeartbreakerPvP.logger.info("Config successfully loaded:");
                HeartbreakerPvP.logger.info(configuration.toString());
            } catch (IOException var4) {
                HeartbreakerPvP.logger.info("Error reading JSON config file:");
                HeartbreakerPvP.logger.info(var4.getMessage());
            }

        }

        private static void writeDefaultConfig(File configFile) {
            String defaultConfig = "{\n    // Content to display in actionbar\n    // Available options: text / emoji (if you are using geyser, hearts are still supported, but will look different) (Emoji Mode will display three Hearts!)\n    // actionbar_msg field disabled, when emoji selected\n    \"actionbar_mode\": \"text\",  // Actionbar display mode\n\n    // Content to display in actionbar\n    // MiniMessage Supported: https://webui.advntr.dev/\n    // Information Keys:\n    // %hearts_num\n    // %hearts_str (English)\n    // %hearts_str_de_DE\n    // %hearts_str_de_KSH (Ripuarian) [Een, Zwo, Drei]\n    // %hearts_str_es_es\n    // %hearts_str_it_it\n    // %hearts_str_custom\n    // Examples:\n    // - actionbar_msg: \"%hearts_num hearts left\" (Number, EN)\n    // - actionbar_msg: \"%hearts_str_de_DE Herz(-en) verbleibend\" (Text, DE)\n    // - actionbar_msg: \"%hearts_str_de_KSH Hätz(-e) (sin/es) übrig\" (Text, DE_KSHm, Ripuarian)\n    \"actionbar_msg\": \"%hearts_str heart(-s) left\",  // Message to display\n\n    // kick: Player is unable to join, once reaching 0 hearts!\n    // spectator: Player's gamemode is set to spectator.\n    \"zero_hearts_handling\": \"kick\",  // Action when hearts reach zero\n\n    // OPTIONALLY\n    // Customize the kick message\n    // MiniMessage Supported: https://webui.advntr.dev/\n    // Disabled, when zero_hearts_handling field is set to spectator.\n    // When empty, \"You have been kicked for reaching 0 hearts!\" is used.\n    \"kick_msg\": \"\",  // Custom kick message, empty means default message: <color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>\n\n    // Custom actionbar text information keys\n    \"custom_actionbar_text_information_keys\": [\"一\", \"二\", \"三\", \"无数据\", \"零\"]  // Example in Chinese (simplified)\n}\n";

            try {
                PrintWriter out = new PrintWriter(configFile);

                try {
                    out.println(defaultConfig);
                    HeartbreakerPvP.logger.info("Default configuration created.");
                } catch (Throwable var6) {
                    try {
                        out.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }

                    throw var6;
                }

                out.close();
            } catch (IOException var7) {
                HeartbreakerPvP.logger.info("Error writing default configuration:");
                HeartbreakerPvP.logger.info(var7.getMessage());
            }

        }
    }
}