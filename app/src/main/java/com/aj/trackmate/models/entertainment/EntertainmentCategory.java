package com.aj.trackmate.models.entertainment;

public enum EntertainmentCategory {
    MOVIES("Movies"),
    MUSIC("Music"),
    TELEVISION_SERIES("Television Series");

    private String category;

    EntertainmentCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public static EntertainmentCategory fromCategory(String entertainmentCategory) {
        for (EntertainmentCategory entCategory : values()) {
            if (entCategory.getCategory().equalsIgnoreCase(entertainmentCategory)) {
                return entCategory;
            }
        }
        throw new IllegalArgumentException("No enum constant with category: " + entertainmentCategory);
    }
}
