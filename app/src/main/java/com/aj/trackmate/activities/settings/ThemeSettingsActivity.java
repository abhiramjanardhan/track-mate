package com.aj.trackmate.activities.settings;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.aj.trackmate.R;
import com.aj.trackmate.utils.ThemeUtils;

public class ThemeSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Theme Settings");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        handleTheme();
    }

    private void handleTheme() {
        RadioGroup radioGroup = findViewById(R.id.radio_group_theme);
        RadioButton radioLight = findViewById(R.id.radio_light);
        RadioButton radioDark = findViewById(R.id.radio_dark);
        RadioButton radioSystem = findViewById(R.id.radio_system);

        int selectedTheme = getSharedPreferences("theme_prefs", MODE_PRIVATE)
                .getInt("selected_theme", AppCompatDelegate.MODE_NIGHT_NO); // Default Light Mode

        if (selectedTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            radioLight.setChecked(true);
        } else if (selectedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            radioDark.setChecked(true);
        } else {
            radioSystem.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int mode;
            if (checkedId == R.id.radio_light) {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radio_dark) {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            updateTheme(mode);
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

    private void updateTheme(int mode) {
        ThemeUtils.setTheme(this, mode);
        recreate();  // Restart activity to apply theme
    }
}