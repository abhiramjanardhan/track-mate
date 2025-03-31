package com.aj.trackmate.models.game;

public enum GameStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    STORY_COMPLETED("Story Completed"),
    STORY_WITH_DLC_COMPLETED("Story with DLC Completed"),
    FULLY_COMPLETED("Fully Completed");

    private final String status;

    GameStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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
