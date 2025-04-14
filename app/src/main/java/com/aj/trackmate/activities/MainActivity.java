package com.aj.trackmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.activities.settings.MainSettingsActivity;
import com.aj.trackmate.activities.settings.TrackMateAboutActivity;
import com.aj.trackmate.adapters.CategoryAdapter;
import com.aj.trackmate.models.view.MainViewModel;
import com.aj.trackmate.models.view.factory.ViewModelFactory;
import com.aj.trackmate.utils.ThemeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_APPLICATION_INFORMATION;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_APPLICATION_SETTINGS;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private TextView emptyStateMessage;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private void refreshData() {
        // Use ViewModelProvider with Factory
        ViewModelFactory factory = new ViewModelFactory(this, this);
        MainViewModel viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        // Observe initialization state
        viewModel.getIsDataInitialized().observe(this, isInitialized -> {
            if (isInitialized) {
                viewModel.getCategories().observe(this, applications -> {
                    Log.d("Application", "Applications Size: " + applications.size());

                    if (applications.isEmpty()) {
                        emptyStateMessage.setVisibility(View.VISIBLE);
                        recyclerViewCategories.setVisibility(View.GONE);
                    } else {
                        emptyStateMessage.setVisibility(View.GONE);
                        recyclerViewCategories.setVisibility(View.VISIBLE);
                    }

                    categoryAdapter = new CategoryAdapter(this, applications);
                    recyclerViewCategories.setAdapter(categoryAdapter);
                });
            }
        });

        // Start data initialization
        viewModel.initializeData(null);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);  // Apply saved theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Main", "Check: Here");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Set Toolbar as the ActionBar

        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        emptyStateMessage = findViewById(R.id.mainEmptyStateMessage);

        refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivityForResult(new Intent(this, MainSettingsActivity.class), REQUEST_CODE_APPLICATION_SETTINGS);
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            startActivityForResult(new Intent(this, TrackMateAboutActivity.class), REQUEST_CODE_APPLICATION_INFORMATION);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this); // Ensure correct theme is applied
        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Properly shut down the executor
    }
}