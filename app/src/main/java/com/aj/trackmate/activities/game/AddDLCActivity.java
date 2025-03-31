package com.aj.trackmate.activities.game;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.aj.trackmate.R;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.game.*;

import java.util.concurrent.Executors;

public class AddDLCActivity extends AppCompatActivity {

    private EditText gameNameEditText;
    private Spinner gameStatusSpinner;
    private RadioButton purchasedYes, purchasedNo;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton dlcTypeStory, dlcTypeMultiPlayer, dlcTypeCosmetics, dlcTypeOther;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private RadioButton purchaseTypePhysical, purchaseTypeDigital, purchaseTypeNotDecided;
    private RadioButton purchaseModePurchase, purchaseModeSubscription, purchaseModeNotYet;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dlc);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add DLC");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Get the platform name from the Intent
        int gameId = getIntent().getIntExtra("GAME_ID", -1);
        Log.d("Add DLC Game", "Game Id: " + gameId);

        GameDatabase.getInstance(this).gameDao().getGameById(gameId).observe(this, game -> {
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

            saveButton = findViewById(R.id.saveDLCButton);

            dlcTypeOther.setChecked(true);
            purchasedNo.setChecked(true);
            wishlistNo.setChecked(true);
            purchaseTypeNotDecided.setChecked(true);
            purchaseModeNotYet.setChecked(true);
            startedNo.setChecked(true);
            completedNo.setChecked(true);
            backlogNo.setChecked(true);

            // Set up the Game Status Spinner (Dropdown)
            ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                    R.array.game_dlc_statuses, android.R.layout.simple_spinner_item);
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gameStatusSpinner.setAdapter(statusAdapter);

            saveButton.setOnClickListener(v -> {
                DownloadableContent dlc = new DownloadableContent();
                dlc.setGameId(game.getId());

                // Get values from the input fields
                String gameName = gameNameEditText.getText().toString();
                String gameStatus = gameStatusSpinner.getSelectedItem().toString();

                if (gameName.isEmpty()) {
                    gameNameEditText.setError("Game Name is required");
                    gameNameEditText.requestFocus();
                    return;
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

                Log.d("Add DLC Game", "Save: " + gameName);

                dlc.setName(gameName);
                dlc.setDlcType(dlcType);  // Convert string to enum
                dlc.setPurchased(isPurchased);
                dlc.setWishlist(isWishlist);
                dlc.setPurchaseMode(gamePurchaseMode);
                dlc.setPurchaseType(gamePurchaseType);
                dlc.setStarted(isStarted);
                dlc.setCompleted(isCompleted);
                dlc.setStatus(DLCStatus.fromStatus(gameStatus));  // Convert string to enum
                dlc.setBacklog(isBacklog);

                Executors.newSingleThreadExecutor().execute(() -> {
                    GameDatabase.getInstance(this).gameDao().insertDLC(dlc);
                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("NEW_GAME", dlc);  // Add the game as Parcelable
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