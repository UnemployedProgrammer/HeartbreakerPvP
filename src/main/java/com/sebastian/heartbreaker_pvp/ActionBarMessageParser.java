package com.sebastian.heartbreaker_pvp;

import net.kyori.adventure.text.Component;

public class ActionBarMessageParser {
    public static Component getParsedActionBarMessage(int hearts) {
        return Component.newline(); //TODO
    }

    private enum LanguageDictionary {
        NUM("1","2","3");

        String one;
        String two;
        String three;
        private LanguageDictionary(String one, String two, String three) {

        }
    }
}
