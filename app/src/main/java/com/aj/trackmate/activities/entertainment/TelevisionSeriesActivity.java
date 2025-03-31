package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.adapters.entertainment.TelevisionSeriesAdapter;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.EntertainmentCategory;
import com.aj.trackmate.models.entertainment.TelevisionSeries;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithTelevisionSeries;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.aj.trackmate.R;
import com.aj.trackmate.operations.SwipeToDeleteCallback;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_TV_SERIES_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_TV_SERIES_EDIT;

public class TelevisionSeriesActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener {

    private ListView listView;
    private RecyclerView televisionSeriesRecyclerView;
    private TelevisionSeriesAdapter televisionSeriesAdapter;
    private List<EntertainmentWithTelevisionSeries> televisionSeries;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_television_series);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        televisionSeriesRecyclerView = findViewById(R.id.recyclerViewTelevisionSeries);
        televisionSeriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.televisionSeriesFloatingButton);
        emptyStateMessage = findViewById(R.id.televisionSeriesEmptyStateMessage);
        title = findViewById(R.id.televisionSeriesTitle);

        // Get the platform name from the Intent
        String category = getIntent().getStringExtra("CATEGORY");
        Log.d("Television Series", "Category: " + category);
        title.setText(category + " List");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title.getText());  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Handle the "Add" button click
        addButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new television series
            Intent intent = new Intent(this, AddTelevisionSeriesActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_TV_SERIES_ADD); // Request code to identify the result
        });

        if (category != null) {
            EntertainmentDatabase.getInstance(this).entertainmentDao().getAllEntertainmentForTelevisionSeries(EntertainmentCategory.TELEVISION_SERIES, category).observe(this, televisionSeriesList -> {
                televisionSeries = televisionSeriesList;

                Log.d("Television Series", "List: " + televisionSeries.size());

                if (televisionSeries == null || televisionSeries.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    televisionSeriesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    televisionSeriesRecyclerView.setVisibility(View.VISIBLE);

                    televisionSeriesAdapter = new TelevisionSeriesAdapter(this, televisionSeries, entertainmentWithTelevisionSeries -> {
                        Intent intent = new Intent(TelevisionSeriesActivity.this, EditTelevisionSeriesActivity.class);
                        intent.putExtra("ENTERTAINMENT_ID", entertainmentWithTelevisionSeries.entertainment.getId());
                        intent.putExtra("TELEVISION_SERIES_ID", entertainmentWithTelevisionSeries.televisionSeries.getId());
                        intent.putExtra("TELEVISION_SERIES_NAME", entertainmentWithTelevisionSeries.entertainment.getName());
                        intent.putExtra("TELEVISION_SERIES_LANGUAGE", entertainmentWithTelevisionSeries.entertainment.getLanguage().getLanguage());
                        startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_TV_SERIES_EDIT);
                    });
                    televisionSeriesRecyclerView.setAdapter(televisionSeriesAdapter);
                    televisionSeriesAdapter.updateTelevisionSeries(televisionSeries);
                }

                // Setup the swipe-to-delete functionality
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(televisionSeriesAdapter, this, this));
                itemTouchHelper.attachToRecyclerView(televisionSeriesRecyclerView);
            });
        } else {
            televisionSeries = new ArrayList<>();
            emptyStateMessage.setVisibility(View.VISIBLE);
            televisionSeriesRecyclerView.setVisibility(View.GONE);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Retrieve the new movie from the result
            EntertainmentWithTelevisionSeries newTvSeries = null;
            if (requestCode == REQUEST_CODE_ENTERTAINMENT_TV_SERIES_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newTvSeries = data.getParcelableExtra("NEW_TELEVISION_SERIES", EntertainmentWithTelevisionSeries.class);
                Log.d("Television Series Action", "Save:" + newTvSeries);

                if (televisionSeriesAdapter == null) {
                    televisionSeriesAdapter = new TelevisionSeriesAdapter(this, televisionSeries, null);
                    televisionSeriesRecyclerView.setAdapter(televisionSeriesAdapter);
                }

                // Add the new movie to the list
                if (newTvSeries != null) {
                    televisionSeries.add(newTvSeries);
                    televisionSeriesAdapter.updateTelevisionSeries(televisionSeries);
                }

                Log.d("Television Series Action", "List count:" + televisionSeries.size());

                // Update empty state visibility
                if (televisionSeries.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    televisionSeriesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    televisionSeriesRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_ENTERTAINMENT_TV_SERIES_EDIT) {
                int updatedBookId = data.getIntExtra("UPDATED_TELEVISION_SERIES_ID", -1);
                if (updatedBookId != -1) {
                    televisionSeriesAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }
        }
    }

    @Override
    public void removeItem(int position) {
        EntertainmentWithTelevisionSeries entertainmentWithTelevisionSeries = televisionSeries.get(position);
        Entertainment entertainment = entertainmentWithTelevisionSeries.entertainment;
        TelevisionSeries series = entertainmentWithTelevisionSeries.televisionSeries;
        televisionSeriesAdapter.removeTelevisionSeries(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            EntertainmentDatabase.getInstance(this).televisionSeriesDao().delete(series);  // Deleting the item from the database
            EntertainmentDatabase.getInstance(this).entertainmentDao().delete(entertainment);  // Deleting the item from the database

            // Show a Toast on the main thread after the deletion is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public boolean isReadOnly(int position) {
        return false;
    }
}