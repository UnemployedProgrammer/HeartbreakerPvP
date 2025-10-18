package com.sebastian.heartbreaker_pvp.translations;

import com.sebastian.heartbreaker_pvp.StatsGui;

import java.util.HashMap;

public class GermanyColognianLanguage extends Language {
    @Override
    public String getCode() {
        return "ksh";
    }

    @Override
    public String getName() {
        return "Kölsch (Kölle)";
    }

    @Override
    protected HashMap<String, String> getTranslations() {
        HashMap<String, String> translations = new HashMap<>();
        translations.put("player_not_found_or_not_whitelisted", "Dä Spiller wood nit jefonge or es nit op dä Erlaubelis!");
        translations.put("stats_for_player", "Statistike för %s");
        translations.put("close", "<red><b>Zoh maache</b></red>");

        translations.put("current_health", StatsGui.Utils.translateTitle("Jäzje Jesundheit"));
        translations.put("current_health.desc", """
                <color:#8a9fff>%s</color><white> sing Jesundheit</white>
                <white><green>»</green> %s/%s <gray>Punkte</gray></white>
                <white><green>»</green> %s <gray>Absorpsjohn</gray></white>
                """);

        translations.put("hero_hearts", StatsGui.Utils.translateTitle("Jäzje Heldehätze"));
        translations.put("hero_hearts.desc", """
                <color:#8a9fff>%s</color><white> sing Heldehätze</white>
                <white><green>»</green> %s/3 <gray>Heldehätze</gray></white>
                """);

        translations.put("kills", StatsGui.Utils.translateTitle("Umgemaat"));
        translations.put("kills.desc", """
                <color:#8a9fff>%s</color><white> hät bis jäz</white>
                <white><green>»</green> %s <gray>Jesamt</gray></white>
                <white><green>»</green> %s <gray>Spiller</gray></white>
                """);

        translations.put("experience", StatsGui.Utils.translateTitle("Erfahrong"));
        translations.put("experience.desc", """
                <color:#8a9fff>%s</color><white> sing Erfahrong</white>
                <white><green>»</green> %s <gray>Levvel</gray></white>
                <white><green>»</green> %s%% <gray>jesamme för et nächste Levvel</gray></white>
                """);

        translations.put("deaths", StatsGui.Utils.translateTitle("Jestorve"));
        translations.put("deaths.desc", """
                <color:#8a9fff>%s</color><white> es bis jäz %s Mol jestorve</white>
                <white><green>»</green> %s <gray>Tode</gray></white>
                """);

        translations.put("blocks_placed", StatsGui.Utils.translateTitle("Jesetzte Klötzje"));
        translations.put("blocks_placed.desc", """
                <color:#8a9fff>%s</color><white> hät bis jäz %s Klötzje jesatz</white>
                <white><green>»</green> %s <gray>Klötzje</gray></white>
                """);

        translations.put("blocks_destroyed", StatsGui.Utils.translateTitle("Kapottjemaat Klötzje"));
        translations.put("blocks_destroyed.desc", """
                <color:#8a9fff>%s</color><white> hät bis jäz %s Klötzje kapott jemaat</white>
                <white><green>»</green> %s <gray>Klötzje</gray></white>
                """);

        return translations;
    }
}