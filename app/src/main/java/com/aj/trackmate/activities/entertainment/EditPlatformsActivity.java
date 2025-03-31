package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.models.application.SubApplication;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.aj.trackmate.R;

import java.util.concurrent.Executors;

public class EditPlatformsActivity extends AppCompatActivity {

    private int platformId;
    private boolean isEditMode = false;
    private String originalPlatformName, originalPlatformDescription;
    private SubApplication currentPlatform;
    private EditText platformName, platformDescription;
    private Button saveButton, editButton, cancelButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_platforms);
        platformName = findViewById(R.id.editPlatformName);
        platformDescription = findViewById(R.id.editPlatformDescription);
        saveButton = findViewById(R.id.platformSaveButton);
        editButton = findViewById(R.id.editPlatformButton);
        cancelButton = findViewById(R.id.cancelPlatformButton);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        platformId = getIntent().getIntExtra("PLATFORM_ID", -1);
        originalPlatformName = getIntent().getStringExtra("PLATFORM_NAME");
        originalPlatformDescription = getIntent().getStringExtra("PLATFORM_DESCRIPTION");
        ApplicationDatabase applicationDatabase = ApplicationDatabase.getInstance(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Platform");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        if (platformId != -1) {
            applicationDatabase.subApplicationDao().getSubApplicationById(platformId).observe(this, subApplication -> {
                currentPlatform = subApplication;
                boolean isReadOnly = currentPlatform.isReadOnly();

                if (currentPlatform != null) {
                    platformName.setText(currentPlatform.getName());
                    platformDescription.setText(currentPlatform.getDescription());
                    setEditMode(false);

                    if (isReadOnly) {
                        editButton.setVisibility(View.GONE);
                    } else {
                        editButton.setVisibility(View.VISIBLE);
                    }
                }
            });

            // Handle Edit button click
            editButton.setOnClickListener(v -> {
                setEditMode(true);
                isEditMode = true;
            });

            // Handle Save button click
            saveButton.setOnClickListener(v -> {
                savePlatformDetails();
                finish(); // Go back to listing page
            });

            // Handle Cancel button click
            cancelButton.setOnClickListener(v -> {
                resetToOriginalState();
                setEditMode(false);
            });
        }
    }

    private void setEditMode(boolean enabled) {
        platformName.setEnabled(enabled);
        platformDescription.setEnabled(enabled);

        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void resetToOriginalState() {
        // Reset fields to original values (if stored)
        platformName.setText(originalPlatformName);
        platformDescription.setText(originalPlatformDescription);
    }

    private void savePlatformDetails() {
        if (currentPlatform != null) {
            currentPlatform.setName(platformName.getText().toString());
            currentPlatform.setDescription(platformDescription.getText().toString());

            Executors.newSingleThreadExecutor().execute(() -> {
                ApplicationDatabase.getInstance(this).subApplicationDao().update(currentPlatform);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Platform updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_PLATFORM_ID", currentPlatform.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the activity and go back to the listing page
                });
            });
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
}