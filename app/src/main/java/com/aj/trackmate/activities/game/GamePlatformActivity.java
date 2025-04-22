package com.aj.trackmate.activities.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.activities.game.statistics.GameStatisticsActivity;
import com.aj.trackmate.adapters.game.GameAdapter;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.managers.filter.FilterBarManager;
import com.aj.trackmate.managers.filter.FilterBottomSheetDialog;
import com.aj.trackmate.models.game.*;
import com.aj.trackmate.models.game.relations.GameWithDownloadableContent;
import com.aj.trackmate.operations.LongPressCallBack;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.aj.trackmate.operations.templates.ItemUpdateListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.aj.trackmate.constants.RequestCodeConstants.*;

public class GamePlatformActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener, ItemUpdateListener {

    private String platform;
    private ListView listView;
    private RecyclerView gamesRecyclerView;
    private GameAdapter gameAdapter;
    private List<GameWithDownloadableContent> games;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

    private EditText searchEditText;
    private List<GameWithDownloadableContent> allGames = new ArrayList<>();
    private Map<String, String> selectedFilters = new HashMap<>();

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gamesRecyclerView = findViewById(R.id.recyclerViewGames);
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        title = findViewById(R.id.categoryTitle);
        emptyStateMessage = findViewById(R.id.gamesEmptyStateMessage);
        addButton = findViewById(R.id.gamesFloatingButton);

        searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterGames(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.advancedGameFilters).setOnClickListener(v -> {
            FilterBottomSheetDialog bottomSheet = new FilterBottomSheetDialog(platform, selectedFilters, new FilterBottomSheetDialog.FilterListener() {
                @Override
                public void onApplyFilters(Map<String, String> filters) {
                    selectedFilters = filters;
                    applyFilters(filters); // Your existing method
                }

                @Override
                public void onClearFilters() {
                    gameAdapter.updateGames(allGames); // Reset
                    emptyStateMessage.setVisibility(allGames.isEmpty() ? View.VISIBLE : View.GONE);
                    gamesRecyclerView.setVisibility(allGames.isEmpty() ? View.GONE : View.VISIBLE);
                }
            });

            bottomSheet.show(getSupportFragmentManager(), "GameFilterBottomSheet");
        });

        title.setText("Games List");
        emptyStateMessage.setText("No Games Available");

        // Get the platform name from the Intent
        platform = getIntent().getStringExtra("CATEGORY");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(platform);  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Handle the "Add" button click
        addButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new game
            Intent intent = new Intent(this, AddGameActivity.class);
            intent.putExtra("CATEGORY", platform);
            startActivityForResult(intent, REQUEST_CODE_GAME_ADD); // Request code to identify the result
        });

        // Fetch the games based on the platform
        if (platform != null) {
            GameDatabase.getInstance(this).gameDao().getGamesWithDLCsByPlatform(Platform.fromName(platform)).observe(this, gameList -> {
                allGames = gameList;
                games = new ArrayList<>(allGames);
                Log.d("Games", "List: " + games);

                if (games == null || games.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    gamesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    gamesRecyclerView.setVisibility(View.VISIBLE);

                    gameAdapter = new GameAdapter(this, games, gameWithDownloadableContent -> {
                        Intent intent = new Intent(GamePlatformActivity.this, EditGameActivity.class);
                        intent.putExtra("GAME_ID", gameWithDownloadableContent.game.getId());
                        intent.putExtra("GAME_NAME", gameWithDownloadableContent.game.getName());
                        intent.putExtra("GAME_STATUS", gameWithDownloadableContent.game.getStatus().getStatus());
                        startActivityForResult(intent, REQUEST_CODE_GAME_EDIT);
                    }, (view, position) -> {
                        LongPressCallBack longPressCallBack = new LongPressCallBack(gameAdapter, this, this, this);
                        longPressCallBack.handleLongPress(view, position, "Game");
                    });
                    gamesRecyclerView.setAdapter(gameAdapter);
                    gameAdapter.updateGames(games);  // Notify adapter of new data
                    gameAdapter.sortGames();
                }
            });
        } else {
            games = new ArrayList<>();
            emptyStateMessage.setVisibility(View.VISIBLE);
            gamesRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Retrieve the new game from the result
            GameWithDownloadableContent newGame = null;
            if (requestCode == REQUEST_CODE_GAME_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newGame = data.getParcelableExtra("NEW_GAME", GameWithDownloadableContent.class);
                Log.d("Game Action", "Save:" + newGame);

                if (gameAdapter == null) {
                    gameAdapter = new GameAdapter(this, allGames, null, null);
                    gamesRecyclerView.setAdapter(gameAdapter);
                }

                // Add the new game to the list
                if (newGame != null) {
                    allGames.add(newGame);
                    gameAdapter.updateGames(allGames);  // Notify the adapter to refresh the RecyclerView
                    searchEditText.setText("");
                }

                Log.d("Game Action", "List count:" + allGames.size());

                // Update empty state visibility
                if (allGames.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    gamesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    gamesRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_GAME_EDIT) {
                int updatedGameId = data.getIntExtra("UPDATED_GAME_ID", -1);
                if (updatedGameId != -1) {
                    gameAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistics_menu, menu);
        return true;
    }

    // Handle back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the back button is pressed
            return true;
        } else if (item.getItemId() == R.id.action_statistics) {
            Intent intent = new Intent(this, GameStatisticsActivity.class);
            intent.putExtra("CATEGORY", platform);
            startActivityForResult(intent, REQUEST_CODE_GAME_STATISTICS);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Implement the removeItem method
    @Override
    public void removeItem(int position) {
        GameWithDownloadableContent gameWithDownloadableContent = games.get(position);
        Game game = gameWithDownloadableContent.game;
        List<DownloadableContent> dlcs = gameWithDownloadableContent.dlcs;
        gameAdapter.removeGame(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            // Assuming you have a game object and gameDao configured for database
            dlcs.forEach(dlc -> {
                GameDatabase.getInstance(this).gameDao().deleteDLC(dlc); // First delete all DLCs
            });
            GameDatabase.getInstance(this).gameDao().delete(game);  // Deleting the item from the database

            // Show a Toast on the main thread after the deletion is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }


    @Override
    public boolean isReadOnly(int position) {
        return false;
    }

    @Override
    public String getSavedItem(int position) {
        GameWithDownloadableContent gameWithDownloadableContent = games.get(position);
        return gameWithDownloadableContent.game.getStatus().getStatus();
    }

    @Override
    public List<String> getItems() {
        return Arrays.stream(GameStatus.values()).map(GameStatus::getStatus).collect(Collectors.toList());
    }

    @Override
    public void updateItem(int position, String value) {
        GameWithDownloadableContent gameWithDownloadableContent = games.get(position);
        Game game = gameWithDownloadableContent.game;

        // Perform database update in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            game.setStatus(GameStatus.fromStatus(value));
            GameDatabase.getInstance(this).gameDao().update(game);

            // Show a Toast on the main thread after the update is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void filterGames(String query) {
        String lower = query.toLowerCase();

        List<GameWithDownloadableContent> filtered = allGames.stream().filter(gameWithDLC -> {
            Game game = gameWithDLC.game;
            boolean matchesName = game.getName().toLowerCase().contains(lower);
            boolean matchesStatus = game.getStatus().getStatus().toLowerCase().contains(lower);
            boolean matchesPurchaseMode = game.getPurchaseMode().name().toLowerCase().contains(lower);
            boolean matchesPurchaseType = game.getPurchaseType().name().toLowerCase().contains(lower);
            boolean matchesCurrency = game.getCurrency().getCurrency().toLowerCase().contains(lower);

            return  matchesName || matchesStatus || matchesPurchaseMode || matchesPurchaseType || matchesCurrency;
        }).collect(Collectors.toList());

        gameAdapter.updateGames(filtered);

        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        gamesRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void applyFilters(Map<String, String> filters) {
        List<GameWithDownloadableContent> filtered = allGames.stream().filter(gameWithDLC -> {
            Game g = gameWithDLC.game;

            boolean status = Objects.equals(filters.get(FilterBarManager.FILTER_STATUS), "All") || g.getStatus().getStatus().equalsIgnoreCase(filters.get(FilterBarManager.FILTER_STATUS));
            boolean type = Objects.equals(filters.get(FilterBarManager.FILTER_PURCHASE_TYPE), "All") || g.getPurchaseType().getPurchaseType().equalsIgnoreCase(filters.get(FilterBarManager.FILTER_PURCHASE_TYPE));
            boolean mode = Objects.equals(filters.get(FilterBarManager.FILTER_PURCHASE_MODE), "All") || g.getPurchaseMode().getPurchaseMode().equalsIgnoreCase(filters.get(FilterBarManager.FILTER_PURCHASE_MODE));
            boolean currency = Objects.equals(filters.get(FilterBarManager.FILTER_CURRENCY), "All") || g.getCurrency().getCurrency().equalsIgnoreCase(filters.get(FilterBarManager.FILTER_CURRENCY));
            boolean backlog = Objects.equals(filters.get(FilterBarManager.FILTER_BACKLOG), "All") || Objects.requireNonNull(filters.get(FilterBarManager.FILTER_BACKLOG)).equalsIgnoreCase("Yes") == g.isBacklog();
            boolean watchlist = Objects.equals(filters.get(FilterBarManager.FILTER_WATCHLIST), "All") || Objects.requireNonNull(filters.get(FilterBarManager.FILTER_WATCHLIST)).equalsIgnoreCase("Yes") == g.isWishlist();
            boolean year = Objects.requireNonNull(filters.get(FilterBarManager.FILTER_YEAR)).isEmpty() || String.valueOf(g.getYear()).equals(filters.get(FilterBarManager.FILTER_YEAR));

            return status && mode && type && currency && backlog && watchlist && year;
        }).collect(Collectors.toList());

        // Sorting Logic
        String sortBy = filters.get(FilterBarManager.FILTER_SORTING);
        if (sortBy != null) {
            switch (sortBy) {
                case "Name":
                    filtered.sort((a, b) -> a.game.getName().compareToIgnoreCase(b.game.getName()));
                    break;
                case "Year":
                    filtered.sort(Comparator.comparingInt(a -> a.game.getYear()));
                    break;
                case "Amount":
                    filtered.sort(Comparator.comparingDouble(a -> a.game.getAmount()));
                    break;
                default:
                    gameAdapter.sortGames();
                    break;
            }
        } else {
            gameAdapter.sortGames();
        }

        gameAdapter.updateGames(filtered);
        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        gamesRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}