package com.sebastian.heartbreaker_pvp.translations;

import com.sebastian.heartbreaker_pvp.StatsGui;

import java.util.HashMap;

public class GermanyColognianLanguage extends Language {
    @Override
    public String getCode() {
        return "ksh"; // Ändere den Code für Kölsch
    }

    @Override
    public String getName() {
        return "Kölsch (Düütschland/Rhingland)";
    }

    @Override
    protected HashMap<String, String> getTranslations() {
        HashMap<String, String> translations = new HashMap<>();
        translations.put("player_not_found_or_not_whitelisted", "Spiller nit jefunge oder nit op de Wiet-Lis!");
        translations.put("stats_for_player", "Statistik vum %s");
        translations.put("close", "<red><b>Zomache</b></red>");

        translations.put("current_health", StatsGui.Utils.translateTitle("Aktuell Jesundheit"));
        translations.put("current_health.desc", """
                <color:#8a9fff>%s</color><white> sing Jesundheit</white>
                <white><green>»</green> %s/%s <gray>Punkte</gray></white>
                <white><green>»</green> %s <gray>Absorption</gray></white>
                """);

        translations.put("hero_hearts", StatsGui.Utils.translateTitle("Aktuell Hero-Hätze"));
        translations.put("hero_hearts.desc", """
                <color:#8a9fff>%s</color><white> sing Hero-Hätze</white>
                <white><green>»</green> %s/3 <gray>Hero-Hätze</gray></white>
                """);

        translations.put("kills", StatsGui.Utils.translateTitle("Plattjemaate"));
        translations.put("kills.desc", """
                <color:#8a9fff>%s</color><white> sing Plattjemaate bes jetz</white>
                <white><green>»</green> %s <gray>Alls en allem</gray></white>
                <white><green>»</green> %s <gray>Spiller</gray></white>
                """);

        translations.put("experience", StatsGui.Utils.translateTitle("Aktuell Erfahrunge"));
        translations.put("experience.desc", """
                <color:#8a9fff>%s</color><white> sing Erfahrunge</white>
                <white><green>»</green> %s <gray>Level</gray></white>
                <white><green>»</green> %s% <gray>Jesammelt för et nächste Level</gray></white>
                """);

        translations.put("deaths", StatsGui.Utils.translateTitle("Jestroove"));
        translations.put("deaths.desc", """
                <color:#8a9fff>%s</color><white> sing Zall vun Jestroove bes jetz</white>
                <white><green>»</green> %s <gray>Jestroove</gray></white>
                """);

        translations.put("blocks_placed", StatsGui.Utils.translateTitle("Jelaate Blöck"));
        translations.put("blocks_placed.desc", """
                <color:#8a9fff>%s</color><white> sing jelaate Blöck bes jetz</white>
                <white><green>»</green> %s <gray>Blöck</gray></white>
                """);

        translations.put("blocks_destroyed", StatsGui.Utils.translateTitle("Kapottjemaate Blöck"));
        translations.put("blocks_destroyed.desc", """
                <color:#8a9fff>%s</color><white> sing kapottjemaate Blöck bes jetz</white>
                <white><green>»</green> %s <gray>Blöck</gray></white>
                """);

        //HUD
        translations.put("seconds_short", "s");
        translations.put("minutes_short", "m");
        translations.put("hours_short", "std");
        translations.put("fight", "Kamf");
        translations.put("no_hearts_left_kick", "<color:#36abff><gray>❤❤❤</gray> Do <u>woodts gekickt</u>, do do <u><b>0</b> Hätze erreich</u> häs! <gray>❤❤❤</gray></color>");

        return translations;
    }
}