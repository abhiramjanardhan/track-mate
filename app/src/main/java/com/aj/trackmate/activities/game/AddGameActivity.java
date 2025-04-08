package com.aj.trackmate.activities.game;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.R;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.game.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class AddGameActivity extends AppCompatActivity {

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
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        // Get the platform name from the Intent
        String platform = getIntent().getStringExtra("CATEGORY");
        Log.d("Add Game", "Platform: " + platform);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(platform);  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

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

        saveButton = findViewById(R.id.saveButton);

        purchasedNo.setChecked(true);
        wishlistNo.setChecked(true);
        purchaseTypeNotDecided.setChecked(true);
        purchaseModeNotYet.setChecked(true);
        startedNo.setChecked(true);
        completedNo.setChecked(true);
        wantToGoFor100PercentNo.setChecked(true);
        backlogNo.setChecked(true);

        // Get current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        gameYear.setText(String.valueOf(currentYear));

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

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Get values from the input fields
            String gameName = gameNameEditText.getText().toString();
            String gameStatus = gameStatusSpinner.getSelectedItem().toString();
            String selectedCurrency = currencySpinner.getSelectedItem().toString();
            Currency currency = Currency.fromCurrency(selectedCurrency);
            String yearText = gameYear.getText().toString();

            if (gameName.isEmpty()) {
                gameNameEditText.setError("Game Name is required");
                gameNameEditText.requestFocus();
                return;
            }

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

            Log.d("Add Game", "Save: " + platform);

            // Create a new game object
            Game newGame = new Game();
            newGame.setName(gameName);
            newGame.setPlatform(Platform.fromName(platform));  // Convert string to enum
            newGame.setPurchased(isPurchased);
            newGame.setWishlist(isWishlist);
            newGame.setPurchaseMode(gamePurchaseMode);
            newGame.setPurchaseType(gamePurchaseType);
            newGame.setStarted(isStarted);
            newGame.setCompleted(isCompleted);
            newGame.setStatus(GameStatus.fromStatus(gameStatus));  // Convert string to enum
            newGame.setWantToGoFor100Percent(want100Percent);
            newGame.setBacklog(isBacklog);
            newGame.setAmount(amount);
            newGame.setCurrency(currency);
            newGame.setYear(year);

            Executors.newSingleThreadExecutor().execute(() -> {
                GameDatabase.getInstance(this).gameDao().insert(newGame);
                runOnUiThread(() -> {
                    GameDatabase.getInstance(this).gameDao().getGameWithDLCsForGameId(newGame.getId()).observe(this, gameWithDLCs -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("NEW_GAME", gameWithDLCs);  // Add the game as Parcelable
                        setResult(RESULT_OK, resultIntent);
                        finish();  // Close the activity
                    });
                });
            });
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