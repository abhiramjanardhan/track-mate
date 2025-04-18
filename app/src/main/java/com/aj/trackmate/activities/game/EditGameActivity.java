package com.aj.trackmate.activities.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.game.GameDLCAdapter;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.game.*;
import com.aj.trackmate.models.game.relations.GameWithDownloadableContent;
import com.aj.trackmate.operations.LongPressCallBack;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.aj.trackmate.operations.templates.ItemUpdateListener;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_GAME_DLC_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_GAME_DLC_EDIT;

public class EditGameActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener, ItemUpdateListener {

    private GameWithDownloadableContent currentGame;
    private List<DownloadableContent> dlcs;
    private GameDLCAdapter gameDLCAdapter;
    private TextView dlcsEmptyStateMessage, editGameHeading;
    private RecyclerView gameDLCsRecyclerView;
    private EditText gameNameEditText, gameAmount, gameYear;
    private Spinner gameStatusSpinner, currencySpinner;
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

    private EditText searchEditText;
    private List<DownloadableContent> allDLCs = new ArrayList<>();

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

        gameAmount = findViewById(R.id.gameAmount);
        currencySpinner = findViewById(R.id.currencySpinner);
        gameYear = findViewById(R.id.gameYear);

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

        // Get currency values from enum
        List<String> currencyList = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            currencyList.add(currency.getCurrency());
        }

        // Set to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

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
                allDLCs = currentGame.dlcs;
                dlcs = new ArrayList<>(allDLCs);

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
                }, ((view, position) -> {
                    LongPressCallBack longPressCallBack = new LongPressCallBack(gameDLCAdapter, this, this, this);
                    longPressCallBack.handleLongPress(view, position, "Game DLC");
                }));

                gameDLCsRecyclerView.setAdapter(gameDLCAdapter);
                gameDLCAdapter.updateGames(dlcs);  // Notify adapter of new data

                gameNameEditText.setText(originalGameName);
                gameAmount.setText(String.valueOf(game.getAmount()));
                gameYear.setText(String.valueOf(game.getYear()));

                ArrayAdapter gameStatusSpinnerAdapter = (ArrayAdapter) gameStatusSpinner.getAdapter();
                int position = gameStatusSpinnerAdapter.getPosition(game.getStatus().getStatus());
                gameStatusSpinner.setSelection(position);

                ArrayAdapter currencySpinnerAdapter = (ArrayAdapter) currencySpinner.getAdapter();
                int currencyPosition = currencySpinnerAdapter.getPosition(game.getCurrency().getCurrency());
                currencySpinner.setSelection(currencyPosition);

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

        gameAmount.setEnabled(enabled);
        currencySpinner.setEnabled(enabled);
        gameYear.setEnabled(enabled);

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
            String selectedCurrency = currencySpinner.getSelectedItem().toString();
            Currency currency = Currency.fromCurrency(selectedCurrency);
            String yearText = gameYear.getText().toString();

            double amount = 0.0;
            try {
                amount = Double.parseDouble(gameAmount.getText().toString());
            } catch (NumberFormatException e) {
                // handle invalid input
                if (!gameAmount.getText().toString().isEmpty()) {
                    // handle invalid input
                    gameAmount.setError("Amount is invalid");
                    gameAmount.requestFocus();
                    return;
                }
            }

            int year = 0;
            if (yearText.length() == 4) {
                try {
                    year = Integer.parseInt(yearText);
                    // Additional validation if needed (e.g., year range)
                } catch (NumberFormatException e) {
                    // handle invalid input
                    if (!gameYear.getText().toString().isEmpty()) {
                        gameYear.setError("Year is invalid");
                        gameYear.requestFocus();
                        return;
                    }
                }
            } else {
                // handle invalid input
                if (!gameYear.getText().toString().isEmpty()) {
                    gameYear.setError("Year is invalid");
                    gameYear.requestFocus();
                    return;
                }
            }

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
            currentGame.game.setAmount(amount);
            currentGame.game.setCurrency(currency);
            currentGame.game.setYear(year);

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
            if (requestCode == REQUEST_CODE_GAME_DLC_ADD && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                newGame = data.getParcelableExtra("NEW_GAME", DownloadableContent.class);
                Log.d("Game DLC Action", "Save:" + newGame);

                if (gameDLCAdapter == null) {
                    gameDLCAdapter = new GameDLCAdapter(this, dlcs, null, null);
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

    @Override
    public String getSavedItem(int position) {
        DownloadableContent dlc = dlcs.get(position);
        return dlc.getStatus().getStatus();
    }

    @Override
    public List<String> getItems() {
        return Arrays.stream(DLCStatus.values()).map(DLCStatus::getStatus).collect(Collectors.toList());
    }

    @Override
    public void updateItem(int position, String value) {
        DownloadableContent dlc = dlcs.get(position);

        // Perform database update in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            dlc.setStatus(DLCStatus.fromStatus(value));
            GameDatabase.getInstance(this).gameDao().updateDLC(dlc);

            // Show a Toast on the main thread after the update is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void filterGames(String query) {
        String lower = query.toLowerCase();

        List<DownloadableContent> filtered = allDLCs.stream().filter(dlc -> {
            boolean matchesName = dlc.getName().toLowerCase().contains(lower);
            boolean matchesStatus = dlc.getStatus().getStatus().toLowerCase().contains(lower);
            boolean matchesDLCType = dlc.getDlcType().getDLCType().toLowerCase().contains(lower);
            boolean matchesPurchaseMode = dlc.getPurchaseMode().name().toLowerCase().contains(lower);
            boolean matchesPurchaseType = dlc.getPurchaseType().name().toLowerCase().contains(lower);
            boolean matchesCurrency = dlc.getCurrency().getCurrency().toLowerCase().contains(lower);

            return  matchesName || matchesStatus || matchesDLCType || matchesPurchaseMode || matchesPurchaseType || matchesCurrency;
        }).collect(Collectors.toList());

        gameDLCAdapter.updateGames(filtered);

        dlcsEmptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        gameDLCsRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}