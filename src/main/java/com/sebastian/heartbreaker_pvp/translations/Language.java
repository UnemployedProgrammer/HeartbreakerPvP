package com.sebastian.heartbreaker_pvp.translations;

import java.util.HashMap;

public abstract class Language {

    protected HashMap<String, String> TRANSLATIONS;

    void init() {
        if(TRANSLATIONS == null) {
            TRANSLATIONS = getTranslations();
        }
    }

    public abstract String getCode();
    public abstract String getName();
    protected abstract HashMap<String, String> getTranslations();

    public String getString(String translationCode) {
        return TRANSLATIONS.getOrDefault(translationCode, translationCode);
    }
}
