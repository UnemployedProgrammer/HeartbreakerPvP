package com.sebastian.heartbreaker_pvp.translations;

import java.util.HashMap;

public class AmericanEnglishLanguage extends Language {
    @Override
    public String getCode() {
        return "en_us";
    }

    @Override
    public String getName() {
        return "English (US)";
    }

    @Override
    protected HashMap<String, String> getTranslations() {
        HashMap<String, String> translations = new HashMap<>();
        translations.put("player_not_found_or_whitelisted", "Player not found or whitelisted!");
        translations.put("stats_for_player", "Stats for %s");
        return translations;
    }
}
