package com.sebastian.heartbreaker_pvp;

import net.kyori.adventure.text.Component;

public class ActionBarMessageParser {
    public static Component getParsedActionBarMessage(int hearts) {
        return Component.newline(); //TODO
    }

    private enum LanguageDictionary {
        NUM("1","2","3", "N/D", "0"),
        EN("One","Two","Three", "N/D", "Zero"),
        DE("Ein","Zwei","Drei", "K/D", "Null"),
        DE_KSH("Een","Zwo","Drei", "Keen Date", "Null"),
        ES("Un","Dos","Tres", "Sin información", "Cero"),
        IT("Un","Due","Tre", "Nessuna informazione", "Zero"),
        CUSTOM("Un","Due","Tre", "Nessuna informazione", "Zero"),
        EMOJI("Un","Due","Tre", "Nessuna informazione", "Zero");

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
    }
}
