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
import com.aj.trackmate.adapters.entertainment.MovieAdapter;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.models.entertainment.*;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMovies;
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

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_MOVIES_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_MOVIES_EDIT;

public class MoviesActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener, ItemUpdateListener {

    private ListView listView;
    private RecyclerView moviesRecyclerView;
    private MovieAdapter movieAdapter;
    private List<EntertainmentWithMovies> movies;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

    private EditText searchEditText;
    private List<EntertainmentWithMovies> allMovies;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        moviesRecyclerView = findViewById(R.id.recyclerViewMovies);
        moviesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.moviesFloatingButton);
        title = findViewById(R.id.moviesTitle);
        emptyStateMessage = findViewById(R.id.moviesEmptyStateMessage);

        searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Get the platform name from the Intent
        String category = getIntent().getStringExtra("CATEGORY");
        Log.d("Movie", "Category: " + category);
        title.setText(category + " List");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title.getText());  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Handle the "Add" button click
        addButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new movie
            Intent intent = new Intent(this, AddMoviesActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_MOVIES_ADD); // Request code to identify the result
        });

        if (category != null) {
            EntertainmentDatabase.getInstance(this).entertainmentDao().getAllEntertainmentForMovies(EntertainmentCategory.MOVIES, category).observe(this, moviesList -> {
                allMovies = moviesList;
                movies = new ArrayList<>(allMovies);
                Log.d("Movies", "List size: " + movies.size());

                if (movies == null || movies.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    moviesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    moviesRecyclerView.setVisibility(View.VISIBLE);

                    movieAdapter = new MovieAdapter(this, movies, entertainmentWithMovies -> {
                        Intent intent = new Intent(MoviesActivity.this, EditMoviesActivity.class);
                        intent.putExtra("ENTERTAINMENT_ID", entertainmentWithMovies.entertainment.getId());
                        intent.putExtra("MOVIE_ID", entertainmentWithMovies.movie.getId());
                        intent.putExtra("MOVIE_NAME", entertainmentWithMovies.entertainment.getName());
                        intent.putExtra("MOVIE_LANGUAGE", entertainmentWithMovies.entertainment.getLanguage().getLanguage());
                        startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_MOVIES_EDIT);
                    }, (view, position) -> {
                        LongPressCallBack longPressCallBack = new LongPressCallBack(movieAdapter, this, this, this);
                        longPressCallBack.handleLongPress(view, position, "Movie");
                    });
                    moviesRecyclerView.setAdapter(movieAdapter);
                    movieAdapter.updateMovies(movies);
                }
            });
        } else {
            movies = new ArrayList<>();
            emptyStateMessage.setVisibility(View.VISIBLE);
            moviesRecyclerView.setVisibility(View.GONE);
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
            EntertainmentWithMovies newMovie = null;
            if (requestCode == REQUEST_CODE_ENTERTAINMENT_MOVIES_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newMovie = data.getParcelableExtra("NEW_MOVIE", EntertainmentWithMovies.class);

                if (movieAdapter == null) {
                    movieAdapter = new MovieAdapter(this, allMovies, null, null);
                    moviesRecyclerView.setAdapter(movieAdapter);
                }

                // Add the new movie to the list
                if (newMovie != null) {
                    allMovies.add(newMovie);
                    movieAdapter.updateMovies(allMovies);
                }

                Log.d("Movies Action", "List count:" + allMovies.size());

                // Update empty state visibility
                if (allMovies.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    moviesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    moviesRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_ENTERTAINMENT_MOVIES_EDIT) {
                int updatedBookId = data.getIntExtra("UPDATED_MOVIE_ID", -1);
                if (updatedBookId != -1) {
                    movieAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }
        }
    }

    @Override
    public void removeItem(int position) {
        EntertainmentWithMovies entertainmentWithMovies = movies.get(position);
        Entertainment entertainment = entertainmentWithMovies.entertainment;
        Movie movie = entertainmentWithMovies.movie;
        movieAdapter.removeMovie(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            EntertainmentDatabase.getInstance(this).movieDao().delete(movie);  // Deleting the item from the database
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
        EntertainmentWithMovies entertainmentWithMovies = movies.get(position);
        return entertainmentWithMovies.movie.getStatus().getStatus();
    }

    @Override
    public List<String> getItems() {
        return Arrays.stream(MovieStatus.values()).map(MovieStatus::getStatus).collect(Collectors.toList());
    }

    @Override
    public void updateItem(int position, String value) {
        EntertainmentWithMovies entertainmentWithMovies = movies.get(position);
        Movie movie = entertainmentWithMovies.movie;

        // Perform database update in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            movie.setStatus(MovieStatus.fromStatus(value));
            EntertainmentDatabase.getInstance(this).movieDao().update(movie);

            // Show a Toast on the main thread after the update is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void filterMovies(String query) {
        String lower = query.toLowerCase();

        List<EntertainmentWithMovies> filtered = allMovies.stream()
                .filter(entertainmentWithMovies -> {
                    Entertainment entertainment = entertainmentWithMovies.entertainment;
                    Movie movie = entertainmentWithMovies.movie;

                    boolean matchesName = entertainment.getName().toLowerCase().contains(lower);
                    boolean matchesStatus = movie.getStatus().getStatus().toLowerCase().contains(lower);
                    boolean matchesLanguage = entertainment.getLanguage().getLanguage().toLowerCase().contains(lower);

                    // Match genre
                    boolean matchesGenre = movie.getGenre().stream()
                            .map(MovieGenre::getGenre)  // assuming getGenre() returns the string name like "Fantasy"
                            .anyMatch(genre -> genre.toLowerCase().contains(lower));

                    return matchesName || matchesStatus || matchesLanguage || matchesGenre;
                })
                .collect(Collectors.toList());

        movieAdapter.updateMovies(filtered);

        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        moviesRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}