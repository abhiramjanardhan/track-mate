package com.aj.trackmate.activities.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.settings.RelatedSubApplicationSettingsAdapter;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.notifiers.SettingsUpdateNotifier;

import java.util.concurrent.Executors;

public class RelatedSubApplicationSettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVisibilitySubApplication;
    private TextView subApplicationVisibilityEmptyStateMessage, subApplicationVisibilityHeading;
    private RelatedSubApplicationSettingsAdapter applicationSettingsAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_sub_application_settings);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ids from the Intent
        int applicationId = getIntent().getIntExtra("APPLICATION_ID", -1);
        String applicationName = getIntent().getStringExtra("APPLICATION_NAME");
        ApplicationDatabase database = ApplicationDatabase.getInstance(this);
        Log.d("Sub Application Settings", "Applications Name: " + applicationName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(applicationName + " Settings");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        subApplicationVisibilityHeading = findViewById(R.id.subApplicationVisibilityHeading);
        subApplicationVisibilityEmptyStateMessage = findViewById(R.id.subApplicationVisibilityEmptyStateMessage);
        recyclerViewVisibilitySubApplication = findViewById(R.id.recyclerViewVisibilitySubApplication);
        recyclerViewVisibilitySubApplication.setLayoutManager(new LinearLayoutManager(this));

        subApplicationVisibilityHeading.setText("Platforms");

        database.subApplicationDao().getSubApplicationsByApplicationId(applicationId).observe(this, subApplications -> {
            Log.d("Sub Application Settings", "Applications Size: " + subApplications.size());

            if (subApplications.isEmpty()) {
                subApplicationVisibilityEmptyStateMessage.setVisibility(View.VISIBLE);
                recyclerViewVisibilitySubApplication.setVisibility(View.GONE);
            } else {
                subApplicationVisibilityEmptyStateMessage.setVisibility(View.GONE);
                recyclerViewVisibilitySubApplication.setVisibility(View.VISIBLE);
            }

            applicationSettingsAdapter = new RelatedSubApplicationSettingsAdapter(this, subApplications, (subApplication, isChecked) -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    subApplication.setVisible(isChecked);
                    database.subApplicationDao().update(subApplication);

                    // Notify that visibility has changed
                    SettingsUpdateNotifier.notifyVisibilityUpdated();
                });
            });

            recyclerViewVisibilitySubApplication.setAdapter(applicationSettingsAdapter);
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