package com.aj.trackmate.managers.filter.game;

import com.aj.trackmate.R;
import com.aj.trackmate.managers.filter.FilterDefinition;
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.game.GamePurchaseMode;
import com.aj.trackmate.models.game.GamePurchaseType;
import com.aj.trackmate.models.game.GameStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameFilterDefinition extends FilterDefinition {

    public GameFilterDefinition() {
        super();
    }

    @Override
    public void defineFilters() {
        showStatus = true;
        showPurchaseMode = true;
        showPurchaseType = true;
        showCurrency = true;
        showFavorite = true;
        showBacklog = true;
        showWatchlist = true;
        showYear = true;
        showSorting = true;
    }

    @Override
    public void buildSpinnerOptions() {
        List<String> gameStatusFilters = new ArrayList<>();
        gameStatusFilters.add("All");
        gameStatusFilters.addAll(Arrays.stream(GameStatus.values()).map(GameStatus::getStatus).collect(Collectors.toList()));

        List<String> purchaseTypeFilters = new ArrayList<>();
        purchaseTypeFilters.add("All");
        purchaseTypeFilters.addAll(Arrays.stream(GamePurchaseType.values()).map(GamePurchaseType::getPurchaseType).collect(Collectors.toList()));

        List<String> purchaseModeFilters = new ArrayList<>();
        purchaseModeFilters.add("All");
        purchaseModeFilters.addAll(Arrays.stream(GamePurchaseMode.values()).map(GamePurchaseMode::getPurchaseMode).collect(Collectors.toList()));

        List<String> currencyFilters = new ArrayList<>();
        currencyFilters.add("All");
        currencyFilters.addAll(Arrays.stream(Currency.values()).map(Currency::getCurrency).collect(Collectors.toList()));

        List<String> booleanFilters = getBooleanFilters();

        spinnerOptions.put(R.id.statusFilterSpinner, gameStatusFilters);
        spinnerOptions.put(R.id.purchaseTypeFilterSpinner, purchaseTypeFilters);
        spinnerOptions.put(R.id.purchaseModeFilterSpinner, purchaseModeFilters);
        spinnerOptions.put(R.id.currencyFilterSpinner, currencyFilters);
        spinnerOptions.put(R.id.favoriteFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.watchlistFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.backlogFilterSpinner, booleanFilters);
        spinnerOptions.put(R.id.sortingSpinner, getSortingOptions());
    }

    @Override
    public List<String> getSortingOptions() {
        return Arrays.asList("Default", "Name", "Year", "Amount");
    }
}
