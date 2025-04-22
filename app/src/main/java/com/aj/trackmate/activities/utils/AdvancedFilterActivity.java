package com.aj.trackmate.activities.utils;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.aj.trackmate.R;
import com.aj.trackmate.activities.game.statistics.GameStatisticsActivity;
import com.aj.trackmate.managers.filter.FilterBarManager;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_GAME_STATISTICS;

public class AdvancedFilterActivity extends AppCompatActivity {

    private FilterBarManager filterBarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_filter);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the platform name from the Intent
        String platform = getIntent().getStringExtra("CATEGORY");
        Log.d("Advanced Filter", "Platform: " + platform);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(platform + " Filters");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        filterBarManager = new FilterBarManager(this, findViewById(R.id.filterBarContainer), platform);

        findViewById(R.id.clearFiltersButton).setOnClickListener(v -> {
            filterBarManager.clearFilters();
        });

        findViewById(R.id.applyFiltersButton).setOnClickListener(v -> {
            Intent resultIntent = filterBarManager.createFilterResultIntent();
            setResult(RESULT_OK, resultIntent);
            finish();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}