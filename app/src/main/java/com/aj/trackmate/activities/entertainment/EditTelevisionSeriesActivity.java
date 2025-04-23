package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.R;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.models.entertainment.Language;
import com.aj.trackmate.models.entertainment.TelevisionSeriesGenre;
import com.aj.trackmate.models.entertainment.TelevisionSeriesStatus;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithTelevisionSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class EditTelevisionSeriesActivity extends AppCompatActivity {

    private int entertainmentId, televisionSeriesId;
    private String televisionSeriesName;
    private Language language;
    private EntertainmentWithTelevisionSeries entertainmentWithTelevisionSeries;
    private boolean isEditMode = false;

    private EditText televisionSeriesNameEditText;
    private Spinner televisionSeriesLanguageSpinner, televisionSeriesStatusSpinner;
    private TextView editTelevisionSeriesHeading, televisionSeriesGenreMultiSelect;
    private RadioButton favoriteYes, favoriteNo;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private EditText televisionSeriesTotalSeasons, televisionSeriesTotalSeasonsEpisodes, televisionSeriesCurrentSeason, televisionSeriesCurrentSeasonEpisodeNumber;
    private Button saveButton, editButton, cancelButton;
    private RadioGroup favoriteTelevisionSeriesGroup, wishlistTelevisionSeriesGroup, startedTelevisionSeriesGroup, completedTelevisionSeriesGroup, backlogTelevisionSeriesGroup;

    private boolean[] selectedGenres;
    private List<TelevisionSeriesGenre> selectedGenreList = new ArrayList<>();
    private String[] genreArray;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_television_series);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ids from the Intent
        entertainmentId = getIntent().getIntExtra("ENTERTAINMENT_ID", -1);
        televisionSeriesId = getIntent().getIntExtra("TELEVISION_SERIES_ID", -1);
        televisionSeriesName = getIntent().getStringExtra("TELEVISION_SERIES_NAME");
        language = Language.fromLanguage(getIntent().getStringExtra("TELEVISION_SERIES_LANGUAGE"));
        Log.d("Edit Television Series", "Television Series: " + televisionSeriesName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Television Series");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        televisionSeriesNameEditText = findViewById(R.id.televisionSeriesNameEditText);
        televisionSeriesLanguageSpinner = findViewById(R.id.televisionSeriesLanguageEditSpinner);
        televisionSeriesStatusSpinner = findViewById(R.id.televisionSeriesStatusEditSpinner);

        televisionSeriesGenreMultiSelect = findViewById(R.id.televisionSeriesGenreEditMultiSelect);

        favoriteYes = findViewById(R.id.favoriteYes);
        favoriteNo = findViewById(R.id.favoriteNo);

        wishlistYes = findViewById(R.id.wishlistYes);
        wishlistNo = findViewById(R.id.wishlistNo);

        startedYes = findViewById(R.id.startedYes);
        startedNo = findViewById(R.id.startedNo);

        completedYes = findViewById(R.id.completedYes);
        completedNo = findViewById(R.id.completedNo);

        backlogYes = findViewById(R.id.backlogYes);
        backlogNo = findViewById(R.id.backlogNo);

        televisionSeriesTotalSeasons = findViewById(R.id.televisionSeriesEditTotalSeasons);
        televisionSeriesTotalSeasonsEpisodes = findViewById(R.id.televisionSeriesEditTotalSeasonsEpisodes);
        televisionSeriesCurrentSeason = findViewById(R.id.televisionSeriesEditCurrentSeason);
        televisionSeriesCurrentSeasonEpisodeNumber = findViewById(R.id.televisionSeriesEditCurrentSeasonEpisodeNumber);

        saveButton = findViewById(R.id.saveTelevisionSeriesButton);
        cancelButton = findViewById(R.id.cancelTelevisionSeriesButton);
        editButton = findViewById(R.id.editTelevisionSeriesButton);

        favoriteTelevisionSeriesGroup = findViewById(R.id.favoriteTelevisionSeriesGroup);
        wishlistTelevisionSeriesGroup = findViewById(R.id.wishlistTelevisionSeriesGroup);
        startedTelevisionSeriesGroup = findViewById(R.id.startedTelevisionSeriesGroup);
        completedTelevisionSeriesGroup = findViewById(R.id.completedTelevisionSeriesGroup);
        backlogTelevisionSeriesGroup = findViewById(R.id.backlogTelevisionSeriesGroup);
        editTelevisionSeriesHeading = findViewById(R.id.editTelevisionSeriesHeading);

        // Convert Enum to String Array
        TelevisionSeriesGenre[] genres = TelevisionSeriesGenre.values();
        genreArray = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreArray[i] = genres[i].getGenre();
        }

        selectedGenres = new boolean[genreArray.length];
        editTelevisionSeriesHeading.setText("View Television Series");

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

        EntertainmentDatabase.getInstance(this).televisionSeriesDao().getTelevisionSeriesEntertainmentByTelevisionSeriesId(entertainmentId, televisionSeriesId).observe(this, televisionSeries -> {
            entertainmentWithTelevisionSeries = televisionSeries;

            if (entertainmentWithTelevisionSeries != null) {
                televisionSeriesNameEditText.setText(televisionSeriesName);

                ArrayAdapter televisionSeriesLanguageSpinnerAdapter = (ArrayAdapter) televisionSeriesLanguageSpinner.getAdapter();
                int languagePosition = televisionSeriesLanguageSpinnerAdapter.getPosition(entertainmentWithTelevisionSeries.entertainment.getLanguage().getLanguage());
                televisionSeriesLanguageSpinner.setSelection(languagePosition);

                ArrayAdapter televisionSeriesStatusSpinnerAdapter = (ArrayAdapter) televisionSeriesStatusSpinner.getAdapter();
                int statusPosition = televisionSeriesStatusSpinnerAdapter.getPosition(entertainmentWithTelevisionSeries.televisionSeries.getStatus().getStatus());
                televisionSeriesStatusSpinner.setSelection(statusPosition);

                // Retrieve the selected genres from the movie object
                selectedGenreList = new ArrayList<>(entertainmentWithTelevisionSeries.televisionSeries.getGenre());
                // Reset the selectedGenres array
                selectedGenres = new boolean[genreArray.length];

                // Update selectedGenres based on the stored values
                for (TelevisionSeriesGenre genre : selectedGenreList) {
                    for (int i = 0; i < genreArray.length; i++) {
                        if (genreArray[i].equals(genre.getGenre())) {
                            selectedGenres[i] = true;
                            break;
                        }
                    }
                }

                // Update the UI to reflect the selected genres
                updateGenreText();

                if (entertainmentWithTelevisionSeries.televisionSeries.isFavorite()) {
                    favoriteYes.setChecked(true);
                } else {
                    favoriteNo.setChecked(true);
                }

                if (entertainmentWithTelevisionSeries.televisionSeries.isWishlist()) {
                    wishlistYes.setChecked(true);
                } else {
                    wishlistNo.setChecked(true);
                }

                if (entertainmentWithTelevisionSeries.televisionSeries.isStarted()) {
                    startedYes.setChecked(true);
                } else {
                    startedNo.setChecked(true);
                }

                if (entertainmentWithTelevisionSeries.televisionSeries.isCompleted()) {
                    completedYes.setChecked(true);
                } else {
                    completedNo.setChecked(true);
                }

                if (entertainmentWithTelevisionSeries.televisionSeries.isBacklog()) {
                    backlogYes.setChecked(true);
                } else {
                    backlogNo.setChecked(true);
                }

                televisionSeriesTotalSeasons.setText(String.valueOf(entertainmentWithTelevisionSeries.televisionSeries.getTotalSeasons()));
                televisionSeriesTotalSeasonsEpisodes.setText(String.valueOf(entertainmentWithTelevisionSeries.televisionSeries.getTotalEpisodesCount()));
                televisionSeriesCurrentSeason.setText(String.valueOf(entertainmentWithTelevisionSeries.televisionSeries.getCurrentSeason()));
                televisionSeriesCurrentSeasonEpisodeNumber.setText(String.valueOf(entertainmentWithTelevisionSeries.televisionSeries.getCurrentEpisodeNumber()));
            }
        });

        // Set initial state (readonly)
        setEditMode(false);

        // Handle Edit button click
        editButton.setOnClickListener(v -> {
            isEditMode = true;
            editTelevisionSeriesHeading.setText("Edit Television Series");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Television Series");  // Change the title dynamically
            setEditMode(true);
        });

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            saveTelevisionSeriesDetails();
            finish(); // Go back to listing page
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            resetToOriginalState();
            isEditMode = false;
            editTelevisionSeriesHeading.setText("View Television Series");
            Objects.requireNonNull(getSupportActionBar()).setTitle("View Television Series");  // Change the title dynamically
            setEditMode(false);
        });
    }

    @SuppressLint("SetTextI18n")
    private void setEditMode(boolean enabled) {
        televisionSeriesNameEditText.setEnabled(enabled);
        televisionSeriesLanguageSpinner.setEnabled(enabled);
        televisionSeriesStatusSpinner.setEnabled(enabled);
        televisionSeriesGenreMultiSelect.setEnabled(enabled);

        favoriteTelevisionSeriesGroup.setEnabled(enabled);
        favoriteYes.setEnabled(enabled);
        favoriteNo.setEnabled(enabled);

        wishlistTelevisionSeriesGroup.setEnabled(enabled);
        wishlistYes.setEnabled(enabled);
        wishlistNo.setEnabled(enabled);

        startedTelevisionSeriesGroup.setEnabled(enabled);
        startedYes.setEnabled(enabled);
        startedNo.setEnabled(enabled);

        completedTelevisionSeriesGroup.setEnabled(enabled);
        completedYes.setEnabled(enabled);
        completedNo.setEnabled(enabled);

        backlogTelevisionSeriesGroup.setEnabled(enabled);
        backlogYes.setEnabled(enabled);
        backlogNo.setEnabled(enabled);

        televisionSeriesTotalSeasons.setEnabled(enabled);
        televisionSeriesTotalSeasonsEpisodes.setEnabled(enabled);
        televisionSeriesCurrentSeason.setEnabled(enabled);
        televisionSeriesCurrentSeasonEpisodeNumber.setEnabled(enabled);

        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void saveTelevisionSeriesDetails() {
        if (entertainmentWithTelevisionSeries != null) {
            // Read values from input fields
            String televisionSeriesName = televisionSeriesNameEditText.getText().toString().trim();
            String televisionSeriesLanguage = televisionSeriesLanguageSpinner.getSelectedItem().toString();
            String televisionSeriesStatus = televisionSeriesStatusSpinner.getSelectedItem().toString();

            boolean isFavorite = favoriteYes.isChecked();
            boolean isWishlist = wishlistYes.isChecked();
            boolean isStarted = startedYes.isChecked();
            boolean isCompleted = completedYes.isChecked();
            boolean isBacklog = backlogYes.isChecked();
            String totalSeasons = televisionSeriesTotalSeasons.getText().toString().trim();
            String totalSeasonsEpisodes = televisionSeriesTotalSeasonsEpisodes.getText().toString().trim();
            String currentSeason = televisionSeriesCurrentSeason.getText().toString().trim();
            String currentSeasonEpisodeNumber = televisionSeriesCurrentSeasonEpisodeNumber.getText().toString().trim();

            entertainmentWithTelevisionSeries.entertainment.setName(televisionSeriesName);
            entertainmentWithTelevisionSeries.entertainment.setLanguage(Language.fromLanguage(televisionSeriesLanguage));

            Executors.newSingleThreadExecutor().execute(() -> {
                EntertainmentDatabase.getInstance(this).entertainmentDao().update(entertainmentWithTelevisionSeries.entertainment);

                entertainmentWithTelevisionSeries.televisionSeries.setGenre(selectedGenreList);
                entertainmentWithTelevisionSeries.televisionSeries.setStatus(TelevisionSeriesStatus.fromStatus(televisionSeriesStatus));
                entertainmentWithTelevisionSeries.televisionSeries.setFavorite(isFavorite);
                entertainmentWithTelevisionSeries.televisionSeries.setWishlist(isWishlist);
                entertainmentWithTelevisionSeries.televisionSeries.setStarted(isStarted);
                entertainmentWithTelevisionSeries.televisionSeries.setCompleted(isCompleted);
                entertainmentWithTelevisionSeries.televisionSeries.setBacklog(isBacklog);
                entertainmentWithTelevisionSeries.televisionSeries.setTotalSeasons(totalSeasons.isEmpty() ? 0 : Integer.parseInt(totalSeasons));
                entertainmentWithTelevisionSeries.televisionSeries.setTotalEpisodesCount(totalSeasonsEpisodes.isEmpty() ? 0 : Integer.parseInt(totalSeasonsEpisodes));
                entertainmentWithTelevisionSeries.televisionSeries.setCurrentSeason(currentSeason.isEmpty() ? 0 : Integer.parseInt(currentSeason));
                entertainmentWithTelevisionSeries.televisionSeries.setCurrentEpisodeNumber(currentSeasonEpisodeNumber.isEmpty() ? 0 : Integer.parseInt(currentSeasonEpisodeNumber));

                EntertainmentDatabase.getInstance(this).televisionSeriesDao().update(entertainmentWithTelevisionSeries.televisionSeries);

                Log.d("Edit Television Series", "Platform: " + entertainmentWithTelevisionSeries.televisionSeries.getPlatform());
                Log.d("Edit Television Series", "Status: " + entertainmentWithTelevisionSeries.televisionSeries.getStatus());

                runOnUiThread(() -> {
                    Toast.makeText(this, "Television Series updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_TELEVISION_SERIES_ID", entertainmentWithTelevisionSeries.entertainment.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the activity and go back to the listing page
                });
            });
        }
    }

    private void resetToOriginalState() {
        // Reset fields to original values (if stored)
        televisionSeriesNameEditText.setText(televisionSeriesName);
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