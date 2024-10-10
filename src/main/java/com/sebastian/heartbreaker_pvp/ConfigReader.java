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

    public static class Configuration {
        public static ActionbarConfig configuration;

        public static void init_reload() {
            File configFile = new File(HeartbreakerPvP.dataFolder, "config.jsonc");
            HeartbreakerPvP.dataFolder.mkdirs();

            if (!configFile.exists()) {
                writeDefaultConfig(configFile);
            }

            try {
                // Read the JSONC file and remove comments
                String jsonContent = new String(Files.readAllBytes(Paths.get(configFile.getPath())));
                String jsonWithoutComments = jsonContent.replaceAll("(?s)//.*?\n", "");

                // Use Gson to parse the JSON
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                configuration = gson.fromJson(jsonWithoutComments, ActionbarConfig.class);

                HeartbreakerPvP.logger.info("Config successfully loaded:");
                HeartbreakerPvP.logger.info(configuration.toString());

            } catch (IOException e) {
                HeartbreakerPvP.logger.info("Error reading JSON config file:");
                HeartbreakerPvP.logger.info(e.getMessage());
            }
        }

        private static void writeDefaultConfig(File configFile) {
            // Write the default configuration with JSONC comments
            String defaultConfig = """
            {
                // Content to display in actionbar
                // Available options: text / emoji (if you are using geyser, hearts are still supported, but will look different)
                // actionbar_msg field disabled, when emoji selected
                "actionbar_mode": "text",  // Actionbar display mode

                // Content to display in actionbar
                // MiniMessage Supported: https://webui.advntr.dev/
                // Information Keys:
                // %hearts_num
                // %hearts_str (English)
                // %hearts_str_de_DE
                // %hearts_str_de_KSH (Ripuarian) [Een, Zwo, Drei]
                // %hearts_str_es_es
                // %hearts_str_it_it
                // %hearts_str_custom
                // Examples:
                // - actionbar_msg: "%hearts_num hearts left" (Number, EN)
                // - actionbar_msg: "%hearts_str_de_DE Herz(-en) verbleibend" (Text, DE)
                // - actionbar_msg: "%hearts_str_de_KSH Hätz(-e) (sin/es) übrig" (Text, DE_KSHm, Ripuarian)
                "actionbar_msg": "%hearts_str heart(-s) left",  // Message to display

                // kick: Player is unable to join, once reaching 0 hearts!
                // spectator: Player's gamemode is set to spectator.
                "zero_hearts_handling": "kick",  // Action when hearts reach zero

                // OPTIONALLY
                // Customize the kick message
                // MiniMessage Supported: https://webui.advntr.dev/
                // Disabled, when zero_hearts_handling field is set to spectator.
                // When empty, "You have been kicked for reaching 0 hearts!" is used.
                "kick_msg": "",  // Custom kick message, empty means default message: <color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>

                // Custom actionbar text information keys
                "custom_actionbar_text_information_keys": ["一", "二", "三"]  // Example in Chinese (simplified)
            }
            """;

            try (PrintWriter out = new PrintWriter(configFile)) {
                out.println(defaultConfig);
                HeartbreakerPvP.logger.info("Default configuration created.");
            } catch (IOException e) {
                HeartbreakerPvP.logger.info("Error writing default configuration:");
                HeartbreakerPvP.logger.info(e.getMessage());
            }
        }
    }

    public static class ActionbarConfig {
        private String actionbar_mode;
        private String actionbar_msg;
        private String zero_hearts_handling;
        private String kick_msg;
        private List<String> custom_actionbar_text_information_keys;

        public String getActionbar_mode() {
            return actionbar_mode;
        }

        public void setActionbar_mode(String actionbar_mode) {
            this.actionbar_mode = actionbar_mode;
        }

        public String getActionbar_msg() {
            return actionbar_msg;
        }

        public void setActionbar_msg(String actionbar_msg) {
            this.actionbar_msg = actionbar_msg;
        }

        public String getZero_hearts_handling() {
            return zero_hearts_handling;
        }

        public void setZero_hearts_handling(String zero_hearts_handling) {
            this.zero_hearts_handling = zero_hearts_handling;
        }

        public String getKick_msg() {
            return kick_msg;
        }

        public void setKick_msg(String kick_msg) {
            this.kick_msg = kick_msg;
        }

        public List<String> getCustom_actionbar_text_information_keys() {
            return custom_actionbar_text_information_keys;
        }

        public void setCustom_actionbar_text_information_keys(List<String> custom_actionbar_text_information_keys) {
            this.custom_actionbar_text_information_keys = custom_actionbar_text_information_keys;
        }

        @Override
        public String toString() {
            return "ActionbarConfig{" +
                    "actionbar_mode='" + actionbar_mode + '\'' +
                    ", actionbar_msg='" + actionbar_msg + '\'' +
                    ", zero_hearts_handling='" + zero_hearts_handling + '\'' +
                    ", kick_msg='" + kick_msg + '\'' +
                    ", custom_actionbar_text_information_keys=" + custom_actionbar_text_information_keys +
                    '}';
        }
    }
}