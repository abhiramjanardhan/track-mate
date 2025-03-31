package com.aj.trackmate.activities.books;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.aj.trackmate.R;
import com.aj.trackmate.database.BookDatabase;
import com.aj.trackmate.models.books.BookNote;
import com.aj.trackmate.models.books.BookNoteStatus;

import java.util.concurrent.Executors;

public class AddNotesActivity extends AppCompatActivity {

    private EditText bookNoteHeadingEditText, bookNoteDescriptionEditText;
    private Spinner bookNoteStatusSpinner;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        // Get the platform name from the Intent
        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        Log.d("Add Book Note", "Book Id: " + bookId);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Note");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        BookDatabase.getInstance(this).bookDao().getBookById(bookId).observe(this, book -> {
            // Initialize the views
            bookNoteHeadingEditText = findViewById(R.id.bookNoteHeadingEditText);
            bookNoteDescriptionEditText = findViewById(R.id.bookNoteDescriptionEditText);
            bookNoteStatusSpinner = findViewById(R.id.bookNoteStatusSpinner);

            saveButton = findViewById(R.id.saveNoteButton);

            // Set up the Book Note Status Spinner (Dropdown)
            ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                    R.array.book_note_statuses, android.R.layout.simple_spinner_item);
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bookNoteStatusSpinner.setAdapter(statusAdapter);

            saveButton.setOnClickListener(v -> {
                String heading = bookNoteHeadingEditText.getText().toString().trim();
                String description = bookNoteDescriptionEditText.getText().toString().trim();
                String status = bookNoteStatusSpinner.getSelectedItem().toString();

                if (heading.isEmpty()) {
                    bookNoteHeadingEditText.setError("Heading is required");
                    bookNoteHeadingEditText.requestFocus();
                    return;
                }

                Log.d("Add Book Note", "Save: " + heading);

                BookNote bookNote = new BookNote();
                bookNote.setBookId(bookId);
                bookNote.setHeading(heading);
                bookNote.setDescription(description);
                bookNote.setStatus(BookNoteStatus.fromStatus(status));

                Executors.newSingleThreadExecutor().execute(() -> {
                    BookDatabase.getInstance(this).bookDao().insertNote(bookNote);
                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("NEW_BOOK", bookNote);  // Add the game as Parcelable
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