package com.aj.trackmate.activities.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.game.GameAdapter;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.game.DownloadableContent;
import com.aj.trackmate.models.game.Game;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.relations.GameWithDownloadableContent;
import com.aj.trackmate.operations.SwipeToDeleteCallback;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_GAME_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_GAME_EDIT;

public class GamePlatformActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener {

    private ListView listView;
    private RecyclerView gamesRecyclerView;
    private GameAdapter gameAdapter;
    private List<GameWithDownloadableContent> games;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

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

        title.setText("Games List");
        emptyStateMessage.setText("No Games Available");

        // Get the platform name from the Intent
        String platform = getIntent().getStringExtra("CATEGORY");

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
                games = gameList;
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
                        intent.putExtra("GAME_STATUS", gameWithDownloadableContent.game.getStatus());
                        startActivityForResult(intent, REQUEST_CODE_GAME_EDIT);
                    });
                    gamesRecyclerView.setAdapter(gameAdapter);
                    gameAdapter.updateGames(games);  // Notify adapter of new data
                }

                // Setup the swipe-to-delete functionality
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(gameAdapter, this, this));
                itemTouchHelper.attachToRecyclerView(gamesRecyclerView);
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
                    gameAdapter = new GameAdapter(this, games, null);
                    gamesRecyclerView.setAdapter(gameAdapter);
                }

                // Add the new game to the list
                if (newGame != null) {
                    games.add(newGame);
                    gameAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the RecyclerView
                }

                Log.d("Game Action", "List count:" + games.size());

                // Update empty state visibility
                if (games.isEmpty()) {
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

    // Handle back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the back button is pressed
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
}