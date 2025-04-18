package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.adapters.entertainment.TelevisionSeriesAdapter;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.models.entertainment.*;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithTelevisionSeries;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.aj.trackmate.R;
import com.aj.trackmate.operations.LongPressCallBack;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.aj.trackmate.operations.templates.ItemUpdateListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_TV_SERIES_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_TV_SERIES_EDIT;

public class TelevisionSeriesActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener, ItemUpdateListener {

    private ListView listView;
    private RecyclerView televisionSeriesRecyclerView;
    private TelevisionSeriesAdapter televisionSeriesAdapter;
    private List<EntertainmentWithTelevisionSeries> televisionSeries;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

    private EditText searchEditText;
    private List<EntertainmentWithTelevisionSeries> allTelevisionSeries;

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

        searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTelevisionSeries(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
                allTelevisionSeries = televisionSeriesList;
                televisionSeries = new ArrayList<>(allTelevisionSeries);
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
                    }, (view, position) -> {
                        LongPressCallBack longPressCallBack = new LongPressCallBack(televisionSeriesAdapter, this, this, this);
                        longPressCallBack.handleLongPress(view, position, "TV Series");
                    });
                    televisionSeriesRecyclerView.setAdapter(televisionSeriesAdapter);
                    televisionSeriesAdapter.updateTelevisionSeries(televisionSeries);
                }
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
                    televisionSeriesAdapter = new TelevisionSeriesAdapter(this, allTelevisionSeries, null, null);
                    televisionSeriesRecyclerView.setAdapter(televisionSeriesAdapter);
                }

                // Add the new movie to the list
                if (newTvSeries != null) {
                    allTelevisionSeries.add(newTvSeries);
                    televisionSeriesAdapter.updateTelevisionSeries(allTelevisionSeries);
                }

                Log.d("Television Series Action", "List count:" + allTelevisionSeries.size());

                // Update empty state visibility
                if (allTelevisionSeries.isEmpty()) {
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

    @Override
    public String getSavedItem(int position) {
        EntertainmentWithTelevisionSeries entertainmentWithTelevisionSeries = televisionSeries.get(position);
        return entertainmentWithTelevisionSeries.televisionSeries.getStatus().getStatus();
    }

    @Override
    public List<String> getItems() {
        return Arrays.stream(TelevisionSeriesStatus.values()).map(TelevisionSeriesStatus::getStatus).collect(Collectors.toList());
    }

    @Override
    public void updateItem(int position, String value) {
        EntertainmentWithTelevisionSeries entertainmentWithTelevisionSeries = televisionSeries.get(position);
        TelevisionSeries series = entertainmentWithTelevisionSeries.televisionSeries;

        // Perform database update in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            series.setStatus(TelevisionSeriesStatus.fromStatus(value));
            EntertainmentDatabase.getInstance(this).televisionSeriesDao().update(series);

            // Show a Toast on the main thread after the update is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void filterTelevisionSeries(String query) {
        String lower = query.toLowerCase();

        List<EntertainmentWithTelevisionSeries> filtered = allTelevisionSeries.stream()
                .filter(entertainmentWithTelevisionSeries -> {
                    Entertainment entertainment = entertainmentWithTelevisionSeries.entertainment;
                    TelevisionSeries tvSeries = entertainmentWithTelevisionSeries.televisionSeries;

                    boolean matchesName = entertainment.getName().toLowerCase().contains(lower);
                    boolean matchesStatus = tvSeries.getStatus().getStatus().toLowerCase().contains(lower);
                    boolean matchesLanguage = entertainment.getLanguage().getLanguage().toLowerCase().contains(lower);

                    // Match genre
                    boolean matchesGenre = tvSeries.getGenre().stream()
                            .map(TelevisionSeriesGenre::getGenre)  // assuming getGenre() returns the string name like "Fantasy"
                            .anyMatch(genre -> genre.toLowerCase().contains(lower));

                    return matchesName || matchesStatus || matchesLanguage || matchesGenre;
                })
                .collect(Collectors.toList());

        televisionSeriesAdapter.updateTelevisionSeries(filtered);

        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        televisionSeriesRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}