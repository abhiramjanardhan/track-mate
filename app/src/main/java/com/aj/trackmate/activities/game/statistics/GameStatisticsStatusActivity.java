package com.aj.trackmate.activities.game.statistics;

import android.annotation.SuppressLint;
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
import com.aj.trackmate.adapters.game.statistics.StatisticsDetailsAdapter;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.game.GameStatus;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.view.factory.ViewModelFactory;
import com.aj.trackmate.models.view.games.GameStatisticsViewModel;

import static com.aj.trackmate.adapters.game.statistics.StatisticsDetailsAdapter.STATISTICS_STATUS;

public class GameStatisticsStatusActivity extends AppCompatActivity {

    private StatisticsDetailsAdapter detailsAdapter;
    private TextView statisticsDetailTitle, statisticsDetailEmptyMessage;
    private RecyclerView statisticsDetailRecyclerView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_statistics_status);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        statisticsDetailTitle = findViewById(R.id.statisticsDetailTitle);
        statisticsDetailEmptyMessage = findViewById(R.id.statisticsDetailEmptyMessage);
        statisticsDetailRecyclerView = findViewById(R.id.statisticsDetailRecyclerView);

        statisticsDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the platform name from the Intent
        Platform platform = Platform.fromName(getIntent().getStringExtra("GAME_PLATFORM"));
        GameStatus status = GameStatus.fromStatus(getIntent().getStringExtra("GAME_STATUS"));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(platform.getName());  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // ViewModel
        ViewModelFactory factory = new ViewModelFactory(this, this);
        GameStatisticsViewModel viewModel = new ViewModelProvider(this, factory).get(GameStatisticsViewModel.class);

        statisticsDetailTitle.setText("Games in Status: " + status.getStatus());

        viewModel.getGamesByStatus(platform, status).observe(this, games -> {
            if (games == null || games.isEmpty()) {
                statisticsDetailEmptyMessage.setVisibility(View.VISIBLE);
                statisticsDetailRecyclerView.setVisibility(View.GONE);
            } else {
                statisticsDetailEmptyMessage.setVisibility(View.GONE);
                statisticsDetailRecyclerView.setVisibility(View.VISIBLE);

                detailsAdapter = new StatisticsDetailsAdapter(this, games, STATISTICS_STATUS);
                statisticsDetailRecyclerView.setAdapter(detailsAdapter);
                detailsAdapter.updateGames(games);  // Notify adapter of new data
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