package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.SubCategoryAdapter;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.SubApplication;

import java.util.ArrayList;
import java.util.List;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_PLATFORMS;

public class EntertainmentCategoryActivity extends AppCompatActivity {

    private TextView textViewSubCategory;
    private RecyclerView recyclerViewSubcategories;
    private Application application;
    private List<SubApplication> subcategories;
    private String categoryName;
    private SubCategoryAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment_category);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewSubCategory = findViewById(R.id.textViewEntertainmentCategory);
        recyclerViewSubcategories = findViewById(R.id.recyclerViewEntertainmentCategory);
        recyclerViewSubcategories.setLayoutManager(new LinearLayoutManager(this));

        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        ApplicationDatabase.getInstance(this).applicationDao().getApplicationsByName(categoryName).observe(this, currentApplication -> {
            application = currentApplication;
        });

        if (getSupportActionBar() != null) {
            textViewSubCategory.setText("Platforms");
            getSupportActionBar().setTitle(categoryName);  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            subcategories = getIntent().getParcelableArrayListExtra("SUBCATEGORIES");
        } else {
            subcategories = new ArrayList<>();
        }

        adapter = new SubCategoryAdapter(this, categoryName, subcategories);
        recyclerViewSubcategories.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.platform_menu, menu);
        return true;
    }

    // Handle back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the back button is pressed
            return true;
        } else if (item.getItemId() == R.id.action_manage_platform) {
            Intent intent = new Intent(this, PlatformManagerActivity.class);
            intent.putExtra("CATEGORY_NAME", categoryName);
            startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_PLATFORMS);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Entertainment Category", "Request code: " + requestCode);
        Log.d("Entertainment Category", "Result code: " + resultCode);

        if (requestCode == REQUEST_CODE_ENTERTAINMENT_PLATFORMS && resultCode == RESULT_OK) {
            boolean shouldRefresh = data.getBooleanExtra("REFRESH_LIST", false);
            if (shouldRefresh) {
                refreshList(); // Call your method to refresh the list
            }
        }
    }

    private void refreshList() {
        ApplicationDatabase.getInstance(this).subApplicationDao().getSubApplicationsByApplicationId(application.getId()).observe(this, subApplications -> {
            adapter.updateSubApplications(subApplications); // Update adapter
        });
    }
}