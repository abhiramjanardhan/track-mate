package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.models.entertainment.Language;
import com.aj.trackmate.models.entertainment.MovieGenre;
import com.aj.trackmate.models.entertainment.MovieStatus;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMovies;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.aj.trackmate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class EditMoviesActivity extends AppCompatActivity {

    private int entertainmentId, movieId;
    private String movieName;
    private Language language;
    private EntertainmentWithMovies entertainmentWithMovie;
    private boolean isEditMode = false;

    private EditText movieNameEditText;
    private Spinner movieLanguageSpinner, movieStatusSpinner;
    private TextView editMoviesHeading, movieGenreMultiSelect;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private Button saveButton, editButton, cancelButton;
    private RadioGroup wishlistMovieGroup, startedMovieGroup, completedMovieGroup, backlogMovieGroup;

    private boolean[] selectedGenres;
    private List<MovieGenre> selectedGenreList = new ArrayList<>();
    private String[] genreArray;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movies);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ids from the Intent
        entertainmentId = getIntent().getIntExtra("ENTERTAINMENT_ID", -1);
        movieId = getIntent().getIntExtra("MOVIE_ID", -1);
        movieName = getIntent().getStringExtra("MOVIE_NAME");
        language = Language.fromLanguage(getIntent().getStringExtra("MOVIE_LANGUAGE"));
        Log.d("Edit Movie", "Movie: " + movieName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Movie");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        movieNameEditText = findViewById(R.id.movieNameEditText);
        movieLanguageSpinner = findViewById(R.id.movieLanguageSpinner);
        movieStatusSpinner = findViewById(R.id.movieStatusSpinner);

        editMoviesHeading = findViewById(R.id.editMoviesHeading);
        movieGenreMultiSelect = findViewById(R.id.movieGenreMultiSelect);

        wishlistYes = findViewById(R.id.wishlistYes);
        wishlistNo = findViewById(R.id.wishlistNo);

        startedYes = findViewById(R.id.startedYes);
        startedNo = findViewById(R.id.startedNo);

        completedYes = findViewById(R.id.completedYes);
        completedNo = findViewById(R.id.completedNo);

        backlogYes = findViewById(R.id.backlogYes);
        backlogNo = findViewById(R.id.backlogNo);

        saveButton = findViewById(R.id.saveMovieButton);
        editButton = findViewById(R.id.editMovieButton);
        cancelButton = findViewById(R.id.cancelMovieButton);

        wishlistMovieGroup = findViewById(R.id.wishlistMovieGroup);
        startedMovieGroup = findViewById(R.id.startedMovieGroup);
        completedMovieGroup = findViewById(R.id.completedMovieGroup);
        backlogMovieGroup = findViewById(R.id.backlogMovieGroup);

        // Convert Enum to String Array
        MovieGenre[] genres = MovieGenre.values();
        genreArray = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreArray[i] = genres[i].getGenre();
        }

        selectedGenres = new boolean[genreArray.length];
        editMoviesHeading.setText("View Movie");

        // Set up the Movie Language Spinner (Dropdown)
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.movie_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        movieLanguageSpinner.setAdapter(languageAdapter);

        // Set up the Movie Status Spinner (Dropdown)
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.movie_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        movieStatusSpinner.setAdapter(statusAdapter);

        // Set click listener to open dialog
        movieGenreMultiSelect.setOnClickListener(v -> showGenreDialog());

        EntertainmentDatabase.getInstance(this).movieDao().getMoviesEntertainmentByMoviesId(entertainmentId, movieId).observe(this, entertainmentWithMovies -> {
            entertainmentWithMovie = entertainmentWithMovies;

            if (entertainmentWithMovie != null) {
                movieNameEditText.setText(movieName);

                ArrayAdapter movieLanguageSpinnerAdapter = (ArrayAdapter) movieLanguageSpinner.getAdapter();
                int languagePosition = movieLanguageSpinnerAdapter.getPosition(entertainmentWithMovie.entertainment.getLanguage().getLanguage());
                movieLanguageSpinner.setSelection(languagePosition);

                ArrayAdapter movieStatusSpinnerAdapter = (ArrayAdapter) movieStatusSpinner.getAdapter();
                int statusPosition = movieStatusSpinnerAdapter.getPosition(entertainmentWithMovie.movie.getStatus().getStatus());
                movieStatusSpinner.setSelection(statusPosition);

                // Retrieve the selected genres from the movie object
                selectedGenreList = new ArrayList<>(entertainmentWithMovie.movie.getGenre());
                // Reset the selectedGenres array
                selectedGenres = new boolean[genreArray.length];

                // Update selectedGenres based on the stored values
                for (MovieGenre genre : selectedGenreList) {
                    for (int i = 0; i < genreArray.length; i++) {
                        if (genreArray[i].equals(genre.getGenre())) {
                            selectedGenres[i] = true;
                            break;
                        }
                    }
                }

                // Update the UI to reflect the selected genres
                updateGenreText();

                if (entertainmentWithMovie.movie.isWishlist()) {
                    wishlistYes.setChecked(true);
                } else {
                    wishlistNo.setChecked(true);
                }

                if (entertainmentWithMovie.movie.isStarted()) {
                    startedYes.setChecked(true);
                } else {
                    startedNo.setChecked(true);
                }

                if (entertainmentWithMovie.movie.isCompleted()) {
                    completedYes.setChecked(true);
                } else {
                    completedNo.setChecked(true);
                }

                if (entertainmentWithMovie.movie.isBacklog()) {
                    backlogYes.setChecked(true);
                } else {
                    backlogNo.setChecked(true);
                }
            }
        });

        // Set initial state (readonly)
        setEditMode(false);

        // Handle Edit button click
        editButton.setOnClickListener(v -> {
            isEditMode = true;
            editMoviesHeading.setText("Edit Game");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Movie");  // Change the title dynamically
            setEditMode(true);
        });

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            saveMovieDetails();
            finish(); // Go back to listing page
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            resetToOriginalState();
            isEditMode = false;
            editMoviesHeading.setText("View Game");
            Objects.requireNonNull(getSupportActionBar()).setTitle("View Movie");  // Change the title dynamically
            setEditMode(false);
        });
    }

    private void setEditMode(boolean enabled) {
        movieNameEditText.setEnabled(enabled);
        movieLanguageSpinner.setEnabled(enabled);
        movieStatusSpinner.setEnabled(enabled);
        movieGenreMultiSelect.setEnabled(enabled);

        wishlistMovieGroup.setEnabled(enabled);
        wishlistYes.setEnabled(enabled);
        wishlistNo.setEnabled(enabled);

        startedMovieGroup.setEnabled(enabled);
        startedYes.setEnabled(enabled);
        startedNo.setEnabled(enabled);

        completedMovieGroup.setEnabled(enabled);
        completedYes.setEnabled(enabled);
        completedNo.setEnabled(enabled);

        backlogMovieGroup.setEnabled(enabled);
        backlogYes.setEnabled(enabled);
        backlogNo.setEnabled(enabled);

        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void saveMovieDetails() {
        if (entertainmentWithMovie != null) {
            // Read values from input fields
            String movieName = movieNameEditText.getText().toString().trim();
            String movieLanguage = movieLanguageSpinner.getSelectedItem().toString();
            String movieStatus = movieStatusSpinner.getSelectedItem().toString();

            boolean isWishlist = wishlistYes.isChecked();
            boolean isStarted = startedYes.isChecked();
            boolean isCompleted = completedYes.isChecked();
            boolean isBacklog = backlogYes.isChecked();

            entertainmentWithMovie.entertainment.setName(movieName);
            entertainmentWithMovie.entertainment.setLanguage(Language.fromLanguage(movieLanguage));

            Executors.newSingleThreadExecutor().execute(() -> {
                EntertainmentDatabase.getInstance(this).entertainmentDao().update(entertainmentWithMovie.entertainment);

                entertainmentWithMovie.movie.setGenre(selectedGenreList);
                entertainmentWithMovie.movie.setStatus(MovieStatus.fromStatus(movieStatus));
                entertainmentWithMovie.movie.setWishlist(isWishlist);
                entertainmentWithMovie.movie.setStarted(isStarted);
                entertainmentWithMovie.movie.setCompleted(isCompleted);
                entertainmentWithMovie.movie.setBacklog(isBacklog);

                EntertainmentDatabase.getInstance(this).movieDao().update(entertainmentWithMovie.movie);

                Log.d("Edit Movie", "Platform: " + entertainmentWithMovie.movie.getPlatform());
                Log.d("Edit Movie", "Status: " + entertainmentWithMovie.movie.getStatus());

                runOnUiThread(() -> {
                    Toast.makeText(this, "Movie updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_MOVIE_ID", entertainmentWithMovie.entertainment.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the activity and go back to the listing page
                });
            });
        }
    }

    private void resetToOriginalState() {
        // Reset fields to original values (if stored)
        movieNameEditText.setText(movieName);
    }

    private void showGenreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Genres");

        builder.setMultiChoiceItems(genreArray, selectedGenres, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedGenreList.add(MovieGenre.values()[which]);
            } else {
                selectedGenreList.remove(MovieGenre.values()[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> updateGenreText());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void updateGenreText() {
        if (selectedGenreList.isEmpty()) {
            movieGenreMultiSelect.setText(R.string.select_genre);
        } else {
            StringBuilder genresText = new StringBuilder();
            for (MovieGenre genre : selectedGenreList) {
                genresText.append(genre.getGenre()).append(", ");
            }
            movieGenreMultiSelect.setText(genresText.substring(0, genresText.length() - 2));
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