package com.aj.trackmate.activities.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.game.GameDLCAdapter;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.game.*;
import com.aj.trackmate.models.game.relations.GameWithDownloadableContent;
import com.aj.trackmate.operations.SwipeToDeleteCallback;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_GAME_DLC_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_GAME_DLC_EDIT;

public class EditGameActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener {

    private GameWithDownloadableContent currentGame;
    private List<DownloadableContent> dlcs;
    private GameDLCAdapter gameDLCAdapter;
    private TextView dlcsEmptyStateMessage, editGameHeading;
    private RecyclerView gameDLCsRecyclerView;
    private EditText gameNameEditText;
    private Spinner gameStatusSpinner;
    private RadioButton purchasedYes, purchasedNo;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton wantToGoFor100PercentYes, wantToGoFor100PercentNo;
    private RadioButton backlogYes, backlogNo;
    private RadioButton purchaseTypePhysical, purchaseTypeDigital, purchaseTypeNotDecided;
    private RadioButton purchaseModePurchase, purchaseModeSubscription, purchaseModeNotYet;
    private RadioGroup purchasedGroup, wishlistGroup, purchaseModeGroup, purchaseTypeGroup, startedGroup, completedGroup, full100Group, backlogGroup;
    private Button editButton, cancelButton, saveButton, addDLCButton;
    private boolean isEditMode = false;
    private String originalGameName, originalGameStatus; // Store original values for cancel functionality
    private int gameId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Views
        gameNameEditText = findViewById(R.id.gameNameEditText);
        gameStatusSpinner = findViewById(R.id.gameStatusSpinner);

        purchasedYes = findViewById(R.id.purchasedYes);
        purchasedNo = findViewById(R.id.purchasedNo);

        wishlistYes = findViewById(R.id.wishlistYes);
        wishlistNo = findViewById(R.id.wishlistNo);

        purchaseTypePhysical = findViewById(R.id.purchaseTypePhysical);
        purchaseTypeDigital = findViewById(R.id.purchaseTypeDigital);
        purchaseTypeNotDecided = findViewById(R.id.purchaseTypeNotDecided);

        purchaseModePurchase = findViewById(R.id.purchaseModePurchase);
        purchaseModeSubscription = findViewById(R.id.purchaseModeSubscription);
        purchaseModeNotYet = findViewById(R.id.purchaseModeNotYet);

        startedYes = findViewById(R.id.startedYes);
        startedNo = findViewById(R.id.startedNo);

        completedYes = findViewById(R.id.completedYes);
        completedNo = findViewById(R.id.completedNo);

        wantToGoFor100PercentYes = findViewById(R.id.wantToGoFor100PercentYes);
        wantToGoFor100PercentNo = findViewById(R.id.wantToGoFor100PercentNo);

        backlogYes = findViewById(R.id.backlogYes);
        backlogNo = findViewById(R.id.backlogNo);

        addDLCButton = findViewById(R.id.addDLCButton);
        editGameHeading = findViewById(R.id.editGameHeading);
        dlcsEmptyStateMessage = findViewById(R.id.dlcsEmptyStateMessage);
        gameDLCsRecyclerView = findViewById(R.id.recyclerViewGameDLCs);
        gameDLCsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the Game Status Spinner (Dropdown)
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.game_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameStatusSpinner.setAdapter(statusAdapter);

        purchasedGroup = findViewById(R.id.purchasedRadioGroup);
        wishlistGroup = findViewById(R.id.wishlistRadioGroup);
        purchaseModeGroup = findViewById(R.id.purchaseModeRadioGroup);
        purchaseTypeGroup = findViewById(R.id.purchaseTypeGroup);
        startedGroup = findViewById(R.id.startedRadioGroup);
        completedGroup = findViewById(R.id.completedRadioGroup);
        full100Group = findViewById(R.id.full100RadioGroup);
        backlogGroup = findViewById(R.id.backlogRadioGroup);

        editButton = findViewById(R.id.editGameButton);
        cancelButton = findViewById(R.id.cancelGameButton);
        saveButton = findViewById(R.id.saveGameButton);

        gameId = getIntent().getIntExtra("GAME_ID", -1);
        originalGameName = getIntent().getStringExtra("GAME_NAME");
        originalGameStatus = getIntent().getStringExtra("GAME_STATUS");

        Log.d("Edit Game", "Game Id: " + gameId);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Game");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        addDLCButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new game dlc
            Intent intent = new Intent(this, AddDLCActivity.class);
            intent.putExtra("GAME_ID", gameId);
            startActivityForResult(intent, REQUEST_CODE_GAME_DLC_ADD); // Request code to identify the result
        });

        GameDatabase.getInstance(this).gameDao().getGameWithDLCsForGameId(gameId).observe(this, gameWithDownloadableContent -> {
            currentGame = gameWithDownloadableContent;
            if (currentGame != null) {
                Game game = currentGame.game;
                dlcs = currentGame.dlcs;

                Log.d("Edit Game", "DLC Size: " + dlcs.size());

                if (dlcs.isEmpty()) {
                    dlcsEmptyStateMessage.setVisibility(View.VISIBLE);
                    gameDLCsRecyclerView.setVisibility(View.GONE);
                } else {
                    dlcsEmptyStateMessage.setVisibility(View.GONE);
                    gameDLCsRecyclerView.setVisibility(View.VISIBLE);
                }

                gameDLCAdapter = new GameDLCAdapter(this, dlcs, dlc -> {
                    Intent intent = new Intent(EditGameActivity.this, EditDLCActivity.class);
                    intent.putExtra("GAME_ID", gameId);
                    intent.putExtra("DLC_ID", dlc.getId());
                    intent.putExtra("DLC_NAME", dlc.getName());
                    startActivityForResult(intent, REQUEST_CODE_GAME_DLC_EDIT);
                });

                gameDLCsRecyclerView.setAdapter(gameDLCAdapter);
                gameDLCAdapter.updateGames(dlcs);  // Notify adapter of new data

                // Setup the swipe-to-delete functionality
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(gameDLCAdapter, this, this));
                itemTouchHelper.attachToRecyclerView(gameDLCsRecyclerView);

                gameNameEditText.setText(originalGameName);

                ArrayAdapter gameStatusSpinnerAdapter = (ArrayAdapter) gameStatusSpinner.getAdapter();
                int position = gameStatusSpinnerAdapter.getPosition(game.getStatus().getStatus());
                gameStatusSpinner.setSelection(position);

                GamePurchaseMode purchaseMode = game.getPurchaseMode();
                if (purchaseMode.equals(GamePurchaseMode.PURCHASE)) {
                    purchaseModePurchase.setChecked(true);
                } else if (purchaseMode.equals(GamePurchaseMode.SUBSCRIPTION)) {
                    purchaseModeSubscription.setChecked(true);
                } else {
                    purchaseModeNotYet.setChecked(true);
                }

                GamePurchaseType purchaseType = game.getPurchaseType();
                if (purchaseType.equals(GamePurchaseType.PHYSICAL)) {
                    purchaseTypePhysical.setChecked(true);
                } else if (purchaseType.equals(GamePurchaseType.DIGITAL)) {
                    purchaseTypeDigital.setChecked(true);
                } else {
                    purchaseTypeNotDecided.setChecked(true);
                }

                if (game.isPurchased()) {
                    purchasedYes.setChecked(true);
                } else {
                    purchasedNo.setChecked(true);
                }

                if (game.isWishlist()) {
                    wishlistYes.setChecked(true);
                } else {
                    wishlistNo.setChecked(true);
                }

                if (game.isStarted()) {
                    startedYes.setChecked(true);
                } else {
                    startedNo.setChecked(true);
                }

                if (game.isCompleted()) {
                    completedYes.setChecked(true);
                } else {
                    completedNo.setChecked(true);
                }

                if (game.isWantToGoFor100Percent()) {
                    wantToGoFor100PercentYes.setChecked(true);
                } else {
                    wantToGoFor100PercentNo.setChecked(true);
                }

                if (game.isBacklog()) {
                    backlogYes.setChecked(true);
                } else {
                    backlogNo.setChecked(true);
                }
            }
        });

        // Set initial state (readonly)
        setEditMode(false);

        // Handle Edit button click
        editButton.setOnClickListener(v -> {
            isEditMode = true;
            editGameHeading.setText("Edit Game");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Game");  // Change the title dynamically
            setEditMode(true);
        });

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            saveGameDetails();
            finish(); // Go back to listing page
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            resetToOriginalState();
            isEditMode = false;
            editGameHeading.setText("View Game");
            Objects.requireNonNull(getSupportActionBar()).setTitle("View Game");  // Change the title dynamically
            setEditMode(false);
        });
    }

    private void setEditMode(boolean enabled) {
        gameNameEditText.setEnabled(enabled);
        gameStatusSpinner.setEnabled(enabled);

        purchasedGroup.setEnabled(enabled);
        purchasedYes.setEnabled(enabled);
        purchasedNo.setEnabled(enabled);

        wishlistGroup.setEnabled(enabled);
        wishlistYes.setEnabled(enabled);
        wishlistNo.setEnabled(enabled);

        purchaseModeGroup.setEnabled(enabled);
        purchaseModePurchase.setEnabled(enabled);
        purchaseModeSubscription.setEnabled(enabled);
        purchaseModeNotYet.setEnabled(enabled);

        purchaseTypeGroup.setEnabled(enabled);
        purchaseTypePhysical.setEnabled(enabled);
        purchaseTypeDigital.setEnabled(enabled);
        purchaseTypeNotDecided.setEnabled(enabled);

        startedGroup.setEnabled(enabled);
        startedYes.setEnabled(enabled);
        startedNo.setEnabled(enabled);

        completedGroup.setEnabled(enabled);
        completedYes.setEnabled(enabled);
        completedNo.setEnabled(enabled);

        full100Group.setEnabled(enabled);
        wantToGoFor100PercentYes.setEnabled(enabled);
        wantToGoFor100PercentNo.setEnabled(enabled);

        backlogGroup.setEnabled(enabled);
        backlogYes.setEnabled(enabled);
        backlogNo.setEnabled(enabled);

        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void saveGameDetails() {
        // Save game details to database or shared preferences
        // You can add logic to update Room database if using one
        if (currentGame != null) {
            // Get values from the input fields
            String gameName = gameNameEditText.getText().toString();
            String gameStatus = gameStatusSpinner.getSelectedItem().toString();

            boolean isPurchased = purchasedYes.isChecked();
            boolean isWishlist = wishlistYes.isChecked();
            boolean isStarted = startedYes.isChecked();
            boolean isCompleted = completedYes.isChecked();
            boolean want100Percent = wantToGoFor100PercentYes.isChecked();
            boolean isBacklog = backlogYes.isChecked();

            GamePurchaseMode gamePurchaseMode = GamePurchaseMode.NOT_YET;
            if (purchaseModePurchase.isChecked()) {
                gamePurchaseMode = GamePurchaseMode.PURCHASE;
            } else if (purchaseModeSubscription.isChecked()) {
                gamePurchaseMode = GamePurchaseMode.SUBSCRIPTION;
            }

            GamePurchaseType gamePurchaseType = GamePurchaseType.NOT_DECIDED;
            if (purchaseTypePhysical.isChecked()) {
                gamePurchaseType = GamePurchaseType.PHYSICAL;
            } else if (purchaseTypeDigital.isChecked()) {
                gamePurchaseType = GamePurchaseType.DIGITAL;
            }

            currentGame.game.setName(gameName);
            currentGame.game.setPurchased(isPurchased);
            currentGame.game.setWishlist(isWishlist);
            currentGame.game.setPurchaseMode(gamePurchaseMode);
            currentGame.game.setPurchaseType(gamePurchaseType);
            currentGame.game.setStarted(isStarted);
            currentGame.game.setCompleted(isCompleted);
            currentGame.game.setStatus(GameStatus.fromStatus(gameStatus));  // Convert string to enum
            currentGame.game.setWantToGoFor100Percent(want100Percent);
            currentGame.game.setBacklog(isBacklog);

            Executors.newSingleThreadExecutor().execute(() -> {
                GameDatabase.getInstance(this).gameDao().update(currentGame.game);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Game updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_GAME_ID", gameId);
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the activity and go back to the listing page
                });
            });
        }
    }

    private void resetToOriginalState() {
        // Reset fields to original values (if stored)
        gameNameEditText.setText(originalGameName);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Retrieve the new game from the result
            DownloadableContent newGame = null;
            if (requestCode == REQUEST_CODE_GAME_DLC_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newGame = data.getParcelableExtra("NEW_GAME", DownloadableContent.class);
                Log.d("Game DLC Action", "Save:" + newGame);

                if (gameDLCAdapter == null) {
                    gameDLCAdapter = new GameDLCAdapter(this, dlcs, null);
                    gameDLCsRecyclerView.setAdapter(gameDLCAdapter);
                }

                // Add the new game to the list
                if (newGame != null) {
                    dlcs.add(newGame);
                    gameDLCAdapter.updateGames(dlcs);
                }

                Log.d("Game DLC Action", "List count:" + dlcs.size());

                // Update empty state visibility
                if (dlcs.isEmpty()) {
                    dlcsEmptyStateMessage.setVisibility(View.VISIBLE);
                    gameDLCsRecyclerView.setVisibility(View.GONE);
                } else {
                    dlcsEmptyStateMessage.setVisibility(View.GONE);
                    gameDLCsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_GAME_DLC_EDIT) {
                int updatedGameId = data.getIntExtra("UPDATED_GAME_ID", -1);
                if (updatedGameId != -1) {
                    gameDLCAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }
        }
    }

    @Override
    public void removeItem(int position) {
        DownloadableContent dlc = dlcs.get(position);
        gameDLCAdapter.removeGame(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            GameDatabase.getInstance(this).gameDao().deleteDLC(dlc); // First delete DLCs

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