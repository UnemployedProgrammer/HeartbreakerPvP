package com.sebastian.heartbreaker_pvp.translations;

import com.sebastian.heartbreaker_pvp.StatsGui;

import java.util.HashMap;

public class GermanyGermanLanguage extends Language {
    @Override
    public String getCode() {
        return "de_de";
    }

    @Override
    public String getName() {
        return "Deutsch (Deutschland)";
    }

    @Override
    protected HashMap<String, String> getTranslations() {
        HashMap<String, String> translations = new HashMap<>();
        translations.put("player_not_found_or_not_whitelisted", "Spieler nicht gefunden oder nicht auf der Whitelist!");
        translations.put("stats_for_player", "Statistiken für %s");
        translations.put("close", "<red><b>Schließen</b></red>");

        translations.put("current_health", StatsGui.Utils.translateTitle("Aktuelle Gesundheit"));
        translations.put("current_health.desc", """
                <color:#8a9fff>%s</color><white>'s Gesundheit</white>
                <white><green>»</green> %s/%s <gray>Punkte</gray></white>
                <white><green>»</green> %s <gray>Absorption</gray></white>
                """);

        translations.put("hero_hearts", StatsGui.Utils.translateTitle("Aktuelle Hero-Herzen"));
        translations.put("hero_hearts.desc", """
                <color:#8a9fff>%s</color><white>'s Hero-Herzen</white>
                <white><green>»</green> %s/3 <gray>Hero-Herzen</gray></white>
                """);

        translations.put("kills", StatsGui.Utils.translateTitle("Kills"));
        translations.put("kills.desc", """
                <color:#8a9fff>%s</color><white>'s bisherige Kills</white>
                <white><green>»</green> %s <gray>Gesamt</gray></white>
                <white><green>»</green> %s <gray>Spieler</gray></white>
                """);

        translations.put("experience", StatsGui.Utils.translateTitle("Aktuelle Erfahrung"));
        translations.put("experience.desc", """
                <color:#8a9fff>%s</color><white>'s Erfahrung</white>
                <white><green>»</green> %s <gray>Level</gray></white>
                <white><green>»</green> %s% <gray>Gesammelt für nächstes Level</gray></white>
                """);

        translations.put("deaths", StatsGui.Utils.translateTitle("Tode"));
        translations.put("deaths.desc", """
                <color:#8a9fff>%s</color><white>'s Todesanzahl bisher</white>
                <white><green>»</green> %s <gray>Tode</gray></white>
                """);

        translations.put("blocks_placed", StatsGui.Utils.translateTitle("Platzierte Blöcke"));
        translations.put("blocks_placed.desc", """
                <color:#8a9fff>%s</color><white>'s bisher platzierte Blöcke</white>
                <white><green>»</green> %s <gray>Blöcke</gray></white>
                """);

        translations.put("blocks_destroyed", StatsGui.Utils.translateTitle("Zerstörte Blöcke"));
        translations.put("blocks_destroyed.desc", """
                <color:#8a9fff>%s</color><white>'s bisher zerstörte Blöcke</white>
                <white><green>»</green> %s <gray>Blöcke</gray></white>
                """);

        return translations;
    }
}