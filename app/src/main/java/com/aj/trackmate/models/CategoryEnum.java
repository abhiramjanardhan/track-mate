package com.aj.trackmate.models;

import com.aj.trackmate.models.entertainment.MoviePlatform;
import com.aj.trackmate.models.entertainment.MusicPlatform;
import com.aj.trackmate.models.entertainment.TelevisionSeriesPlatform;

import java.util.*;
import java.util.stream.Collectors;

import static com.aj.trackmate.constants.CategoryConstants.*;

public enum CategoryEnum {
    GAMING(
            Arrays.asList(GAME_PLAY_STATION, GAME_NINTENDO, GAME_XBOX, GAME_PC),
            Collections.emptyMap()
    ),
    ENTERTAINMENT(
            Arrays.asList(ENTERTAINMENT_MOVIES, ENTERTAINMENT_TV_SERIES, ENTERTAINMENT_MUSIC),
            Map.of(
                    ENTERTAINMENT_MOVIES, Arrays.stream(MoviePlatform.values()).map(MoviePlatform::getPlatform).collect(Collectors.toList()),
                    ENTERTAINMENT_MUSIC, Arrays.stream(MusicPlatform.values()).map(MusicPlatform::getPlatform).collect(Collectors.toList()),
                    ENTERTAINMENT_TV_SERIES, Arrays.stream(TelevisionSeriesPlatform.values()).map(TelevisionSeriesPlatform::getPlatform).collect(Collectors.toList())
            )
    ),
    BOOKS(
            Arrays.asList(BOOKS_READING,  BOOKS_WRITING),
            Collections.emptyMap()
    );

    private final List<String> items;
    private final Map<String, List<String>> subcategories;  // Key: Parent category item, Value: List of subcategories

    CategoryEnum(List<String> items, Map<String, List<String>> subcategories) {
        this.items = items;
        this.subcategories = subcategories;
    }

    public List<String> getItems() {
        return items;
    }

    public Map<String, List<String>> getSubcategories() {
        return subcategories;
    }

    public static CategoryEnum getCategoryByItem(String itemName) {
        for (CategoryEnum category : values()) {
            if (category.getItems().contains(itemName)) {
                return category; // Return the category if item matches
            }
        }
        return null; // Return null if no match is found
    }
}
