package com.aj.trackmate.models.books;

public enum BookGenre {
    ACTION("Action"),
    CRIME("Crime"),
    THRILLER("Thriller"),
    FICTION("Fiction"),
    FANTASY("Fantasy"),
    BIOPIC("Biopic"),
    COMEDY("Comedy"),
    ADVENTURE("Adventure"),
    HORROR("Horror"),
    SCIFI("SciFi"),
    ROMANCE("Romance"),
    HISTORY("History");

    private String genre;

    BookGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    // Method to find GameStatus by status
    public static BookGenre fromGenre(String bookGenre) {
        for (BookGenre genre : values()) {
            if (genre.getGenre().equalsIgnoreCase(bookGenre)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("No enum constant with genre: " + bookGenre);
    }
}
