package com.aj.trackmate.activities.settings;

import android.content.Intent;
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
import com.aj.trackmate.adapters.settings.RelatedApplicationSettingsAdapter;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.notifiers.SettingsUpdateNotifier;

import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_APPLICATION_SETTINGS_APPLICATION_SUB_APPLICATIONS_RELATED;

public class RelatedApplicationSettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVisibilityApplication;
    private TextView applicationVisibilityEmptyStateMessage;
    private RelatedApplicationSettingsAdapter applicationSettingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_application_settings);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ids from the Intent
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        ApplicationDatabase database = ApplicationDatabase.getInstance(this);
        Log.d("Application Settings", "Applications Name: " + categoryName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName + " Settings");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        applicationVisibilityEmptyStateMessage = findViewById(R.id.applicationVisibilityEmptyStateMessage);
        recyclerViewVisibilityApplication = findViewById(R.id.recyclerViewVisibilityApplication);
        recyclerViewVisibilityApplication.setLayoutManager(new LinearLayoutManager(this));

        database.applicationDao().getApplicationsWithSubApplicationsByCategory(categoryId).observe(this, applicationWithSubApplications -> {
            Log.d("Application Settings", "Applications Size: " + applicationWithSubApplications.size());

            if (applicationWithSubApplications.isEmpty()) {
                applicationVisibilityEmptyStateMessage.setVisibility(View.VISIBLE);
                recyclerViewVisibilityApplication.setVisibility(View.GONE);
            } else {
                applicationVisibilityEmptyStateMessage.setVisibility(View.GONE);
                recyclerViewVisibilityApplication.setVisibility(View.VISIBLE);
            }

            applicationSettingsAdapter = new RelatedApplicationSettingsAdapter(this, applicationWithSubApplications, (application, subApplications, isChecked) -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    // Update visibility
                    application.setVisible(isChecked);
                    database.applicationDao().update(application);

                    // Update Applications and their SubApplications
                    subApplications.forEach(subApplication -> {
                        subApplication.setVisible(isChecked);
                        database.subApplicationDao().update(subApplication);
                    });

                    // Notify that visibility has changed
                    SettingsUpdateNotifier.notifyVisibilityUpdated();
                });
            }, (application, isChecked) -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    // Update can add sub applications
                    application.setHasSubApplication(isChecked);
                    database.applicationDao().update(application);

                    // Notify that visibility has changed
                    SettingsUpdateNotifier.notifyCanAddSubApplicationUpdated();
                });
            }, (applicationId, applicationName) -> {
                Intent intent = new Intent(RelatedApplicationSettingsActivity.this, RelatedSubApplicationSettingsActivity.class);
                intent.putExtra("APPLICATION_ID", applicationId);
                intent.putExtra("APPLICATION_NAME", applicationName);
                startActivityForResult(intent, REQUEST_CODE_APPLICATION_SETTINGS_APPLICATION_SUB_APPLICATIONS_RELATED);
            });

            recyclerViewVisibilityApplication.setAdapter(applicationSettingsAdapter);
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