package com.aj.trackmate.activities.entertainment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.R;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.models.entertainment.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddTelevisionSeriesActivity extends AppCompatActivity {

    private EditText televisionSeriesNameEditText;
    private Spinner televisionSeriesLanguageSpinner, televisionSeriesStatusSpinner;
    private TextView televisionSeriesGenreMultiSelect;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private EditText televisionSeriesTotalSeasons, televisionSeriesTotalSeasonsEpisodes, televisionSeriesCurrentSeason, televisionSeriesCurrentSeasonEpisodeNumber;
    private Button saveButton;

    private boolean[] selectedGenres;
    private List<TelevisionSeriesGenre> selectedGenreList = new ArrayList<>();
    private String[] genreArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_television_series);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the platform name from the Intent
        String platform = getIntent().getStringExtra("CATEGORY");
        Log.d("Add Television Series", "Platform: " + platform);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Television Series");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        televisionSeriesNameEditText = findViewById(R.id.televisionSeriesNameEditText);
        televisionSeriesLanguageSpinner = findViewById(R.id.televisionSeriesLanguageSpinner);
        televisionSeriesStatusSpinner = findViewById(R.id.televisionSeriesStatusSpinner);

        televisionSeriesGenreMultiSelect = findViewById(R.id.televisionSeriesGenreMultiSelect);

        wishlistYes = findViewById(R.id.wishlistYes);
        wishlistNo = findViewById(R.id.wishlistNo);

        startedYes = findViewById(R.id.startedYes);
        startedNo = findViewById(R.id.startedNo);

        completedYes = findViewById(R.id.completedYes);
        completedNo = findViewById(R.id.completedNo);

        backlogYes = findViewById(R.id.backlogYes);
        backlogNo = findViewById(R.id.backlogNo);

        televisionSeriesTotalSeasons = findViewById(R.id.televisionSeriesTotalSeasons);
        televisionSeriesTotalSeasonsEpisodes = findViewById(R.id.televisionSeriesTotalSeasonsEpisodes);
        televisionSeriesCurrentSeason = findViewById(R.id.televisionSeriesCurrentSeason);
        televisionSeriesCurrentSeasonEpisodeNumber = findViewById(R.id.televisionSeriesCurrentSeasonEpisodeNumber);

        saveButton = findViewById(R.id.saveButton);

        // Convert Enum to String Array
        TelevisionSeriesGenre[] genres = TelevisionSeriesGenre.values();
        genreArray = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreArray[i] = genres[i].getGenre();
        }

        selectedGenres = new boolean[genreArray.length];

        wishlistNo.setChecked(true);
        startedNo.setChecked(true);
        completedNo.setChecked(true);
        backlogNo.setChecked(true);

        // Set up the Television Series Language Spinner (Dropdown)
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.television_series_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        televisionSeriesLanguageSpinner.setAdapter(languageAdapter);

        // Set up the Television Series Status Spinner (Dropdown)
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.television_series_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        televisionSeriesStatusSpinner.setAdapter(statusAdapter);

        // Set click listener to open dialog
        televisionSeriesGenreMultiSelect.setOnClickListener(v -> showGenreDialog());

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Read values from input fields
            String televisionSeriesName = televisionSeriesNameEditText.getText().toString().trim();
            String televisionSeriesLanguage = televisionSeriesLanguageSpinner.getSelectedItem().toString();
            String televisionSeriesStatus = televisionSeriesStatusSpinner.getSelectedItem().toString();

            if (televisionSeriesName.isEmpty()) {
                televisionSeriesNameEditText.setError("Television Series Name is required");
                televisionSeriesNameEditText.requestFocus();
                return;
            }

            boolean isWishlist = wishlistYes.isChecked();
            boolean isStarted = startedYes.isChecked();
            boolean isCompleted = completedYes.isChecked();
            boolean isBacklog = backlogYes.isChecked();
            String totalSeasons = televisionSeriesTotalSeasons.getText().toString().trim();
            String totalSeasonsEpisodes = televisionSeriesTotalSeasonsEpisodes.getText().toString().trim();
            String currentSeason = televisionSeriesCurrentSeason.getText().toString().trim();
            String currentSeasonEpisodeNumber = televisionSeriesCurrentSeasonEpisodeNumber.getText().toString().trim();

            Entertainment entertainment = new Entertainment();
            TelevisionSeries televisionSeries = new TelevisionSeries();
            entertainment.setName(televisionSeriesName);
            entertainment.setLanguage(Language.fromLanguage(televisionSeriesLanguage));
            entertainment.setCategory(EntertainmentCategory.TELEVISION_SERIES);

            Executors.newSingleThreadExecutor().execute(() -> {
                int entertainmentId = (int) EntertainmentDatabase.getInstance(this).entertainmentDao().insert(entertainment);

                // now insert the movie
                televisionSeries.setEntertainmentId(entertainmentId);
                televisionSeries.setGenre(selectedGenreList);
                televisionSeries.setPlatform(platform);
                televisionSeries.setStatus(TelevisionSeriesStatus.fromStatus(televisionSeriesStatus));
                televisionSeries.setWishlist(isWishlist);
                televisionSeries.setStarted(isStarted);
                televisionSeries.setCompleted(isCompleted);
                televisionSeries.setBacklog(isBacklog);
                televisionSeries.setTotalSeasons(totalSeasons.isEmpty() ? 0 : Integer.parseInt(totalSeasons));
                televisionSeries.setTotalEpisodesCount(totalSeasonsEpisodes.isEmpty() ? 0 : Integer.parseInt(totalSeasonsEpisodes));
                televisionSeries.setCurrentSeason(currentSeason.isEmpty() ? 0 : Integer.parseInt(currentSeason));
                televisionSeries.setCurrentEpisodeNumber(currentSeasonEpisodeNumber.isEmpty() ? 0 : Integer.parseInt(currentSeasonEpisodeNumber));

                Log.d("Add Television Series", "Platform: " + televisionSeries.getPlatform());
                Log.d("Add Television Series", "Status: " + televisionSeries.getStatus());

                EntertainmentDatabase.getInstance(this).televisionSeriesDao().insert(televisionSeries);

                // Switch to the main thread to observe LiveData
                runOnUiThread(() -> {
                    EntertainmentDatabase.getInstance(this).entertainmentDao()
                            .getTelevisionSeriesEntertainmentByEntertainmentId(entertainmentId)
                            .observe(this, entertainmentWithTelevisionSeries -> {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("NEW_TELEVISION_SERIES", entertainmentWithTelevisionSeries);  // Add the movie as Parcelable
                                setResult(RESULT_OK, resultIntent);
                                finish();  // Close the activity
                            });
                });
            });
        });
    }

    private void showGenreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Genres");

        builder.setMultiChoiceItems(genreArray, selectedGenres, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedGenreList.add(TelevisionSeriesGenre.values()[which]);
            } else {
                selectedGenreList.remove(TelevisionSeriesGenre.values()[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> updateGenreText());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void updateGenreText() {
        if (selectedGenreList.isEmpty()) {
            televisionSeriesGenreMultiSelect.setText(R.string.select_genre);
        } else {
            StringBuilder genresText = new StringBuilder();
            for (TelevisionSeriesGenre genre : selectedGenreList) {
                genresText.append(genre.getGenre()).append(", ");
            }
            televisionSeriesGenreMultiSelect.setText(genresText.substring(0, genresText.length() - 2));
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
}