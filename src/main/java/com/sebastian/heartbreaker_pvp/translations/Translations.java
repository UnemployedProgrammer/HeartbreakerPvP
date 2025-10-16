package com.sebastian.heartbreaker_pvp.translations;

import org.bukkit.entity.Player;

import java.util.Objects;

public class Translations {

    public static final Language FALLBACK = new Language() {
        @Override
        public String getCode() {
            return "en_us";
        }

        @Override
        public String getName() {
            return "English (Fallback, Error occurred)";
        }

        @Override
        public String getString(String translationCode) {
            return "Please choose your language again using /language";
        }
    };

    public static String getString(Language lang, String key) {
        return lang.getString(key);
    }

    public static String getString(Language lang, String key, Object... args) {
        String raw = lang.getString(key);
        if (raw == null) return key; // Fallback, falls Schlüssel fehlt

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
        if(Objects.equals(code, "not_set")) return FALLBACK;

        return FALLBACK;
    }
}
