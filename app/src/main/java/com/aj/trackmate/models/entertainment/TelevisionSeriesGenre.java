package com.aj.trackmate.models.entertainment;

public enum TelevisionSeriesGenre {
    ACTION("Action"),
    THRILLER("Thriller"),
    CRIME("Crime"),
    FICTION("Fiction"),
    COMEDY("Comedy"),
    FANTASY("Fantasy"),
    BIOPIC("Biopic"),
    ADVENTURE("Adventure"),
    HORROR("Horror"),
    SCIFI("SciFi"),
    ROMANCE("Romance"),
    HISTORY("History");

    private String genre;

    TelevisionSeriesGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    // Method to find Genre by genre
    public static TelevisionSeriesGenre fromGenre(String genre) {
        for (TelevisionSeriesGenre movieGenre : values()) {
            if (movieGenre.getGenre().equalsIgnoreCase(genre)) {
                return movieGenre;
            }
        }
        throw new IllegalArgumentException("No enum constant with genre: " + genre);
    }
}
