package com.aj.trackmate.activities.entertainment;

import android.annotation.SuppressLint;
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
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMusic;

import java.util.Objects;
import java.util.concurrent.Executors;

public class EditMusicActivity extends AppCompatActivity {

    private int entertainmentId, musicId;
    private String musicName;
    private Language language;
    private EntertainmentWithMusic entertainmentWithMusic;
    private boolean isEditMode = false;

    private TextView editMusicHeading;
    private EditText musicNameEditText, musicArtistNameEditText, musicAlbumNameEditText;
    private Spinner musicLanguageSpinner;
    private Button saveButton, cancelButton, editButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_music);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ids from the Intent
        entertainmentId = getIntent().getIntExtra("ENTERTAINMENT_ID", -1);
        musicId = getIntent().getIntExtra("MUSIC_ID", -1);
        musicName = getIntent().getStringExtra("MUSIC_NAME");
        language = Language.fromLanguage(getIntent().getStringExtra("MUSIC_LANGUAGE"));
        Log.d("Edit Music", "Music: " + musicName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Music");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        musicNameEditText = findViewById(R.id.musicNameEditText);
        musicArtistNameEditText = findViewById(R.id.musicArtistNameEditText);
        musicAlbumNameEditText = findViewById(R.id.musicAlbumNameEditText);
        editMusicHeading = findViewById(R.id.editMusicHeading);
        musicLanguageSpinner = findViewById(R.id.musicLanguageSpinner);

        editButton = findViewById(R.id.editMusicButton);
        saveButton = findViewById(R.id.saveMusicButton);
        cancelButton = findViewById(R.id.cancelMusicButton);

        // Set up the Music Language Spinner (Dropdown)
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.music_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musicLanguageSpinner.setAdapter(languageAdapter);

        editMusicHeading.setText("View Music");

        EntertainmentDatabase.getInstance(this).musicDao().getMusicEntertainmentByMusicId(entertainmentId, musicId).observe(this, music -> {
            entertainmentWithMusic = music;

            if (entertainmentWithMusic != null) {
                musicNameEditText.setText(musicName);
                musicArtistNameEditText.setText(entertainmentWithMusic.music.getArtist());
                musicAlbumNameEditText.setText(entertainmentWithMusic.music.getAlbum());

                ArrayAdapter musicLanguageSpinnerAdapter = (ArrayAdapter) musicLanguageSpinner.getAdapter();
                int languagePosition = musicLanguageSpinnerAdapter.getPosition(entertainmentWithMusic.entertainment.getLanguage().getLanguage());
                musicLanguageSpinner.setSelection(languagePosition);
            }
        });

        // Set initial state (readonly)
        setEditMode(false);

        // Handle Edit button click
        editButton.setOnClickListener(v -> {
            isEditMode = true;
            editMusicHeading.setText("Edit Music");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Music");  // Change the title dynamically
            setEditMode(true);
        });

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            saveMusicDetails();
            finish(); // Go back to listing page
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            resetToOriginalState();
            isEditMode = false;
            editMusicHeading.setText("View Music");
            Objects.requireNonNull(getSupportActionBar()).setTitle("View Music");  // Change the title dynamically
            setEditMode(false);
        });
    }

    private void setEditMode(boolean enabled) {
        musicNameEditText.setEnabled(enabled);
        musicLanguageSpinner.setEnabled(enabled);
        musicArtistNameEditText.setEnabled(enabled);
        musicAlbumNameEditText.setEnabled(enabled);

        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void saveMusicDetails() {
        if (entertainmentWithMusic != null) {
            // Read values from input fields
            String musicName = musicNameEditText.getText().toString().trim();
            String musicArtistName = musicArtistNameEditText.getText().toString().trim();
            String musicAlbumName = musicAlbumNameEditText.getText().toString().trim();
            String musicLanguage = musicLanguageSpinner.getSelectedItem().toString();

            entertainmentWithMusic.entertainment.setName(musicName);
            entertainmentWithMusic.entertainment.setLanguage(Language.fromLanguage(musicLanguage));

            Executors.newSingleThreadExecutor().execute(() -> {
                EntertainmentDatabase.getInstance(this).entertainmentDao().update(entertainmentWithMusic.entertainment);

                entertainmentWithMusic.music.setArtist(musicArtistName);
                entertainmentWithMusic.music.setAlbum(musicAlbumName);

                EntertainmentDatabase.getInstance(this).musicDao().update(entertainmentWithMusic.music);

                Log.d("Edit Music", "Platform: " + entertainmentWithMusic.music.getPlatform());

                runOnUiThread(() -> {
                    Toast.makeText(this, "Music updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_MUSIC_ID", entertainmentWithMusic.entertainment.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the activity and go back to the listing page
                });
            });
        }
    }

    private void resetToOriginalState() {
        // Reset fields to original values (if stored)
        musicNameEditText.setText(musicName);
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