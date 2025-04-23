package com.aj.trackmate.activities.entertainment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.models.entertainment.*;
import androidx.appcompat.app.AppCompatActivity;

import com.aj.trackmate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddMoviesActivity extends AppCompatActivity {

    private EditText movieNameEditText;
    private Spinner movieLanguageSpinner, movieStatusSpinner;
    private TextView movieGenreMultiSelect;
    private RadioButton favoriteYes, favoriteNo;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private Button saveButton;

    private boolean[] selectedGenres;
    private List<MovieGenre> selectedGenreList = new ArrayList<>();
    private String[] genreArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movies);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the platform name from the Intent
        String platform = getIntent().getStringExtra("CATEGORY");
        Log.d("Add Movie", "Platform: " + platform);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Movie");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        movieNameEditText = findViewById(R.id.movieNameEditText);
        movieLanguageSpinner = findViewById(R.id.movieLanguageSpinner);
        movieStatusSpinner = findViewById(R.id.movieStatusSpinner);

        movieGenreMultiSelect = findViewById(R.id.movieGenreMultiSelect);

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

        saveButton = findViewById(R.id.saveButton);

        // Convert Enum to String Array
        MovieGenre[] genres = MovieGenre.values();
        genreArray = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreArray[i] = genres[i].getGenre();
        }

        selectedGenres = new boolean[genreArray.length];

        favoriteNo.setChecked(true);
        wishlistNo.setChecked(true);
        startedNo.setChecked(true);
        completedNo.setChecked(true);
        backlogNo.setChecked(true);

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

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Read values from input fields
            String movieName = movieNameEditText.getText().toString().trim();
            String movieLanguage = movieLanguageSpinner.getSelectedItem().toString();
            String movieStatus = movieStatusSpinner.getSelectedItem().toString();

            if (movieName.isEmpty()) {
                movieNameEditText.setError("Movie Name is required");
                movieNameEditText.requestFocus();
                return;
            }

            boolean isFavorite = favoriteYes.isChecked();
            boolean isWishlist = wishlistYes.isChecked();
            boolean isStarted = startedYes.isChecked();
            boolean isCompleted = completedYes.isChecked();
            boolean isBacklog = backlogYes.isChecked();

            Entertainment entertainment = new Entertainment();
            Movie movie = new Movie();
            entertainment.setName(movieName);
            entertainment.setLanguage(Language.fromLanguage(movieLanguage));
            entertainment.setCategory(EntertainmentCategory.MOVIES);

            Executors.newSingleThreadExecutor().execute(() -> {
                int entertainmentId = (int) EntertainmentDatabase.getInstance(this).entertainmentDao().insert(entertainment);

                // now insert the movie
                movie.setEntertainmentId(entertainmentId);
                movie.setGenre(selectedGenreList);
                movie.setPlatform(platform);
                movie.setStatus(MovieStatus.fromStatus(movieStatus));
                movie.setFavorite(isFavorite);
                movie.setWishlist(isWishlist);
                movie.setStarted(isStarted);
                movie.setCompleted(isCompleted);
                movie.setBacklog(isBacklog);

                Log.d("Add Movie", "Platform: " + movie.getPlatform());
                Log.d("Add Movie", "Status: " + movie.getStatus());

                EntertainmentDatabase.getInstance(this).movieDao().insert(movie);

                // Switch to the main thread to observe LiveData
                runOnUiThread(() -> {
                    EntertainmentDatabase.getInstance(this).entertainmentDao()
                            .getMoviesEntertainmentByEntertainmentId(entertainmentId)
                            .observe(this, entertainmentWithMovies -> {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("NEW_MOVIE", entertainmentWithMovies);  // Add the movie as Parcelable
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