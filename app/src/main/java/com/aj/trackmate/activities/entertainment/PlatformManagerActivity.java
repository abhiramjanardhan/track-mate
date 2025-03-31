package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.adapters.entertainment.PlatformsAdapter;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.models.application.SubApplication;
import com.aj.trackmate.operations.SwipeToDeleteCallback;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.aj.trackmate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_PLATFORMS_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_PLATFORMS_EDIT;

public class PlatformManagerActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener {

    private String categoryName;
    private List<SubApplication> platforms;
    private RecyclerView platformsRecyclerView;
    private TextView emptyStateMessage;
    private FloatingActionButton addButton;
    private PlatformsAdapter platformsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_manager);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        platformsRecyclerView = findViewById(R.id.recyclerViewPlatforms);
        platformsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyStateMessage = findViewById(R.id.platformsEmptyStateMessage);
        addButton = findViewById(R.id.platformsFloatingButton);

        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        ApplicationDatabase applicationDatabase = ApplicationDatabase.getInstance(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Platforms");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Handle the "Add" button click
        addButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new game
            Intent intent = new Intent(this, AddPlatformsActivity.class);
            intent.putExtra("CATEGORY_NAME", categoryName);
            startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_PLATFORMS_ADD); // Request code to identify the result
        });

        if (categoryName != null) {
            applicationDatabase.applicationDao().getApplicationsByName(categoryName).observe(this, application -> {
                if (!application.isHasSubApplication()) {
                    addButton.hide();
                }

                applicationDatabase.subApplicationDao().getSubApplicationsByApplicationId(application.getId()).observe(this, subApplicationsList -> {
                    platforms = subApplicationsList;
                    Log.d("Sub Applications", "List: " + platforms);

                    if (platforms == null || platforms.isEmpty()) {
                        emptyStateMessage.setVisibility(View.VISIBLE);
                        platformsRecyclerView.setVisibility(View.GONE);
                    } else {
                        emptyStateMessage.setVisibility(View.GONE);
                        platformsRecyclerView.setVisibility(View.VISIBLE);

                        platformsAdapter = new PlatformsAdapter(this, platforms, platform -> {
                            Intent intent = new Intent(PlatformManagerActivity.this, EditPlatformsActivity.class);
                            intent.putExtra("PLATFORM_ID", platform.getId());
                            intent.putExtra("PLATFORM_NAME", platform.getName());
                            intent.putExtra("PLATFORM_DESCRIPTION", platform.getDescription());
                            startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_PLATFORMS_EDIT);
                        });
                        platformsRecyclerView.setAdapter(platformsAdapter);
                        platformsAdapter.updateSubApplications(platforms);  // Notify adapter of new data
                    }

                    // Setup the swipe-to-delete functionality
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(platformsAdapter, this, this));
                    itemTouchHelper.attachToRecyclerView(platformsRecyclerView);
                });
            });
        } else {
            platforms = new ArrayList<>();
            emptyStateMessage.setVisibility(View.VISIBLE);
            platformsRecyclerView.setVisibility(View.GONE);
        }
    }

    // Handle back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("REFRESH_LIST", true);  // Set the flag
            setResult(RESULT_OK, resultIntent);  // Send result back
            finish(); // Close the activity when the back button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Retrieve the new game from the result
            SubApplication newPlatform = null;
            if (requestCode == REQUEST_CODE_ENTERTAINMENT_PLATFORMS_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newPlatform = data.getParcelableExtra("NEW_PLATFORM", SubApplication.class);
                Log.d("Platform Action", "Save:" + newPlatform);

                if (platformsAdapter == null) {
                    platformsAdapter = new PlatformsAdapter(this, platforms, null);
                    platformsRecyclerView.setAdapter(platformsAdapter);
                }

                // Add the new game to the list
                if (newPlatform != null) {
                    platforms.add(newPlatform);
                    platformsAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the RecyclerView
                }

                Log.d("Platform Action", "List count:" + platforms.size());

                // Update empty state visibility
                if (platforms.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    platformsRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    platformsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_ENTERTAINMENT_PLATFORMS_EDIT) {
                int updatedGameId = data.getIntExtra("UPDATED_PLATFORM_ID", -1);
                if (updatedGameId != -1) {
                    platformsAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }
        }
    }

    // Implement the removeItem method
    @Override
    public void removeItem(int position) {
        SubApplication platform = platforms.get(position);
        platformsAdapter.removePlatform(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            ApplicationDatabase.getInstance(this).subApplicationDao().delete(platform);  // Deleting the item from the database

            // Show a Toast on the main thread after the deletion is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public boolean isReadOnly(int position) {
        return platforms.get(position).isReadOnly();
    }
}