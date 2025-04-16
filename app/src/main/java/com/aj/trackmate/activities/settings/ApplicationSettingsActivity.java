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
import com.aj.trackmate.adapters.settings.ApplicationSettingsAdapter;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.SubApplication;
import com.aj.trackmate.notifiers.SettingsUpdateNotifier;

import java.util.List;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_APPLICATION_SETTINGS_APPLICATION_RELATED;

public class ApplicationSettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVisibilityCategory;
    private TextView categoryVisibilityEmptyStateMessage;
    private ApplicationSettingsAdapter applicationSettingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_settings);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Visibility Settings");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        categoryVisibilityEmptyStateMessage = findViewById(R.id.categoryVisibilityEmptyStateMessage);
        recyclerViewVisibilityCategory = findViewById(R.id.recyclerViewVisibilityCategory);
        recyclerViewVisibilityCategory.setLayoutManager(new LinearLayoutManager(this));

        // Use database
        ApplicationDatabase database = ApplicationDatabase.getInstance(this);

        database.categoryDao().getAllCategoriesWithApplicationsAndSubApplications().observe(this, applications -> {
            Log.d("Application", "Applications Size: " + applications.size());

            if (applications.isEmpty()) {
                categoryVisibilityEmptyStateMessage.setVisibility(View.VISIBLE);
                recyclerViewVisibilityCategory.setVisibility(View.GONE);
            } else {
                categoryVisibilityEmptyStateMessage.setVisibility(View.GONE);
                recyclerViewVisibilityCategory.setVisibility(View.VISIBLE);
            }

            applicationSettingsAdapter = new ApplicationSettingsAdapter(this, applications, (category, applicationWithSubApplications, isChecked) -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    // Update Category
                    category.setVisible(isChecked);
                    database.categoryDao().update(category);

                    // Update Applications and their SubApplications
                    applicationWithSubApplications.forEach(applicationWithSubApplication -> {
                        Application application = applicationWithSubApplication.application;
                        List<SubApplication> subApplications = applicationWithSubApplication.subApplications;

                        application.setVisible(isChecked);
                        database.applicationDao().update(application);

                        subApplications.forEach(subApplication -> {
                            subApplication.setVisible(isChecked);
                            database.subApplicationDao().update(subApplication);
                        });
                    });

                    // Notify that visibility has changed
                    SettingsUpdateNotifier.notifyVisibilityUpdated();
                });
            },(categoryId, categoryName) -> {
                Intent intent = new Intent(ApplicationSettingsActivity.this, RelatedApplicationSettingsActivity.class);
                intent.putExtra("CATEGORY_ID", categoryId);
                intent.putExtra("CATEGORY_NAME", categoryName);
                startActivityForResult(intent, REQUEST_CODE_APPLICATION_SETTINGS_APPLICATION_RELATED);
            });

            recyclerViewVisibilityCategory.setAdapter(applicationSettingsAdapter);
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