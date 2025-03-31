package com.aj.trackmate.models.entertainment;

public enum TelevisionSeriesPlatform {
    NETFLIX("Netflix TV Series"),
    AMAZON_PRIME("Amazon Prime TV Series"),
    APPLE_PLUS("Apple Plus TV Series"),
    HOT_STAR("Hot Star TV Series"),
    ZEE_5("Zee5 TV Series"),
    SONY_LIV("Sony Liv TV Series"),
    PARAMOUNT_PLUS("Paramount Plus TV Series"),
    OTHER("Other TV Series");

    private String platform;

    TelevisionSeriesPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return this.platform;
    }

    // Method to find Genre by genre
    public static TelevisionSeriesPlatform fromPlatform(String moviePlatform) {
        for (TelevisionSeriesPlatform platform : values()) {
            if (platform.getPlatform().equalsIgnoreCase(moviePlatform)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("No enum constant with platform: " + moviePlatform);
    }
}
