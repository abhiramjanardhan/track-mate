package com.aj.trackmate.activities.books;

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
import com.aj.trackmate.database.BookDatabase;
import com.aj.trackmate.models.books.BookNote;
import com.aj.trackmate.models.books.BookNoteStatus;

import java.util.Objects;
import java.util.concurrent.Executors;

public class EditNotesActivity extends AppCompatActivity {

    private int bookId, bookNoteId;
    private String bookNoteHeading;
    private BookNote currentBookNote;
    private boolean isEditMode = false;
    private TextView editBookNoteHeading;
    private EditText bookNoteHeadingEditText, bookNoteDescriptionEditText;
    private Spinner bookNoteStatusSpinner;
    private Button saveButton, editButton, cancelButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Note");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        bookNoteId = getIntent().getIntExtra("BOOK_NOTE_ID", -1);
        bookNoteHeading = getIntent().getStringExtra("BOOK_NOTE_HEADING");

        // Initialize the views
        editBookNoteHeading = findViewById(R.id.editBookNoteHeading);
        bookNoteHeadingEditText = findViewById(R.id.bookNoteHeadingEditText);
        bookNoteDescriptionEditText = findViewById(R.id.bookNoteDescriptionEditText);
        bookNoteStatusSpinner = findViewById(R.id.bookNoteStatusSpinner);

        saveButton = findViewById(R.id.saveBookNoteButton);
        editButton = findViewById(R.id.editBookNoteButton);
        cancelButton = findViewById(R.id.cancelBookNoteButton);

        // Set up the Book Note Status Spinner (Dropdown)
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.book_note_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookNoteStatusSpinner.setAdapter(statusAdapter);

        Log.d("Edit Book Note", "Book Id: " + bookId);
        Log.d("Edit Book Note", "Book Note Id: " + bookNoteId);
        editBookNoteHeading.setText("View Note");

        BookDatabase.getInstance(this).bookDao().getBookNoteById(bookId, bookNoteId).observe(this, bookNote -> {
           currentBookNote = bookNote;

           if (currentBookNote != null) {
               bookNoteHeadingEditText.setText(currentBookNote.getHeading());
               bookNoteDescriptionEditText.setText(currentBookNote.getDescription());

               ArrayAdapter bookNoteStatusSpinnerAdapter = (ArrayAdapter) bookNoteStatusSpinner.getAdapter();
               int position = bookNoteStatusSpinnerAdapter.getPosition(currentBookNote.getStatus().getStatus());
               bookNoteStatusSpinner.setSelection(position);
           }
        });

        // Set initial state (readonly)
        setEditMode(false);

        // Handle Edit button click
        editButton.setOnClickListener(v -> {
            isEditMode = true;
            editBookNoteHeading.setText("Edit Note");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Note");  // Change the title dynamically
            setEditMode(true);
        });

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            saveNoteDetails();
            finish(); // Go back to listing page
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            resetToOriginalState();
            isEditMode = false;
            editBookNoteHeading.setText("View Note");
            Objects.requireNonNull(getSupportActionBar()).setTitle("View Note");  // Change the title dynamically
            setEditMode(false);
        });
    }

    private void setEditMode(boolean enabled) {
        bookNoteHeadingEditText.setEnabled(enabled);
        bookNoteDescriptionEditText.setEnabled(enabled);
        bookNoteStatusSpinner.setEnabled(enabled);

        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void saveNoteDetails() {
        if (currentBookNote != null) {
            String heading = bookNoteHeadingEditText.getText().toString().trim();
            String description = bookNoteDescriptionEditText.getText().toString().trim();
            String status = bookNoteStatusSpinner.getSelectedItem().toString();

            currentBookNote.setHeading(heading);
            currentBookNote.setDescription(description);
            currentBookNote.setStatus(BookNoteStatus.fromStatus(status));

            Executors.newSingleThreadExecutor().execute(() -> {
                BookDatabase.getInstance(this).bookDao().updateNote(currentBookNote);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Book Note updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_BOOK_ID", currentBookNote.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the activity and go back to the listing page
                });
            });
        }
    }

    private void resetToOriginalState() {
        bookNoteHeadingEditText.setText(bookNoteHeading);
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