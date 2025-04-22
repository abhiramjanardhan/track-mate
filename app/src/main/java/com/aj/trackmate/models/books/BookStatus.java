package com.aj.trackmate.models.books;

import java.util.HashMap;
import java.util.Map;

public enum BookStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    STORY_COMPLETED("Story Completed"),
    COMPLETED("Completed");

    private final String status;

    private static final Map<BookStatus, Integer> STATUS_PRIORITY = new HashMap<>() {{
        put(BookStatus.IN_PROGRESS, 1);
        put(BookStatus.NOT_STARTED, 2);
        put(BookStatus.STORY_COMPLETED, 3);
        put(BookStatus.COMPLETED, 4);
    }};

    BookStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Map<BookStatus, Integer> getStatusPriority() {
        return BookStatus.STATUS_PRIORITY;
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
