package com.aj.trackmate.models.game;

import static com.aj.trackmate.constants.CategoryConstants.*;

public enum Platform {
    PLAYSTATION(GAME_PLAY_STATION),
    NINTENDO(GAME_NINTENDO),
    XBOX(GAME_XBOX),
    PC(GAME_PC);

    private final String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Method to find Platform by name
    public static Platform fromName(String name) {
        for (Platform platform : values()) {
            if (platform.getName().equalsIgnoreCase(name)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("No enum constant with name: " + name);
    }
}
