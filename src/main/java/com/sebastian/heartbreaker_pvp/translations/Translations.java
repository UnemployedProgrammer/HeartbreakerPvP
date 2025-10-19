package com.sebastian.heartbreaker_pvp.translations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Translations {
    public static final Language FALLBACK = new AmericanEnglishLanguage();
    private static ConcurrentHashMap<String, Language> translations = new ConcurrentHashMap<>();
    private static MiniMessage MM = MiniMessage.miniMessage();

    public static void registerLanguage(Language language) {
        translations.putIfAbsent(language.getCode(), language);
        language.init();
    }

    public static ConcurrentHashMap<String, Language> getLanguages() {
        return translations;
    }

    public static int getLanguageCount() {
        return translations.size();
    }

    public static String getString(Language lang, String key) {
        return lang.getString(key);
    }

    public static List<Component> getComponents(Language lang, String key) {
        String[] lines = getString(lang, key).split("\n");
        return Arrays.stream(lines)
                .map(str -> MM.deserialize(str))
                .toList();
    }

    public static List<Component> getComponents(Language lang, String key, Object... args) {
        String[] lines = getString(lang, key, args).split("\n");
        return Arrays.stream(lines)
                .map(str -> MM.deserialize(str))
                .toList();
    }

    public static Component getComponent(Language lang, String key) {
        return MM.deserialize(getString(lang, key));
    }

    public static Component getComponent(Language lang, String key, Object... args) {
        return MM.deserialize(getString(lang, key, args));
    }

    public static String getString(Language lang, String key, Object... args) {
        String raw = lang.getString(key);
        if (raw == null) return key;

        StringBuilder result = new StringBuilder();
        int argIndex = 0;

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '%' && i + 1 < raw.length() && raw.charAt(i + 1) == 's') {
                if (argIndex < args.length) {
                    result.append(String.valueOf(args[argIndex++]));
                } else {
                    result.append("%s");
                }
                i++;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static Language getLanguageFromCode(String code) {
        if(code == null) return FALLBACK;
        return translations.getOrDefault(code, FALLBACK);
    }
}
