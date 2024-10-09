package com.sebastian.heartbreaker_pvp;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileReader {
    public static class Configuration {
        public static ActionbarConfig configuration;

        public static void init_reload() {
            File config_file = new File(HeartbreakerPvP.dataFolder, "config.yml");
            HeartbreakerPvP.dataFolder.mkdirs();

            if (!config_file.exists()) {
                // If the file doesn't exist, write default content to it
                writeDefaultConfig(config_file);
            }

            Yaml yaml = new Yaml();
            try (InputStream inputStream = new FileInputStream(config_file)) {
                // Parse the YAML as a Map to get the top-level config
                Map<String, List<ActionbarConfig>> configMap = yaml.load(inputStream);
                List<ActionbarConfig> configList = configMap.get("config"); // Access the list of ActionbarConfig
                if (configList != null && !configList.isEmpty()) {
                    configuration = configList.getFirst(); // Assuming you want the first entry
                }

                // Print the loaded config
                HeartbreakerPvP.logger.info("Read Config:");
                HeartbreakerPvP.logger.info(configuration.toString());
            } catch (Exception e) {
                HeartbreakerPvP.logger.info("Error reading in yaml");
                HeartbreakerPvP.logger.info(e.getMessage());
                //Kein Bock Mehr Nehme einf. JSON :)
            }
        }

        private static void writeDefaultConfig(File config_file) {
            // Default YAML content
            String defaultConfig = "config:\n" +
                    "    #Content to display in actionbar\n" +
                    "    #Available options: text / emoji (if you are using geyser, hearts are still supported, but will look diffrent)\n" +
                    "    # - actionbar_msg field disabled, when emoji selected\n" +
                    "    - actionbar_mode: text\n\n" +
                    "    #Content to display in actionbar\n" +
                    "    #MiniMessage Supported: https://webui.advntr.dev/\n" +
                    "    #Information Keys:\n" +
                    "        # %hearts_num\n" +
                    "        # %hearts_str (English)\n" +
                    "        # %hearts_str_de_DE\n" +
                    "        # %hearts_str_de_KSH (Ripuarian) [Een, Zwo, Drei]\n" +
                    "        # %hearts_str_es_es\n" +
                    "        # %hearts_str_it_it\n" +
                    "        # %hearts_str_custom\n" +
                    "    #Examples:\n" +
                    "        # - actionbar_msg: \"%hearts_num hearts left\" (Number, EN)\n" +
                    "        # - actionbar_msg: \"%hearts_str_de_DE Herz(-en) verbleibend\" (Text, DE)\n" +
                    "        # - actionbar_msg: \"%hearts_str_de_KSH Hätz(-e) (sin/es) übrig\" (Text, DE_KSHm, Ripuarian)\n\n" +
                    "    - actionbar_msg: \"%hearts_str heart(-s) left\" #(Text, EN)\n\n" +
                    "    #kick: Player is unable to join, once reaching 0 hearts!\n" +
                    "    #spectator: Player's gamemode is set to spectator.\n" +
                    "    - zero_hearts_handling: \"kick\"\n\n" +
                    "    #OPTIONALLY\n" +
                    "    #Customize the kick message\n" +
                    "    #Disabled, when zero_hearts_handling field is set to spectator.\n" +
                    "    #When empty, \"You have been kicked for reaching 0 hearts!\" is used.\n" +
                    "    - kick_msg: \"\"\n" +
                    "    - custom_actionbar_text_information_keys: [\"一\", \"二\", \"三\"]"; // Chinese (simplified)

            // Write the default configuration to the file
            try (PrintWriter out = new PrintWriter(new FileOutputStream(config_file))) {
                out.println(defaultConfig);
                System.out.println("Default configuration file created: " + config_file.getPath());
            } catch (IOException e) {
                System.err.println("Error writing default configuration: " + e.getMessage());
            }
        }
    }

    public static class ActionbarConfig {
        private String actionbar_mode;
        private String actionbar_msg;
        private String zero_hearts_handling;
        private String kick_msg;
        private List<String> custom_actionbar_text_information_keys;

        public ActionbarConfig(Map<String, Object> map) {
            this.actionbar_mode = (String) map.get("actionbar_mode");
            this.actionbar_msg = (String) map.get("actionbar_msg");
            this.zero_hearts_handling = (String) map.get("zero_hearts_handling");
            this.kick_msg = (String) map.get("kick_msg");
            this.custom_actionbar_text_information_keys = (List<String>) map.get("custom_actionbar_text_information_keys");
        }

        // Getters and Setters
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
