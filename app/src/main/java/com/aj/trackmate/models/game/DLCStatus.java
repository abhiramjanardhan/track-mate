package com.aj.trackmate.models.game;

public enum DLCStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    FULLY_COMPLETED("Fully Completed");

    private final String status;

    DLCStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // Method to find GameStatus by status
    public static DLCStatus fromStatus(String status) {
        for (DLCStatus gameStatus : values()) {
            if (gameStatus.getStatus().equalsIgnoreCase(status)) {
                return gameStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with status: " + status);
    }
}
