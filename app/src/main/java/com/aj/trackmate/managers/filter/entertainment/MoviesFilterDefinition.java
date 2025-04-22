package com.aj.trackmate.managers.filter.entertainment;

import com.aj.trackmate.R;
import com.aj.trackmate.managers.filter.FilterDefinition;
import com.aj.trackmate.models.entertainment.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MoviesFilterDefinition extends FilterDefinition {

    public MoviesFilterDefinition() {
        super();
    }

    @Override
    public void defineFilters() {
        showLanguage = true;
        showGenre = true;
        showWatchlist = true;
        showBacklog = true;
        showStatus = true;
        showSorting = true;
    }

    @Override
    public void buildSpinnerOptions() {
        List<String> languageFilters = new ArrayList<>();
        languageFilters.add("All");
        languageFilters.addAll(Arrays.stream(Language.values()).map(Language::getLanguage).collect(Collectors.toList()));

        List<String> moviesStatusFilters = new ArrayList<>();
        moviesStatusFilters.add("All");
        moviesStatusFilters.addAll(Arrays.stream(MovieStatus.values()).map(MovieStatus::getStatus).collect(Collectors.toList()));

        List<String> moviesGenreFilters = new ArrayList<>();
        moviesGenreFilters.add("All");
        moviesGenreFilters.addAll(Arrays.stream(MovieGenre.values()).map(MovieGenre::getGenre).collect(Collectors.toList()));

        List<String> booleanFilters = getBooleanFilters();

        spinnerOptions.put(R.id.languageFilterSpinner, languageFilters);
        spinnerOptions.put(R.id.statusFilterSpinner, moviesStatusFilters);
        spinnerOptions.put(R.id.genreFilterSpinner, moviesGenreFilters);
        spinnerOptions.put(R.id.backlogFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.watchlistFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.sortingSpinner, getSortingOptions());
    }

    @Override
    public List<String> getSortingOptions() {
        return Arrays.asList("Default", "Name", "Language");
    }
}
