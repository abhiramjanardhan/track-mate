package com.aj.trackmate.managers.filter.entertainment;

import com.aj.trackmate.R;
import com.aj.trackmate.managers.filter.FilterDefinition;
import com.aj.trackmate.models.entertainment.Language;
import com.aj.trackmate.models.entertainment.TelevisionSeriesGenre;
import com.aj.trackmate.models.entertainment.TelevisionSeriesStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TelevisionSeriesFilterDefinition extends FilterDefinition {
    @Override
    public void defineFilters() {
        showLanguage = true;
        showGenre = true;
        showFavorite = true;
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

        List<String> tvSeriesStatusFilters = new ArrayList<>();
        tvSeriesStatusFilters.add("All");
        tvSeriesStatusFilters.addAll(Arrays.stream(TelevisionSeriesStatus.values()).map(TelevisionSeriesStatus::getStatus).collect(Collectors.toList()));

        List<String> tvSeriesGenreFilters = new ArrayList<>();
        tvSeriesGenreFilters.add("All");
        tvSeriesGenreFilters.addAll(Arrays.stream(TelevisionSeriesGenre.values()).map(TelevisionSeriesGenre::getGenre).collect(Collectors.toList()));

        List<String> booleanFilters = getBooleanFilters();

        spinnerOptions.put(R.id.languageFilterSpinner, languageFilters);
        spinnerOptions.put(R.id.statusFilterSpinner, tvSeriesStatusFilters);
        spinnerOptions.put(R.id.genreFilterSpinner, tvSeriesGenreFilters);
        spinnerOptions.put(R.id.favoriteFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.backlogFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.watchlistFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.sortingSpinner, getSortingOptions());
    }

    @Override
    public List<String> getSortingOptions() {
        return Arrays.asList("Default", "Name", "Language");
    }
}
