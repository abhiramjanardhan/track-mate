package com.aj.trackmate.models.books;

public enum BookStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    STORY_COMPLETED("Story Completed"),
    COMPLETED("Completed");

    private final String status;

    BookStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // Method to find GameStatus by status
    public static BookStatus fromStatus(String status) {
        for (BookStatus bookStatus : values()) {
            if (bookStatus.getStatus().equalsIgnoreCase(status)) {
                return bookStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with status: " + status);
    }
}
