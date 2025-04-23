package com.aj.trackmate.activities.books;

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
import com.aj.trackmate.database.BookDatabase;
import com.aj.trackmate.models.books.Book;
import com.aj.trackmate.models.books.BookFor;
import com.aj.trackmate.models.books.BookGenre;
import com.aj.trackmate.models.books.BookStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddBookActivity extends AppCompatActivity {

    private EditText bookNameEditText, bookAuthorNameEditText, bookPublicationNameEditText;
    private Spinner bookStatusSpinner;
    private TextView bookGenreMultiSelect;
    private RadioButton favoriteYes, favoriteNo;
    private RadioButton purchasedYes, purchasedNo;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private Button saveButton;

    private boolean[] selectedGenres;
    private List<BookGenre> selectedGenreList = new ArrayList<>();
    private String[] genreArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Get the platform name from the Intent
        String platform = getIntent().getStringExtra("CATEGORY");
        Log.d("Add Book", "Platform: " + platform);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(platform);  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        bookNameEditText = findViewById(R.id.bookNameEditText);
        bookAuthorNameEditText = findViewById(R.id.bookAuthorNameEditText);
        bookPublicationNameEditText = findViewById(R.id.bookPublicationNameEditText);
        bookStatusSpinner = findViewById(R.id.bookStatusSpinner);

        bookGenreMultiSelect = findViewById(R.id.bookGenreMultiSelect);

        favoriteYes = findViewById(R.id.favoriteYes);
        favoriteNo = findViewById(R.id.favoriteNo);

        purchasedYes = findViewById(R.id.purchasedYes);
        purchasedNo = findViewById(R.id.purchasedNo);

        wishlistYes = findViewById(R.id.wishlistYes);
        wishlistNo = findViewById(R.id.wishlistNo);

        startedYes = findViewById(R.id.startedYes);
        startedNo = findViewById(R.id.startedNo);

        completedYes = findViewById(R.id.completedYes);
        completedNo = findViewById(R.id.completedNo);

        backlogYes = findViewById(R.id.backlogYes);
        backlogNo = findViewById(R.id.backlogNo);

        saveButton = findViewById(R.id.saveBookButton);

        favoriteNo.setChecked(true);
        purchasedNo.setChecked(true);
        wishlistNo.setChecked(true);
        startedNo.setChecked(true);
        completedNo.setChecked(true);
        backlogNo.setChecked(true);

        // Convert Enum to String Array
        BookGenre[] genres = BookGenre.values();
        genreArray = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreArray[i] = genres[i].getGenre();
        }

        selectedGenres = new boolean[genreArray.length];

        // Set up the Movie Status Spinner (Dropdown)
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.book_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookStatusSpinner.setAdapter(statusAdapter);

        // Set click listener to open dialog
        bookGenreMultiSelect.setOnClickListener(v -> showGenreDialog());

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Read values from input fields
            String bookName = bookNameEditText.getText().toString().trim();
            String bookAuthorName = bookAuthorNameEditText.getText().toString().trim();
            String bookPublicationName = bookPublicationNameEditText.getText().toString().trim();
            String bookStatus = bookStatusSpinner.getSelectedItem().toString();

            if (bookName.isEmpty()) {
                bookNameEditText.setError("Book Name is required");
                bookNameEditText.requestFocus();
                return;
            }

            boolean isFavorite = favoriteYes.isChecked();
            boolean isWishlist = wishlistYes.isChecked();
            boolean isStarted = startedYes.isChecked();
            boolean isCompleted = completedYes.isChecked();
            boolean isBacklog = backlogYes.isChecked();
            boolean isPurchased = purchasedYes.isChecked();

            Log.d("Add Book", "Save: " + platform);

            Book book = new Book();
            book.setName(bookName);
            book.setBookFor(BookFor.fromGenre(platform));
            book.setAuthor(bookAuthorName);
            book.setPublication(bookPublicationName);
            book.setStatus(BookStatus.fromStatus(bookStatus));
            book.setGenre(selectedGenreList);
            book.setFavorite(isFavorite);
            book.setWishlist(isWishlist);
            book.setStarted(isStarted);
            book.setCompleted(isCompleted);
            book.setBacklog(isBacklog);
            book.setPurchased(isPurchased);

            Executors.newSingleThreadExecutor().execute(() -> {
                BookDatabase.getInstance(this).bookDao().insert(book);
                runOnUiThread(() -> {
                    BookDatabase.getInstance(this).bookDao().getBooksWithNotesById(book.getId()).observe(this, bookWithNotes -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("NEW_BOOK", bookWithNotes);  // Add the game as Parcelable
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
                selectedGenreList.add(BookGenre.values()[which]);
            } else {
                selectedGenreList.remove(BookGenre.values()[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> updateGenreText());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void updateGenreText() {
        if (selectedGenreList.isEmpty()) {
            bookGenreMultiSelect.setText(R.string.select_genre);
        } else {
            StringBuilder genresText = new StringBuilder();
            for (BookGenre genre : selectedGenreList) {
                genresText.append(genre.getGenre()).append(", ");
            }
            bookGenreMultiSelect.setText(genresText.substring(0, genresText.length() - 2));
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