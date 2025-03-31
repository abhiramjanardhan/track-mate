package com.aj.trackmate.models.books;

public enum BookNoteStatus {
    NOT_APPLICABLE("Not Applicable"),
    STARTED("Started"),
    COMPLETED("Completed");

    private final String status;

    BookNoteStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // Method to find GameStatus by status
    public static BookNoteStatus fromStatus(String status) {
        for (BookNoteStatus bookStatus : values()) {
            if (bookStatus.getStatus().equalsIgnoreCase(status)) {
                return bookStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with status: " + status);
    }
}
