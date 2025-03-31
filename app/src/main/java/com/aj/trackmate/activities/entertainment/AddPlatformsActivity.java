package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.models.application.SubApplication;
import androidx.appcompat.app.AppCompatActivity;

import com.aj.trackmate.R;

import java.util.concurrent.Executors;

public class AddPlatformsActivity extends AppCompatActivity {

    private String categoryName;
    private TextView addPlatformHeading;
    private EditText platformName, platformDescription;
    private Button saveButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_platform);
        addPlatformHeading = findViewById(R.id.addSubApplicationHeading);
        platformName = findViewById(R.id.addPlatformName);
        platformDescription = findViewById(R.id.addPlatformDescription);
        saveButton = findViewById(R.id.platformSaveButton);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        ApplicationDatabase applicationDatabase = ApplicationDatabase.getInstance(this);
        addPlatformHeading.setText("Platform Details");
        platformName.setHint("Platform Name");
        platformDescription.setHint("Platform Description");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Platform");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        applicationDatabase.applicationDao().getApplicationsByName(categoryName).observe(this, application -> {
            saveButton.setOnClickListener(v -> {
                String name = platformName.getText().toString().trim();
                String description = platformDescription.getText().toString().trim();

                if (!name.trim().toLowerCase().contains(categoryName)) {
                    name = name + " " + categoryName;
                }

                if (name.isEmpty()) {
                    platformName.setError("Name is required");
                    platformName.requestFocus();
                    return;
                }

                if (description.isEmpty()) {
                    platformDescription.setError("Description is required");
                    platformDescription.requestFocus();
                    return;
                }

                SubApplication subApplication = new SubApplication();
                subApplication.setApplicationId(application.getId());
                subApplication.setName(name);
                subApplication.setDescription(description);
                subApplication.setReadOnly(false);

                Executors.newSingleThreadExecutor().execute(() -> {
                    applicationDatabase.subApplicationDao().insert(subApplication);
                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("NEW_PLATFORM", subApplication);  // Add the sub application as Parcelable
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