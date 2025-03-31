package com.aj.trackmate.models.entertainment;

public enum MoviePlatform {
    THEATRE("Theatre Movies"),
    NETFLIX("Netflix Movies"),
    AMAZON_PRIME("Amazon Prime Movies"),
    APPLE_PLUS("Apple Plus Movies"),
    HOT_STAR("Hot Star Movies"),
    ZEE_5("Zee5 Movies"),
    SONY_LIV("Sony Liv Movies"),
    PARAMOUNT_PLUS("Paramount Plus Movies"),
    OTHER("Other Movies");

    private String platform;

    MoviePlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return this.platform;
    }

    // Method to find Genre by genre
    public static MoviePlatform fromPlatform(String moviePlatform) {
        for (MoviePlatform platform : values()) {
            if (platform.getPlatform().equalsIgnoreCase(moviePlatform)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("No enum constant with platform: " + moviePlatform);
    }
}
