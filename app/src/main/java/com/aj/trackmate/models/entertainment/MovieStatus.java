package com.aj.trackmate.models.entertainment;

import java.util.HashMap;
import java.util.Map;

public enum MovieStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String status;

    private static final Map<MovieStatus, Integer> STATUS_PRIORITY = new HashMap<>() {{
        put(MovieStatus.IN_PROGRESS, 1);
        put(MovieStatus.NOT_STARTED, 2);
        put(MovieStatus.COMPLETED, 3);
    }};

    MovieStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Map<MovieStatus, Integer> getStatusPriority() {
        return MovieStatus.STATUS_PRIORITY;
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
