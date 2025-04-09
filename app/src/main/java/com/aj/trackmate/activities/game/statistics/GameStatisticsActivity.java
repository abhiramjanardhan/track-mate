package com.aj.trackmate.activities.game.statistics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.game.statistics.StatisticsCurrencyAdapter;
import com.aj.trackmate.adapters.game.statistics.StatisticsStatusAdapter;
import com.aj.trackmate.adapters.game.statistics.StatisticsYearAdapter;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.view.factory.ViewModelFactory;
import com.aj.trackmate.models.view.games.GameStatisticsViewModel;

import static com.aj.trackmate.constants.RequestCodeConstants.*;

public class GameStatisticsActivity extends AppCompatActivity {

    private TextView totalGamesTextView;
    private TextView totalAmountTextView;

    private TextView statusEmptyMessage, yearEmptyMessage, currencyEmptyMessage;
    private RecyclerView currencyRecyclerView, statusRecyclerView, yearRecyclerView;

    private StatisticsCurrencyAdapter currencyAdapter;
    private StatisticsStatusAdapter statusAdapter;
    private StatisticsYearAdapter yearAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_statistics);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the platform name from the Intent
        String category = getIntent().getStringExtra("CATEGORY");
        Platform platform = Platform.fromName(category);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(platform.getName());  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize views
        totalGamesTextView = findViewById(R.id.valueTotalGames);
        currencyRecyclerView = findViewById(R.id.amountCurrencyRecyclerView);
        statusRecyclerView = findViewById(R.id.statusRecyclerView);
        yearRecyclerView = findViewById(R.id.yearRecyclerView);

        currencyEmptyMessage = findViewById(R.id.currencyDistributionEmptyMessage);
        statusEmptyMessage = findViewById(R.id.statusDistributionEmptyMessage);
        yearEmptyMessage = findViewById(R.id.yearDistributionEmptyMessage);

        currencyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        yearRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ViewModel
        ViewModelFactory factory = new ViewModelFactory(this, this);
        GameStatisticsViewModel viewModel = new ViewModelProvider(this, factory).get(GameStatisticsViewModel.class);

        GameDatabase.getInstance(this).gameDao().getTotalGamesCount(platform).observe(this, totalGames -> {
            totalGamesTextView.setText(String.valueOf(totalGames));
        });

        GameDatabase.getInstance(this).gameDao().getTotalAmountSpent(platform).observe(this, amountWithCurrencies -> {
            if (amountWithCurrencies == null || amountWithCurrencies.isEmpty()) {
                currencyEmptyMessage.setVisibility(View.VISIBLE);
                currencyRecyclerView.setVisibility(View.GONE);
            } else {
                currencyEmptyMessage.setVisibility(View.GONE);
                currencyRecyclerView.setVisibility(View.VISIBLE);

                currencyAdapter = new StatisticsCurrencyAdapter(this, platform, amountWithCurrencies, (currentPlatform, currency) -> {
                    Intent intent = new Intent(GameStatisticsActivity.this, GameStatisticsCurrencyActivity.class);
                    intent.putExtra("GAME_PLATFORM", currentPlatform.getName());
                    intent.putExtra("GAME_CURRENCY", currency.getCurrency());
                    startActivityForResult(intent, REQUEST_CODE_GAME_STATISTICS_CURRENCY);
                });
                currencyRecyclerView.setAdapter(currencyAdapter);
                currencyAdapter.updateGames(amountWithCurrencies);  // Notify adapter of new data
            }
        });

        GameDatabase.getInstance(this).gameDao().getGameCountByStatus(platform).observe(this, statusCounts -> {
            if (statusCounts == null || statusCounts.isEmpty()) {
                statusEmptyMessage.setVisibility(View.VISIBLE);
                statusRecyclerView.setVisibility(View.GONE);
            } else {
                statusEmptyMessage.setVisibility(View.GONE);
                statusRecyclerView.setVisibility(View.VISIBLE);

                statusAdapter = new StatisticsStatusAdapter(this, platform, statusCounts, (currentPlatform, status) -> {
                    Intent intent = new Intent(GameStatisticsActivity.this, GameStatisticsStatusActivity.class);
                    intent.putExtra("GAME_PLATFORM", currentPlatform.getName());
                    intent.putExtra("GAME_STATUS", status.getStatus());
                    startActivityForResult(intent, REQUEST_CODE_GAME_STATISTICS_STATUS);
                });
                statusRecyclerView.setAdapter(statusAdapter);
                statusAdapter.updateGames(statusCounts);  // Notify adapter of new data
            }
        });

        GameDatabase.getInstance(this).gameDao().getGameCountByYear(platform).observe(this, yearCounts -> {
            if (yearCounts == null || yearCounts.isEmpty()) {
                yearEmptyMessage.setVisibility(View.VISIBLE);
                yearRecyclerView.setVisibility(View.GONE);
            } else {
                yearEmptyMessage.setVisibility(View.GONE);
                yearRecyclerView.setVisibility(View.VISIBLE);

                yearAdapter = new StatisticsYearAdapter(this, platform, yearCounts, (currentPlatform, year) -> {
                    Intent intent = new Intent(GameStatisticsActivity.this, GameStatisticsYearActivity.class);
                    intent.putExtra("GAME_PLATFORM", currentPlatform.getName());
                    intent.putExtra("GAME_YEAR", year);
                    startActivityForResult(intent, REQUEST_CODE_GAME_STATISTICS_STATUS);
                });
                yearRecyclerView.setAdapter(yearAdapter);
                yearAdapter.updateGames(yearCounts);  // Notify adapter of new data
            }
        });
    }

    // Handle back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the back button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}