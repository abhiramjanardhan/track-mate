package com.aj.trackmate.managers.filter;

import com.aj.trackmate.constants.CategoryConstants;
import com.aj.trackmate.managers.filter.books.BooksFilterDefinition;
import com.aj.trackmate.managers.filter.entertainment.MoviesFilterDefinition;
import com.aj.trackmate.managers.filter.entertainment.MusicFilterDefinition;
import com.aj.trackmate.managers.filter.entertainment.TelevisionSeriesFilterDefinition;
import com.aj.trackmate.managers.filter.game.GameFilterDefinition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FilterDefinition {
    public boolean showStatus = false;
    public boolean showPurchaseMode = false;
    public boolean showPurchaseType = false;
    public boolean showCurrency = false;
    public boolean showLanguage = false;
    public boolean showGenre = false;
    public boolean showFavorite = false;
    public boolean showWatchlist = false;
    public boolean showBacklog = false;
    public boolean showYear = false;
    public boolean showSorting = false;

    public Map<Integer, List<String>> spinnerOptions = new HashMap<>();

    public abstract void defineFilters();
    public abstract void buildSpinnerOptions();

    public FilterDefinition() {
        defineFilters();
        buildSpinnerOptions();
    }

    public List<String> getSortingOptions() {
        return Arrays.asList("Default", "Name");
    }

    protected List<String> getBooleanFilters() {
        return Arrays.asList("All", "Yes", "No");
    }

    public static FilterDefinition getFilterDefinition(String platform) {
        switch (platform) {
            case CategoryConstants.GAME_PLAY_STATION, CategoryConstants.GAME_XBOX, CategoryConstants.GAME_NINTENDO, CategoryConstants.GAME_PC -> {
                return new GameFilterDefinition();
            }
            case CategoryConstants.ENTERTAINMENT_MOVIES -> {
                return new MoviesFilterDefinition();
            }
            case CategoryConstants.ENTERTAINMENT_MUSIC -> {
                return new MusicFilterDefinition();
            }
            case CategoryConstants.ENTERTAINMENT_TV_SERIES -> {
                return new TelevisionSeriesFilterDefinition();
            }
            case CategoryConstants.BOOKS_READING, CategoryConstants.BOOKS_WRITING -> {
                return new BooksFilterDefinition();
            }
            default -> {}
        }
        return null;
    }
}
