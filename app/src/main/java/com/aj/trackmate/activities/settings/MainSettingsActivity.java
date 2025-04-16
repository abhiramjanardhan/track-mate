package com.aj.trackmate.activities.settings;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.aj.trackmate.R;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_APPLICATION_SETTINGS_THEME;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_APPLICATION_SETTINGS_APPLICATION;

public class MainSettingsActivity extends AppCompatActivity {

    private CardView themeSettingsCard, visibilitySettingsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        themeSettingsCard = findViewById(R.id.themeSettingsCard);
        visibilitySettingsCard = findViewById(R.id.visibilitySettingsCard);

        themeSettingsCard.setOnClickListener(v ->
                startActivityForResult(new Intent(this, ThemeSettingsActivity.class), REQUEST_CODE_APPLICATION_SETTINGS_THEME));

        visibilitySettingsCard.setOnClickListener(v ->
                startActivityForResult(new Intent(this, ApplicationSettingsActivity.class), REQUEST_CODE_APPLICATION_SETTINGS_APPLICATION));
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