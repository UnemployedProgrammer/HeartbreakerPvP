package com.sebastian.heartbreaker_pvp.translations;

import com.sebastian.heartbreaker_pvp.StatsGui;

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
        translations.put("player_not_found_or_not_whitelisted", "Player not found or not whitelisted!");
        translations.put("stats_for_player", "Stats for %s");
        translations.put("close", "<red><b>Close</b></red>");

        translations.put("current_health", StatsGui.Utils.translateTitle("Current Health"));
        translations.put("current_health.desc", """
                <color:#8a9fff>%s</color><white>'s Health</white>
                <white><green>»</green> %s/%s <gray>Points</gray></white>
                <white><green>»</green> %s <gray>Absorption</gray></white>
                """);

        translations.put("hero_hearts", StatsGui.Utils.translateTitle("Current Hero-Hearts"));
        translations.put("hero_hearts.desc", """
                <color:#8a9fff>%s</color><white>'s Hero-Hearts</white>
                <white><green>»</green> %s/3 <gray>Hero-Hearts</gray></white>
                """);

        translations.put("kills", StatsGui.Utils.translateTitle("Kills"));
        translations.put("kills.desc", """
                <color:#8a9fff>%s</color><white>'s kills so far</white>
                <white><green>»</green> %s <gray>Total</gray></white>
                <white><green>»</green> %s <gray>Players</gray></white>
                """);

        translations.put("experience", StatsGui.Utils.translateTitle("Current Experience"));
        translations.put("experience.desc", """
                <color:#8a9fff>%s</color><white>'s experience</white>
                <white><green>»</green> %s <gray>Level</gray></white>
                <white><green>»</green> %s% <gray>Collected for Next Level</gray></white>
                """);

        translations.put("deaths", StatsGui.Utils.translateTitle("Deaths"));
        translations.put("deaths.desc", """
                <color:#8a9fff>%s</color><white>'s death count so far</white>
                <white><green>»</green> %s <gray>Deaths</gray></white>
                """);

        translations.put("blocks_placed", StatsGui.Utils.translateTitle("Blocks Placed"));
        translations.put("blocks_placed.desc", """
                <color:#8a9fff>%s</color><white>'s blocks placed so far</white>
                <white><green>»</green> %s <gray>Blocks</gray></white>
                """);

        translations.put("blocks_destroyed", StatsGui.Utils.translateTitle("Blocks Destroyed"));
        translations.put("blocks_destroyed.desc", """
                <color:#8a9fff>%s</color><white>'s blocks destroyed so far</white>
                <white><green>»</green> %s <gray>Blocks</gray></white>
                """);

        return translations;
    }

    //TODO: Add to all languages
    //  - "seconds_short" -> "s"
    //  - "minutes_short" -> "m"
    //  - "hours_short" -> "h"
    //  - "fight" -> "Fight"
    //  - "no_hearts_left_kick" -> "<color:#36abff><gray>❤❤❤</gray> You've <u>got kicked</u> because of <u>reaching <b>0</b> hearts</u>! <gray>❤❤❤</gray></color>"
}
