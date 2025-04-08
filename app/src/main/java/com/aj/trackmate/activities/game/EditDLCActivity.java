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
import com.aj.trackmate.R;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class EditDLCActivity extends AppCompatActivity {

    private DownloadableContent currentDLC;
    private int gameId, dlcId;
    private String dlcName;
    private boolean isEditMode = false;
    private TextView editGameDLCHeading;
    private EditText gameNameEditText, gameAmount, gameYear;
    private Spinner gameStatusSpinner, currencySpinner;
    private RadioButton purchasedYes, purchasedNo;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton dlcTypeStory, dlcTypeMultiPlayer, dlcTypeCosmetics, dlcTypeOther;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private RadioButton purchaseTypePhysical, purchaseTypeDigital, purchaseTypeNotDecided;
    private RadioButton purchaseModePurchase, purchaseModeSubscription, purchaseModeNotYet;
    private RadioGroup purchasedDLCGroup, wishlistDLCGroup, dlcTypeGroup, purchaseTypeDLCGroup, purchaseModeDLCGroup, startedDLCGroup, completedDLCGroup, backlogDLCGroup;
    private Button saveButton, editButton, cancelButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dlc);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Game DLC");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        gameId = getIntent().getIntExtra("GAME_ID", -1);
        dlcId = getIntent().getIntExtra("DLC_ID", -1);
        dlcName = getIntent().getStringExtra("DLC_NAME");

        // Initialize Views
        gameNameEditText = findViewById(R.id.gameDLCNameEditText);
        gameStatusSpinner = findViewById(R.id.gameDLCStatusSpinner);

        purchasedYes = findViewById(R.id.purchasedDLCYes);
        purchasedNo = findViewById(R.id.purchasedDLCNo);

        wishlistYes = findViewById(R.id.wishlistDLCYes);
        wishlistNo = findViewById(R.id.wishlistDLCNo);

        purchaseTypePhysical = findViewById(R.id.purchaseTypeDLCPhysical);
        purchaseTypeDigital = findViewById(R.id.purchaseTypeDLCDigital);
        purchaseTypeNotDecided = findViewById(R.id.purchaseTypeDLCNotDecided);

        purchaseModePurchase = findViewById(R.id.purchaseModeDLCPurchase);
        purchaseModeSubscription = findViewById(R.id.purchaseModeDLCSubscription);
        purchaseModeNotYet = findViewById(R.id.purchaseModeDLCNotYet);

        dlcTypeStory = findViewById(R.id.dlcTypeStory);
        dlcTypeMultiPlayer = findViewById(R.id.dlcTypeMultiPlayer);
        dlcTypeCosmetics = findViewById(R.id.dlcTypeCosmetics);
        dlcTypeOther = findViewById(R.id.dlcTypeOther);

        startedYes = findViewById(R.id.startedDLCYes);
        startedNo = findViewById(R.id.startedDLCNo);

        completedYes = findViewById(R.id.completedDLCYes);
        completedNo = findViewById(R.id.completedDLCNo);

        backlogYes = findViewById(R.id.backlogDLCYes);
        backlogNo = findViewById(R.id.backlogDLCNo);

        gameAmount = findViewById(R.id.gameAmount);
        currencySpinner = findViewById(R.id.currencySpinner);
        gameYear = findViewById(R.id.gameYear);

        purchasedDLCGroup = findViewById(R.id.purchasedDLCGroup);
        wishlistDLCGroup = findViewById(R.id.wishlistDLCGroup);
        dlcTypeGroup = findViewById(R.id.dlcTypeGroup);
        purchaseTypeDLCGroup = findViewById(R.id.purchaseTypeDLCGroup);
        purchaseModeDLCGroup = findViewById(R.id.purchaseModeDLCGroup);
        startedDLCGroup = findViewById(R.id.startedDLCGroup);
        completedDLCGroup = findViewById(R.id.completedDLCGroup);
        backlogDLCGroup = findViewById(R.id.backlogDLCGroup);

        editGameDLCHeading = findViewById(R.id.editGameDLCHeading);
        editButton = findViewById(R.id.editDLCGameButton);
        cancelButton = findViewById(R.id.cancelDLCGameButton);
        saveButton = findViewById(R.id.saveDLCGameButton);

        // Set up the Game Status Spinner (Dropdown)
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.game_dlc_statuses, android.R.layout.simple_spinner_item);
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

        Log.d("Edit Game DLC", "Game Id: " + gameId);
        Log.d("Edit Game DLC", "DLC Id: " + dlcId);

        GameDatabase.getInstance(this).gameDao().getDLCById(dlcId, gameId).observe(this, downloadableContent -> {
            currentDLC = downloadableContent;
            Log.d("Edit Game DLC", "DLC Type: " + currentDLC.getStatus().getStatus());

            if (currentDLC != null) {
                gameNameEditText.setText(dlcName);
                gameAmount.setText(String.valueOf(currentDLC.getAmount()));
                gameYear.setText(String.valueOf(currentDLC.getYear()));

                ArrayAdapter gameStatusSpinnerAdapter = (ArrayAdapter) gameStatusSpinner.getAdapter();
                int position = gameStatusSpinnerAdapter.getPosition(currentDLC.getStatus().getStatus());
                gameStatusSpinner.setSelection(position);

                ArrayAdapter currencySpinnerAdapter = (ArrayAdapter) currencySpinner.getAdapter();
                int currencyPosition = currencySpinnerAdapter.getPosition(currentDLC.getCurrency().getCurrency());
                currencySpinner.setSelection(currencyPosition);

                DownloadableContentType dlcType = currentDLC.getDlcType();
                if (dlcType.equals(DownloadableContentType.STORY)) {
                    dlcTypeStory.setChecked(true);
                } else if (dlcType.equals(DownloadableContentType.MULTI_PLAYER)) {
                    dlcTypeMultiPlayer.setChecked(true);
                } else if (dlcType.equals(DownloadableContentType.COSMETICS)) {
                    dlcTypeCosmetics.setChecked(true);
                } else {
                    dlcTypeOther.setChecked(true);
                }

                GamePurchaseMode purchaseMode = currentDLC.getPurchaseMode();
                if (purchaseMode.equals(GamePurchaseMode.PURCHASE)) {
                    purchaseModePurchase.setChecked(true);
                } else if (purchaseMode.equals(GamePurchaseMode.SUBSCRIPTION)) {
                    purchaseModeSubscription.setChecked(true);
                } else {
                    purchaseModeNotYet.setChecked(true);
                }

                GamePurchaseType purchaseType = currentDLC.getPurchaseType();
                if (purchaseType.equals(GamePurchaseType.PHYSICAL)) {
                    purchaseTypePhysical.setChecked(true);
                } else if (purchaseType.equals(GamePurchaseType.DIGITAL)) {
                    purchaseTypeDigital.setChecked(true);
                } else {
                    purchaseTypeNotDecided.setChecked(true);
                }

                if (currentDLC.isPurchased()) {
                    purchasedYes.setChecked(true);
                } else {
                    purchasedNo.setChecked(true);
                }

                if (currentDLC.isWishlist()) {
                    wishlistYes.setChecked(true);
                } else {
                    wishlistNo.setChecked(true);
                }

                if (currentDLC.isStarted()) {
                    startedYes.setChecked(true);
                } else {
                    startedNo.setChecked(true);
                }

                if (currentDLC.isCompleted()) {
                    completedYes.setChecked(true);
                } else {
                    completedNo.setChecked(true);
                }

                if (currentDLC.isBacklog()) {
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
            editGameDLCHeading.setText("Edit Game DLC");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Game DLC");  // Change the title dynamically
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
            editGameDLCHeading.setText("View Game DLC");
            Objects.requireNonNull(getSupportActionBar()).setTitle("View Game DLC");  // Change the title dynamically
            setEditMode(false);
        });
    }

    private void setEditMode(boolean enabled) {
        gameNameEditText.setEnabled(enabled);
        gameStatusSpinner.setEnabled(enabled);

        purchasedDLCGroup.setEnabled(enabled);
        purchasedYes.setEnabled(enabled);
        purchasedNo.setEnabled(enabled);

        wishlistDLCGroup.setEnabled(enabled);
        wishlistYes.setEnabled(enabled);
        wishlistNo.setEnabled(enabled);

        dlcTypeGroup.setEnabled(enabled);
        dlcTypeStory.setEnabled(enabled);
        dlcTypeMultiPlayer.setEnabled(enabled);
        dlcTypeCosmetics.setEnabled(enabled);
        dlcTypeOther.setEnabled(enabled);

        purchaseModeDLCGroup.setEnabled(enabled);
        purchaseModePurchase.setEnabled(enabled);
        purchaseModeSubscription.setEnabled(enabled);
        purchaseModeNotYet.setEnabled(enabled);

        purchaseTypeDLCGroup.setEnabled(enabled);
        purchaseTypePhysical.setEnabled(enabled);
        purchaseTypeDigital.setEnabled(enabled);
        purchaseTypeNotDecided.setEnabled(enabled);

        startedDLCGroup.setEnabled(enabled);
        startedYes.setEnabled(enabled);
        startedNo.setEnabled(enabled);

        completedDLCGroup.setEnabled(enabled);
        completedYes.setEnabled(enabled);
        completedNo.setEnabled(enabled);

        backlogDLCGroup.setEnabled(enabled);
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
        if (currentDLC != null) {
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

            DownloadableContentType dlcType = DownloadableContentType.OTHER;
            if (dlcTypeStory.isChecked()) {
                dlcType = DownloadableContentType.STORY;
            } else if (dlcTypeMultiPlayer.isChecked()) {
                dlcType = DownloadableContentType.MULTI_PLAYER;
            } else if (dlcTypeCosmetics.isChecked()) {
                dlcType = DownloadableContentType.COSMETICS;
            }

            currentDLC.setName(gameName);
            currentDLC.setPurchased(isPurchased);
            currentDLC.setWishlist(isWishlist);
            currentDLC.setDlcType(dlcType);
            currentDLC.setPurchaseMode(gamePurchaseMode);
            currentDLC.setPurchaseType(gamePurchaseType);
            currentDLC.setStarted(isStarted);
            currentDLC.setCompleted(isCompleted);
            currentDLC.setStatus(DLCStatus.fromStatus(gameStatus));  // Convert string to enum
            currentDLC.setBacklog(isBacklog);
            currentDLC.setAmount(amount);
            currentDLC.setCurrency(currency);
            currentDLC.setYear(year);

            Executors.newSingleThreadExecutor().execute(() -> {
                GameDatabase.getInstance(this).gameDao().updateDLC(currentDLC);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Game DLC updated successfully!", Toast.LENGTH_SHORT).show();
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
        gameNameEditText.setText(dlcName);
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