package com.aj.trackmate.models.entertainment;

public enum MovieGenre {
    ACTION("Action"),
    THRILLER("Thriller"),
    ADVENTURE("Adventure"),
    CRIME("Crime"),
    COMEDY("Comedy"),
    FICTION("Fiction"),
    FANTASY("Fantasy"),
    BIOPIC("Biopic"),
    HORROR("Horror"),
    SCIFI("SciFi"),
    ROMANCE("Romance"),
    HISTORY("History");

    private String genre;

    MovieGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    // Method to find Genre by genre
    public static MovieGenre fromGenre(String genre) {
        for (MovieGenre movieGenre : values()) {
            if (movieGenre.getGenre().equalsIgnoreCase(genre)) {
                return movieGenre;
            }
        }
        throw new IllegalArgumentException("No enum constant with genre: " + genre);
    }
}
