package com.aj.trackmate.models.entertainment;

public enum Language {
    ENGLISH("English"),
    HINDI("Hindi"),
    KANNADA("Kannada"),
    TELUGU("Telugu"),
    TAMIL("Tamil"),
    MALAYALAM("Malayalam"),
    OTHER("Other");

    private String language;

    Language(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    // Method to find Genre by genre
    public static Language fromLanguage(String platformLanguage) {
        for (Language language : values()) {
            if (language.getLanguage().equalsIgnoreCase(platformLanguage)) {
                return language;
            }
        }
        throw new IllegalArgumentException("No enum constant with language: " + platformLanguage);
    }
}
