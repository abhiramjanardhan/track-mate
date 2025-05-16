package com.aj.trackmate.managers.filter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import com.aj.trackmate.R;

import java.util.*;

public class FilterBarManager {
    private final Context context;
    private final View rootView;
    private final String platform;
    private FilterDefinition filterDefinition;

    public static final String FILTER_STATUS = "status";
    public static final String FILTER_PURCHASE_TYPE = "purchaseType";
    public static final String FILTER_PURCHASE_MODE = "purchaseMode";
    public static final String FILTER_CURRENCY = "currency";
    public static final String FILTER_LANGUAGE = "language";
    public static final String FILTER_GENRE = "genre";
    public static final String FILTER_FAVORITE = "favorite";
    public static final String FILTER_BACKLOG = "backlog";
    public static final String FILTER_WATCHLIST = "watchlist";
    public static final String FILTER_YEAR = "year";
    public static final String FILTER_SORTING = "sorting";
    public static final String FILTER_DESCENDING_ORDER = "descendingOrder";

    public static final String FILTER_SWITCH_VALUE_YES = "Yes";
    public static final String FILTER_SWITCH_VALUE_NO = "No";

    public FilterBarManager(Context context, View view, String platform) {
        this.context = context;
        this.rootView = view;
        this.platform = platform;
        this.filterDefinition = FilterDefinition.getFilterDefinition(platform);
        assert filterDefinition != null;
        applyDefinition(filterDefinition);
    }

    public static List<String> getAllFilterKeys() {
        return Arrays.asList(
                FILTER_STATUS,
                FILTER_PURCHASE_TYPE,
                FILTER_PURCHASE_MODE,
                FILTER_CURRENCY,
                FILTER_BACKLOG,
                FILTER_FAVORITE,
                FILTER_WATCHLIST,
                FILTER_YEAR,
                FILTER_LANGUAGE,
                FILTER_GENRE,
                FILTER_SORTING,
                FILTER_DESCENDING_ORDER
        );
    }

    public void applyDefinition(FilterDefinition def) {
        toggleVisibility(R.id.statusFilterRow, def.showStatus);
        toggleVisibility(R.id.purchaseTypeFilterRow, def.showPurchaseType);
        toggleVisibility(R.id.purchaseModeFilterRow, def.showPurchaseMode);
        toggleVisibility(R.id.currencyFilterRow, def.showCurrency);
        toggleVisibility(R.id.languageFilterRow, def.showLanguage);
        toggleVisibility(R.id.genreFilterRow, def.showGenre);
        toggleVisibility(R.id.favoriteFilterRow, def.showFavorite);
        toggleVisibility(R.id.watchlistFilterRow, def.showWatchlist);
        toggleVisibility(R.id.backlogFilterRow, def.showBacklog);
        toggleVisibility(R.id.yearFilterRow, def.showYear);
        toggleVisibility(R.id.sortingSpinner, def.showSorting);
        toggleVisibility(R.id.descendingOrderSwitch, def.showSorting);

        for (Map.Entry<Integer, List<String>> entry : def.spinnerOptions.entrySet()) {
            Spinner spinner = rootView.findViewById(entry.getKey());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, entry.getValue());
            spinner.setAdapter(adapter);
        }
    }

    private void setSpinnerSelection(int spinnerId, String value) {
        Spinner spinner = rootView.findViewById(spinnerId);
        if (spinner != null && value != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(value);
                if (position >= 0) {
                    spinner.setSelection(position);
                }
            }
        }
    }

    public void restoreSelections(Map<String, String> selectedFilters) {
        setSpinnerSelection(R.id.statusFilterSpinner, selectedFilters.get(FILTER_STATUS));
        setSpinnerSelection(R.id.purchaseTypeFilterSpinner, selectedFilters.get(FILTER_PURCHASE_TYPE));
        setSpinnerSelection(R.id.purchaseModeFilterSpinner, selectedFilters.get(FILTER_PURCHASE_MODE));
        setSpinnerSelection(R.id.languageFilterSpinner, selectedFilters.get(FILTER_LANGUAGE));
        setSpinnerSelection(R.id.genreFilterSpinner, selectedFilters.get(FILTER_GENRE));
        setSpinnerSelection(R.id.currencyFilterSpinner, selectedFilters.get(FILTER_CURRENCY));
        setSpinnerSelection(R.id.favoriteFilterSpinner, selectedFilters.get(FILTER_FAVORITE));
        setSpinnerSelection(R.id.watchlistFilterSpinner, selectedFilters.get(FILTER_WATCHLIST));
        setSpinnerSelection(R.id.backlogFilterSpinner, selectedFilters.get(FILTER_BACKLOG));
        setSpinnerSelection(R.id.sortingSpinner, selectedFilters.get(FILTER_SORTING));

        EditText yearEditText = rootView.findViewById(R.id.yearFilterText);
        if (yearEditText != null && selectedFilters.get(FILTER_YEAR) != null) {
            yearEditText.setText(selectedFilters.get(FILTER_YEAR));
        }

        Switch descendingOrderSwitch = rootView.findViewById(R.id.descendingOrderSwitch);
        if (descendingOrderSwitch != null && selectedFilters.get(FILTER_DESCENDING_ORDER) != null) {
            descendingOrderSwitch.setChecked(Objects.requireNonNull(selectedFilters.get(FILTER_DESCENDING_ORDER)).equalsIgnoreCase(FILTER_SWITCH_VALUE_YES));
        }
    }

    private void toggleVisibility(int viewId, boolean show) {
        View view = rootView.findViewById(viewId);
        if (view != null) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void clearFilters() {
        clearSpinner(R.id.statusFilterSpinner);
        clearSpinner(R.id.purchaseTypeFilterSpinner);
        clearSpinner(R.id.purchaseModeFilterSpinner);
        clearSpinner(R.id.currencyFilterSpinner);
        clearSpinner(R.id.languageFilterSpinner);
        clearSpinner(R.id.genreFilterSpinner);
        clearSpinner(R.id.favoriteFilterSpinner);
        clearSpinner(R.id.watchlistFilterSpinner);
        clearSpinner(R.id.backlogFilterSpinner);
        clearSpinner(R.id.sortingSpinner);

        EditText yearEditText = rootView.findViewById(R.id.yearFilterText);
        if (yearEditText != null) {
            yearEditText.setText("");
        }

        Switch descendingOrderSwitch = rootView.findViewById(R.id.descendingOrderSwitch);
        if (descendingOrderSwitch != null) {
            descendingOrderSwitch.setChecked(false);
        }
    }

    private void clearSpinner(int spinnerId) {
        Spinner spinner = rootView.findViewById(spinnerId);
        if (spinner != null && spinner.getAdapter() != null && spinner.getAdapter().getCount() > 0) {
            spinner.setSelection(0);
        }
    }

    public Map<String, String> getSelectedFilters() {
        Map<String, String> filters = new HashMap<>();

        filters.put(FILTER_STATUS, getSpinnerValue(R.id.statusFilterSpinner));
        filters.put(FILTER_PURCHASE_TYPE, getSpinnerValue(R.id.purchaseTypeFilterSpinner));
        filters.put(FILTER_PURCHASE_MODE, getSpinnerValue(R.id.purchaseModeFilterSpinner));
        filters.put(FILTER_CURRENCY, getSpinnerValue(R.id.currencyFilterSpinner));
        filters.put(FILTER_LANGUAGE, getSpinnerValue(R.id.languageFilterSpinner));
        filters.put(FILTER_GENRE, getSpinnerValue(R.id.genreFilterSpinner));
        filters.put(FILTER_WATCHLIST, getSpinnerValue(R.id.watchlistFilterSpinner));
        filters.put(FILTER_FAVORITE, getSpinnerValue(R.id.favoriteFilterSpinner));
        filters.put(FILTER_BACKLOG, getSpinnerValue(R.id.backlogFilterSpinner));
        filters.put(FILTER_SORTING, getSpinnerValue(R.id.sortingSpinner));
        filters.put(FILTER_DESCENDING_ORDER, getSwitchValue(R.id.descendingOrderSwitch));

        EditText yearEditText = rootView.findViewById(R.id.yearFilterText);
        filters.put(FILTER_YEAR, yearEditText != null ? yearEditText.getText().toString().trim() : "");

        return filters;
    }

    private String getSpinnerValue(int spinnerId) {
        Spinner spinner = rootView.findViewById(spinnerId);
        return spinner != null && spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : "";
    }

    private String getSwitchValue(int switchId) {
        Switch fieldSwitch = rootView.findViewById(switchId);
        return fieldSwitch != null ? (fieldSwitch.isChecked() ? FILTER_SWITCH_VALUE_YES : FILTER_SWITCH_VALUE_NO) : FILTER_SWITCH_VALUE_NO;
    }

    public Intent createFilterResultIntent() {
        Intent result = new Intent();
        Map<String, String> filters = getSelectedFilters();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            result.putExtra(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static boolean isSwitchOn(String filterValue) {
        return filterValue != null && filterValue.equalsIgnoreCase(FILTER_SWITCH_VALUE_YES);
    }

    public static Map<String, String> extractFiltersFromIntent(Intent intent) {
        Map<String, String> filters = new HashMap<>();
        filters.put(FILTER_STATUS, intent.getStringExtra(FILTER_STATUS));
        filters.put(FILTER_PURCHASE_TYPE, intent.getStringExtra(FILTER_PURCHASE_TYPE));
        filters.put(FILTER_PURCHASE_MODE, intent.getStringExtra(FILTER_PURCHASE_MODE));
        filters.put(FILTER_CURRENCY, intent.getStringExtra(FILTER_CURRENCY));
        filters.put(FILTER_WATCHLIST, intent.getStringExtra(FILTER_WATCHLIST));
        filters.put(FILTER_LANGUAGE, intent.getStringExtra(FILTER_LANGUAGE));
        filters.put(FILTER_GENRE, intent.getStringExtra(FILTER_GENRE));
        filters.put(FILTER_FAVORITE, intent.getStringExtra(FILTER_FAVORITE));
        filters.put(FILTER_BACKLOG, intent.getStringExtra(FILTER_BACKLOG));
        filters.put(FILTER_YEAR, intent.getStringExtra(FILTER_YEAR));
        filters.put(FILTER_SORTING, intent.getStringExtra(FILTER_SORTING));
        filters.put(FILTER_DESCENDING_ORDER, intent.getStringExtra(FILTER_DESCENDING_ORDER));
        return filters;
    }
}
