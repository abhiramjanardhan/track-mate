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
import com.aj.trackmate.adapters.CategoryAdapter;
import com.aj.trackmate.models.view.MainViewModel;
import com.aj.trackmate.models.view.factory.MainViewModelFactory;
import com.aj.trackmate.utils.ThemeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_APPLICATION_SETTINGS;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private TextView emptyStateMessage;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private void refreshDB() {
        executorService.execute(() -> {
            //this.deleteDatabase("application_database");
            //this.deleteDatabase("game_database");
            this.deleteDatabase("book_database");
            Log.d("Application", "Database deleted");
        });
    }

    private void refreshData() {
        // Use ViewModelProvider with Factory
        MainViewModelFactory factory = new MainViewModelFactory(this, this);
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

        //refreshDB();

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
            startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_APPLICATION_SETTINGS);
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