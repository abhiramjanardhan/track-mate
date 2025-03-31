package com.aj.trackmate.models.entertainment;

public enum MovieStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String status;

    MovieStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // Method to find EntertainmentStatus by status
    public static MovieStatus fromStatus(String status) {
        for (MovieStatus bookStatus : values()) {
            if (bookStatus.getStatus().equalsIgnoreCase(status)) {
                return bookStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with status: " + status);
    }
}
