package com.aj.trackmate.models.game;

import java.util.HashMap;
import java.util.Map;

public enum GameStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    STORY_COMPLETED("Story Completed"),
    STORY_WITH_DLC_COMPLETED("Story with DLC Completed"),
    FULLY_COMPLETED("Fully Completed");

    private final String status;

    private static final Map<GameStatus, Integer> STATUS_PRIORITY = new HashMap<>() {{
        put(GameStatus.IN_PROGRESS, 1);
        put(GameStatus.NOT_STARTED, 2);
        put(GameStatus.STORY_COMPLETED, 3);
        put(GameStatus.STORY_WITH_DLC_COMPLETED, 4);
        put(GameStatus.FULLY_COMPLETED, 5);
    }};

    GameStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Map<GameStatus, Integer> getStatusPriority() {
        return GameStatus.STATUS_PRIORITY;
    }

    // Method to find GameStatus by status
    public static GameStatus fromStatus(String status) {
        for (GameStatus gameStatus : values()) {
            if (gameStatus.getStatus().equalsIgnoreCase(status)) {
                return gameStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with status: " + status);
    }
}
