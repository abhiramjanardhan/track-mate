package com.aj.trackmate.models.entertainment;

public enum TelevisionSeriesStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String status;

    TelevisionSeriesStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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
