package com.aj.trackmate.managers.filter.entertainment;

import com.aj.trackmate.R;
import com.aj.trackmate.managers.filter.FilterDefinition;
import com.aj.trackmate.models.entertainment.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MusicFilterDefinition extends FilterDefinition {
    @Override
    public void defineFilters() {
        showLanguage = true;
        showFavorite = true;
        showSorting = true;
    }

    @Override
    public void buildSpinnerOptions() {
        List<String> languageFilters = new ArrayList<>();
        languageFilters.add("All");
        languageFilters.addAll(Arrays.stream(Language.values()).map(Language::getLanguage).collect(Collectors.toList()));

        List<String> booleanFilters = getBooleanFilters();

        spinnerOptions.put(R.id.languageFilterSpinner, languageFilters);
        spinnerOptions.put(R.id.favoriteFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.sortingSpinner, getSortingOptions());
    }

    @Override
    public List<String> getSortingOptions() {
        return Arrays.asList("Default", "Name", "Language");
    }
}
