package com.aj.trackmate.models.entertainment;

import java.util.HashMap;
import java.util.Map;

public enum TelevisionSeriesStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String status;

    private static final Map<TelevisionSeriesStatus, Integer> STATUS_PRIORITY = new HashMap<>() {{
        put(TelevisionSeriesStatus.IN_PROGRESS, 1);
        put(TelevisionSeriesStatus.NOT_STARTED, 2);
        put(TelevisionSeriesStatus.COMPLETED, 3);
    }};

    TelevisionSeriesStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Map<TelevisionSeriesStatus, Integer> getStatusPriority() {
        return TelevisionSeriesStatus.STATUS_PRIORITY;
    }

    // Method to find EntertainmentStatus by status
    public static TelevisionSeriesStatus fromStatus(String status) {
        for (TelevisionSeriesStatus bookStatus : values()) {
            if (bookStatus.getStatus().equalsIgnoreCase(status)) {
                return bookStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with status: " + status);
    }
}
