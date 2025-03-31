package com.aj.trackmate.models.books;

public enum BookFor {
    READING("Reading"),
    WRITING("Writing");

    private String bookFor;

    BookFor(String bookFor) {
        this.bookFor = bookFor;
    }

    public String getBookFor() {
        return this.bookFor;
    }

    // Method to find BookFor by genre
    public static BookFor fromGenre(String bookFor) {
        for (BookFor forBook : values()) {
            if (forBook.getBookFor().equalsIgnoreCase(bookFor)) {
                return forBook;
            }
        }
        throw new IllegalArgumentException("No enum constant with book for: " + bookFor);
    }
}
