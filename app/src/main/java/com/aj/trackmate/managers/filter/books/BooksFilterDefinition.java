package com.aj.trackmate.managers.filter.books;

import com.aj.trackmate.R;
import com.aj.trackmate.managers.filter.FilterDefinition;
import com.aj.trackmate.models.books.BookGenre;
import com.aj.trackmate.models.books.BookStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BooksFilterDefinition extends FilterDefinition {

    public BooksFilterDefinition() {
        super();
    }

    @Override
    public void defineFilters() {
        showStatus = true;
        showGenre = true;
        showWatchlist = true;
        showBacklog = true;
        showSorting = true;
    }

    @Override
    public void buildSpinnerOptions() {
        List<String> booksStatusFilters = new ArrayList<>();
        booksStatusFilters.add("All");
        booksStatusFilters.addAll(Arrays.stream(BookStatus.values()).map(BookStatus::getStatus).collect(Collectors.toList()));

        List<String> booksGenreFilters = new ArrayList<>();
        booksGenreFilters.add("All");
        booksGenreFilters.addAll(Arrays.stream(BookGenre.values()).map(BookGenre::getGenre).collect(Collectors.toList()));

        List<String> booleanFilters = getBooleanFilters();

        spinnerOptions.put(R.id.statusFilterSpinner, booksStatusFilters);
        spinnerOptions.put(R.id.genreFilterSpinner, booksGenreFilters);
        spinnerOptions.put(R.id.backlogFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.watchlistFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.sortingSpinner, getSortingOptions());
    }

    @Override
    public List<String> getSortingOptions() {
        return Arrays.asList("Default", "Name");
    }
}
