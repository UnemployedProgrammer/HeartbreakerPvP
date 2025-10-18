package com.sebastian.heartbreaker_pvp.translations;

import com.sebastian.heartbreaker_pvp.StatsGui;

import java.util.HashMap;

public class RussiaRussianLanguage extends Language {
    @Override
    public String getCode() {
        return "ru_ru";
    }

    @Override
    public String getName() {
        return "Русский (Россия)";
    }

    @Override
    protected HashMap<String, String> getTranslations() {
        HashMap<String, String> translations = new HashMap<>();
        translations.put("player_not_found_or_not_whitelisted", "Игрок не найден или не в белом списке!");
        translations.put("stats_for_player", "Статистика для %s");
        translations.put("close", "<red><b>Закрыть</b></red>");

        translations.put("current_health", StatsGui.Utils.translateTitle("Текущее Здоровье"));
        translations.put("current_health.desc", """
                <color:#8a9fff>Здоровье</color><white> %s</white>
                <white><green>»</green> %s/%s <gray>Очки</gray></white>
                <white><green>»</green> %s <gray>Поглощение</gray></white>
                """); // Hinweis: Die Position des Platzhalters wurde im Russischen leicht angepasst.

        translations.put("hero_hearts", StatsGui.Utils.translateTitle("Текущие Геро-Сердца"));
        translations.put("hero_hearts.desc", """
                <color:#8a9fff>Геро-Сердца</color><white> %s</white>
                <white><green>»</green> %s/3 <gray>Геро-Сердца</gray></white>
                """); // Hinweis: Die Position des Platzhalters wurde im Russischen leicht angepasst.

        translations.put("kills", StatsGui.Utils.translateTitle("Убийства"));
        translations.put("kills.desc", """
                <color:#8a9fff>Убийства</color><white> %s на данный момент</white>
                <white><green>»</green> %s <gray>Всего</gray></white>
                <white><green>»</green> %s <gray>Игроков</gray></white>
                """); // Hinweis: Die Position des Platzhalters wurde im Russischen leicht angepasst.

        translations.put("experience", StatsGui.Utils.translateTitle("Текущий Опыт"));
        translations.put("experience.desc", """
                <color:#8a9fff>Опыт</color><white> %s</white>
                <white><green>»</green> %s <gray>Уровень</gray></white>
                <white><green>»</green> %s% <gray>Собрано для Следующего Уровня</gray></white>
                """); // Hinweis: Die Position des Platzhalters wurde im Russischen leicht angepasst.

        translations.put("deaths", StatsGui.Utils.translateTitle("Смерти"));
        translations.put("deaths.desc", """
                <color:#8a9fff>Количество смертей</color><white> %s на данный момент</white>
                <white><green>»</green> %s <gray>Смертей</gray></white>
                """); // Hinweis: Die Position des Platzhalters wurde im Russischen leicht angepasst.

        translations.put("blocks_placed", StatsGui.Utils.translateTitle("Установленные Блоки"));
        translations.put("blocks_placed.desc", """
                <color:#8a9fff>Установленные блоки</color><white> %s на данный момент</white>
                <white><green>»</green> %s <gray>Блоков</gray></white>
                """); // Hinweis: Die Position des Platzhalters wurde im Russischen leicht angepasst.

        translations.put("blocks_destroyed", StatsGui.Utils.translateTitle("Разрушенные Блоки"));
        translations.put("blocks_destroyed.desc", """
                <color:#8a9fff>Разрушенные блоки</color><white> %s на данный момент</white>
                <white><green>»</green> %s <gray>Блоков</gray></white>
                """); // Hinweis: Die Position des Platzhalters wurde im Russischen leicht angepasst.

        return translations;
    }
}