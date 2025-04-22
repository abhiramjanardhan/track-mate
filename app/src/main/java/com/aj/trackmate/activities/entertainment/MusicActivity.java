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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.adapters.entertainment.MusicAdapter;
import com.aj.trackmate.database.EntertainmentDatabase;
import com.aj.trackmate.managers.filter.FilterBarManager;
import com.aj.trackmate.managers.filter.FilterBottomSheetDialog;
import com.aj.trackmate.models.entertainment.*;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMusic;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.aj.trackmate.R;
import com.aj.trackmate.operations.SwipeToDeleteCallback;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_MUSIC_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_ENTERTAINMENT_MUSIC_EDIT;

public class MusicActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener {

    private ListView listView;
    private RecyclerView musicRecyclerView;
    private MusicAdapter musicAdapter;
    private List<EntertainmentWithMusic> musics;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

    private EditText searchEditText;
    private List<EntertainmentWithMusic> allMusics;
    private Map<String, String> selectedFilters = new HashMap<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        musicRecyclerView = findViewById(R.id.recyclerViewMusic);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.musicFloatingButton);
        emptyStateMessage = findViewById(R.id.musicEmptyStateMessage);
        title = findViewById(R.id.musicTitle);

        searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMusics(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Get the platform name from the Intent
        String category = getIntent().getStringExtra("CATEGORY");
        String platform = getIntent().getStringExtra("PLATFORM");
        title.setText(category + " List");

        findViewById(R.id.advancedMusicFilters).setOnClickListener(v -> {
            FilterBottomSheetDialog bottomSheet = new FilterBottomSheetDialog(platform, selectedFilters, new FilterBottomSheetDialog.FilterListener() {
                @Override
                public void onApplyFilters(Map<String, String> filters) {
                    selectedFilters = filters;
                    applyFilters(filters); // Your existing method
                }

                @Override
                public void onClearFilters() {
                    musicAdapter.updateMusics(allMusics); // Reset
                    emptyStateMessage.setVisibility(allMusics.isEmpty() ? View.VISIBLE : View.GONE);
                    musicRecyclerView.setVisibility(allMusics.isEmpty() ? View.GONE : View.VISIBLE);
                }
            });

            bottomSheet.show(getSupportFragmentManager(), "MusicsFilterBottomSheet");
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title.getText());  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Handle the "Add" button click
        addButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new music
            Intent intent = new Intent(this, AddMusicActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_MUSIC_ADD); // Request code to identify the result
        });

        if (category != null) {
            EntertainmentDatabase.getInstance(this).entertainmentDao().getAllEntertainmentForMusic(EntertainmentCategory.MUSIC, category).observe(this, musicList -> {
                allMusics = musicList;
                musics = new ArrayList<>(allMusics);
                Log.d("Music", "List: " + musics);

                if (musics == null || musics.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    musicRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    musicRecyclerView.setVisibility(View.VISIBLE);

                    musicAdapter = new MusicAdapter(this, musics, entertainmentWithMusic -> {
                        Intent intent = new Intent(MusicActivity.this, EditMusicActivity.class);
                        intent.putExtra("ENTERTAINMENT_ID", entertainmentWithMusic.entertainment.getId());
                        intent.putExtra("MUSIC_ID", entertainmentWithMusic.music.getId());
                        intent.putExtra("MUSIC_NAME", entertainmentWithMusic.entertainment.getName());
                        intent.putExtra("MUSIC_LANGUAGE", entertainmentWithMusic.entertainment.getLanguage().getLanguage());
                        startActivityForResult(intent, REQUEST_CODE_ENTERTAINMENT_MUSIC_EDIT);
                    });
                    musicRecyclerView.setAdapter(musicAdapter);
                    musicAdapter.updateMusics(musics);  // Notify adapter of new data
                }

                // Setup the swipe-to-delete functionality
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(musicAdapter, this, this));
                itemTouchHelper.attachToRecyclerView(musicRecyclerView);
            });
        } else {
            musics = new ArrayList<>();
            emptyStateMessage.setVisibility(View.VISIBLE);
            musicRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Retrieve the new music from the result
            EntertainmentWithMusic newMusic = null;
            if (requestCode == REQUEST_CODE_ENTERTAINMENT_MUSIC_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newMusic = data.getParcelableExtra("NEW_MUSIC", EntertainmentWithMusic.class);
                Log.d("Music Action", "Save:" + newMusic);

                if (musicAdapter == null) {
                    musicAdapter = new MusicAdapter(this, allMusics, null);
                    musicRecyclerView.setAdapter(musicAdapter);
                }

                // Add the new music to the list
                if (newMusic != null) {
                    allMusics.add(newMusic);
                    musicAdapter.updateMusics(allMusics);  // Notify the adapter to refresh the RecyclerView
                    searchEditText.setText("");
                }

                Log.d("Music Action", "List count:" + allMusics.size());

                // Update empty state visibility
                if (allMusics.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    musicRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    musicRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_ENTERTAINMENT_MUSIC_EDIT) {
                int updatedMusicId = data.getIntExtra("UPDATED_MUSIC_ID", -1);
                if (updatedMusicId != -1) {
                    musicAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }
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
    public void removeItem(int position) {
        EntertainmentWithMusic entertainmentWithMusic = musics.get(position);
        Entertainment entertainment = entertainmentWithMusic.entertainment;
        Music music = entertainmentWithMusic.music;
        musicAdapter.removeMusic(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            EntertainmentDatabase.getInstance(this).musicDao().delete(music);  // Deleting the item from the database
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

    private void filterMusics(String query) {
        String lower = query.toLowerCase();

        List<EntertainmentWithMusic> filtered = allMusics.stream()
                .filter(entertainmentWithMusic -> {
                    Entertainment entertainment = entertainmentWithMusic.entertainment;
                    Music music = entertainmentWithMusic.music;

                    boolean matchesName = entertainment.getName().toLowerCase().contains(lower);
                    boolean matchesAlbum = music.getAlbum().toLowerCase().contains(lower);
                    boolean matchesArtist = music.getAlbum().toLowerCase().contains(lower);
                    boolean matchesLanguage = entertainment.getLanguage().getLanguage().toLowerCase().contains(lower);

                    return matchesName || matchesLanguage || matchesAlbum || matchesArtist;
                })
                .collect(Collectors.toList());

        musicAdapter.updateMusics(filtered);

        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        musicRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void applyFilters(Map<String, String> filters) {
        List<EntertainmentWithMusic> filtered = allMusics.stream().filter(entertainmentWithMusic -> {
            Entertainment e = entertainmentWithMusic.entertainment;
            Music m = entertainmentWithMusic.music;

            boolean language = Objects.equals(filters.get(FilterBarManager.FILTER_LANGUAGE), "All") || e.getLanguage().getLanguage().equalsIgnoreCase(filters.get(FilterBarManager.FILTER_LANGUAGE));

            return language;
        }).collect(Collectors.toList());

        // Sorting Logic
        String sortBy = filters.get(FilterBarManager.FILTER_SORTING);
        if (sortBy != null) {
            switch (sortBy) {
                case "Name":
                    filtered.sort((a, b) -> a.entertainment.getName().compareToIgnoreCase(b.entertainment.getName()));
                    break;
                case "Language":
                    filtered.sort((a, b) -> a.entertainment.getLanguage().getLanguage().compareToIgnoreCase(b.entertainment.getLanguage().getLanguage()));
                    break;
                default:
                    musicAdapter.sortMusics();
                    break;
            }
        } else {
            musicAdapter.sortMusics();
        }

        musicAdapter.updateMusics(filtered);
        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        musicRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}