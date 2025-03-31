package com.aj.trackmate.models.entertainment;

public enum MusicPlatform {
    SPOTIFY("Spotify Music"),
    APPLE_MUSIC("Apple Music"),
    YOU_TUBE_MUSIC("YouTube Music"),
    AMAZON_PRIME_MUSIC("Amazon Prime Music"),
    OTHER("Other Music");

    private String platform;

    MusicPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return this.platform;
    }

    // Method to find Genre by genre
    public static MusicPlatform fromPlatform(String moviePlatform) {
        for (MusicPlatform platform : values()) {
            if (platform.getPlatform().equalsIgnoreCase(moviePlatform)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("No enum constant with platform: " + moviePlatform);
    }
}
