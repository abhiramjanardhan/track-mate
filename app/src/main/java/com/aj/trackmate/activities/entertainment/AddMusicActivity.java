package com.aj.trackmate.activities.entertainment;

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

import java.util.concurrent.Executors;

public class AddMusicActivity extends AppCompatActivity {

    private EditText musicNameEditText, musicArtistNameEditText, musicAlbumNameEditText;
    private Spinner musicLanguageSpinner;
    private RadioButton favoriteYes, favoriteNo;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the platform name from the Intent
        String platform = getIntent().getStringExtra("CATEGORY");
        Log.d("Add Music", "Platform: " + platform);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Music");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        musicNameEditText = findViewById(R.id.musicNameEditText);
        musicArtistNameEditText = findViewById(R.id.musicArtistNameEditText);
        musicAlbumNameEditText = findViewById(R.id.musicAlbumNameEditText);
        musicLanguageSpinner = findViewById(R.id.musicLanguageSpinner);

        favoriteYes = findViewById(R.id.favoriteYes);
        favoriteNo = findViewById(R.id.favoriteNo);

        saveButton = findViewById(R.id.saveMusicButton);

        // Set up the Music Language Spinner (Dropdown)
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.music_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musicLanguageSpinner.setAdapter(languageAdapter);

        favoriteNo.setChecked(true);

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Read values from input fields
            String musicName = musicNameEditText.getText().toString().trim();
            String musicArtistName = musicArtistNameEditText.getText().toString().trim();
            String musicAlbumName = musicAlbumNameEditText.getText().toString().trim();
            String musicLanguage = musicLanguageSpinner.getSelectedItem().toString();

            boolean isFavorite = favoriteYes.isChecked();

            if (musicName.isEmpty()) {
                musicNameEditText.setError("Music Name is required");
                musicNameEditText.requestFocus();
                return;
            }

            Entertainment entertainment = new Entertainment();
            Music music = new Music();
            entertainment.setName(musicName);
            entertainment.setLanguage(Language.fromLanguage(musicLanguage));
            entertainment.setCategory(EntertainmentCategory.MUSIC);

            Executors.newSingleThreadExecutor().execute(() -> {
                int entertainmentId = (int) EntertainmentDatabase.getInstance(this).entertainmentDao().insert(entertainment);

                music.setEntertainmentId(entertainmentId);
                music.setPlatform(platform);
                music.setArtist(musicArtistName);
                music.setAlbum(musicAlbumName);
                music.setFavorite(isFavorite);

                Log.d("Add Music", "Platform: " + music.getPlatform());

                EntertainmentDatabase.getInstance(this).musicDao().insert(music);

                // Switch to the main thread to observe LiveData
                runOnUiThread(() -> {
                    EntertainmentDatabase.getInstance(this).entertainmentDao()
                            .getMusicEntertainmentByEntertainmentId(entertainmentId)
                            .observe(this, entertainmentWithMusic -> {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("NEW_MUSIC", entertainmentWithMusic);  // Add the music as Parcelable
                                setResult(RESULT_OK, resultIntent);
                                finish();  // Close the activity
                            });
                });
            });
        });
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