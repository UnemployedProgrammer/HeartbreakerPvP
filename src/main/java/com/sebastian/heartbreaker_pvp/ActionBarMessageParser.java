package com.sebastian.heartbreaker_pvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.Objects;

public class ActionBarMessageParser {
    private static final ConfigReader.ActionbarConfig config = ConfigReader.Configuration.configuration;
    private static final List<String> customTextNumbers = config.getCustom_actionbar_text_information_keys();

    public static Component getParsedActionBarMessage(int hearts) {

        if(Objects.equals(config.getActionbar_mode(), "emoji")) {
            return MiniMessage.miniMessage().deserialize(LanguageDictionary.EMOJI.getForNum(hearts));
        }

        String mm = LanguageDictionary.replaceTranslation(config.getActionbar_msg(), hearts);

        return MiniMessage.miniMessage().deserialize(mm);
    }

    private enum LanguageDictionary {
        NUM("1","2","3", "N/D", "0"),
        EN("One","Two","Three", "N/D", "Zero"),
        DE("Ein","Zwei","Drei", "K/D", "Null"),
        DE_KSH("Een","Zwo","Drei", "Keen Date", "Null"),
        ES("Un","Dos","Tres", "Sin información", "Cero"),
        IT("Un","Due","Tre", "Nessuna informazione", "Zero"),
        CUSTOM(customTextNumbers.getFirst() ,customTextNumbers.get(1),customTextNumbers.get(2), customTextNumbers.get(3), customTextNumbers.get(4)),
        EMOJI("<dark_gray>❤❤</dark_gray><color:#85c8ff>❤</color>","<dark_gray>❤</dark_gray><color:#85c8ff>❤❤</color>","<color:#85c8ff>❤❤❤</color>", "?❤?❤?❤?", "<dark_gray>❤❤❤</dark_gray>");

        final String one;
        final String two;
        final String three;
        final String zero;
        final String no_data;
        LanguageDictionary(String one, String two, String three, String no_data, String zero) {
            this.one = one;
            this.two = two;
            this.three = three;
            this.no_data = no_data;
            this.zero = zero;
        }

        public String getForNum(int num) {
            if(num == 3) {
                return three;
            }
            if(num == 2) {
                return three;
            }
            if(num == 1) {
                return three;
            }
            if(num == 0) {
                return zero;
            }
            return no_data;
        }

        public static String replaceTranslation(String source, int i) {
            return source
                    .replace("%hearts_num", LanguageDictionary.NUM.getForNum(i))
                    .replace("%hearts_str", LanguageDictionary.EN.getForNum(i))
                    .replace("%hearts_str_de_DE", LanguageDictionary.DE.getForNum(i))
                    .replace("%hearts_str_de_KSH", LanguageDictionary.DE_KSH.getForNum(i))
                    .replace("%hearts_str_es_es", LanguageDictionary.ES.getForNum(i))
                    .replace("%hearts_str_it_it", LanguageDictionary.IT.getForNum(i))
                    .replace("%hearts_str_custom", LanguageDictionary.CUSTOM.getForNum(i));
        }
    }
}
