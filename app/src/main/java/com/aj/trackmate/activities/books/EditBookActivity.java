package com.aj.trackmate.activities.books;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.books.BookNotesAdapter;
import com.aj.trackmate.database.BookDatabase;
import com.aj.trackmate.models.books.*;
import com.aj.trackmate.models.books.relations.BookWithNotes;
import com.aj.trackmate.operations.LongPressCallBack;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.aj.trackmate.operations.templates.ItemUpdateListener;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.aj.trackmate.constants.RequestCodeConstants.*;

public class EditBookActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener, ItemUpdateListener {

    private int bookId;
    private String bookName;
    private BookStatus bookStatus;
    private BookWithNotes currentBook;
    private boolean isEditMode = false;
    private List<BookNote> bookNotes;
    private BookNotesAdapter bookNotesAdapter;

    private EditText bookNameEditText, bookAuthorNameEditText, bookPublicationNameEditText;
    private Spinner bookStatusSpinner;
    private TextView editBookHeading, bookGenreMultiSelect, bookNotesEmptyStateMessage;
    private RadioButton favoriteYes, favoriteNo;
    private RadioButton purchasedYes, purchasedNo;
    private RadioButton wishlistYes, wishlistNo;
    private RadioButton startedYes, startedNo;
    private RadioButton completedYes, completedNo;
    private RadioButton backlogYes, backlogNo;
    private RadioGroup favoriteBookGroup, purchasedBookGroup, wishlistBookGroup, startedBookGroup, completedBookGroup, backlogBookGroup;
    private Button saveButton, cancelButton, editButton, addBookNoteButton;
    private RecyclerView recyclerViewBookNotes;

    private boolean[] selectedGenres;
    private List<BookGenre> selectedGenreList = new ArrayList<>();
    private String[] genreArray;

    private EditText searchEditText;
    private List<BookNote> allNotes;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ids from the Intent
        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookStatus = BookStatus.fromStatus(getIntent().getStringExtra("BOOK_STATUS"));
        Log.d("Edit Book", "Book: " + bookName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Book");  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Initialize Views
        bookNameEditText = findViewById(R.id.bookNameEditText);
        bookAuthorNameEditText = findViewById(R.id.bookAuthorNameEditText);
        bookPublicationNameEditText = findViewById(R.id.bookPublicationNameEditText);
        bookStatusSpinner = findViewById(R.id.bookStatusSpinner);

        bookGenreMultiSelect = findViewById(R.id.bookGenreMultiSelect);
        editBookHeading = findViewById(R.id.editBookHeading);

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
        editButton = findViewById(R.id.editBookButton);
        cancelButton = findViewById(R.id.cancelBookButton);

        favoriteBookGroup = findViewById(R.id.favoriteBookGroup);
        purchasedBookGroup = findViewById(R.id.purchasedBookGroup);
        wishlistBookGroup = findViewById(R.id.wishlistBookGroup);
        startedBookGroup = findViewById(R.id.startedBookGroup);
        completedBookGroup = findViewById(R.id.completedBookGroup);
        backlogBookGroup = findViewById(R.id.backlogBookGroup);

        addBookNoteButton = findViewById(R.id.addBookNotesButton);
        bookNotesEmptyStateMessage = findViewById(R.id.bookNotesEmptyStateMessage);
        recyclerViewBookNotes = findViewById(R.id.recyclerViewBookNotes);
        recyclerViewBookNotes.setLayoutManager(new LinearLayoutManager(this));

        searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Convert Enum to String Array
        BookGenre[] genres = BookGenre.values();
        genreArray = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreArray[i] = genres[i].getGenre();
        }

        selectedGenres = new boolean[genreArray.length];
        editBookHeading.setText("View Book");

        // Set up the Movie Status Spinner (Dropdown)
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.book_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookStatusSpinner.setAdapter(statusAdapter);

        // Set click listener to open dialog
        bookGenreMultiSelect.setOnClickListener(v -> showGenreDialog());

        addBookNoteButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new game dlc
            Intent intent = new Intent(this, AddNotesActivity.class);
            intent.putExtra("BOOK_ID", bookId);
            startActivityForResult(intent, REQUEST_CODE_BOOK_NOTES_ADD); // Request code to identify the result
        });

        BookDatabase.getInstance(this).bookDao().getBooksWithNotesById(bookId).observe(this, bookWithNotes -> {
            currentBook = bookWithNotes;

            if (currentBook != null) {
                Book book = currentBook.book;
                allNotes = currentBook.notes;
                bookNotes = new ArrayList<>(allNotes);

                Log.d("Edit Book", "Book Notes Size: " + bookNotes.size());

                if (bookNotes.isEmpty()) {
                    bookNotesEmptyStateMessage.setVisibility(View.VISIBLE);
                    recyclerViewBookNotes.setVisibility(View.GONE);
                } else {
                    bookNotesEmptyStateMessage.setVisibility(View.GONE);
                    recyclerViewBookNotes.setVisibility(View.VISIBLE);
                }

                bookNotesAdapter = new BookNotesAdapter(this, bookNotes, bookNote -> {
                    Intent intent = new Intent(EditBookActivity.this, EditNotesActivity.class);
                    intent.putExtra("BOOK_ID", bookId);
                    intent.putExtra("BOOK_NOTE_ID", bookNote.getId());
                    intent.putExtra("BOOK_NOTE_HEADING", bookNote.getHeading());
                    startActivityForResult(intent, REQUEST_CODE_BOOK_NOTES_EDIT);
                }, (view, position) -> {
                    LongPressCallBack longPressCallBack = new LongPressCallBack(bookNotesAdapter, this, this, this);
                    longPressCallBack.handleLongPress(view, position, "Book Note");
                });

                recyclerViewBookNotes.setAdapter(bookNotesAdapter);
                bookNotesAdapter.updateBookNotes(bookNotes);  // Notify adapter of new data

                bookNameEditText.setText(bookName);
                bookAuthorNameEditText.setText(book.getAuthor());
                bookPublicationNameEditText.setText(book.getPublication());

                ArrayAdapter bookStatusSpinnerAdapter = (ArrayAdapter) bookStatusSpinner.getAdapter();
                int statusPosition = bookStatusSpinnerAdapter.getPosition(book.getStatus().getStatus());
                bookStatusSpinner.setSelection(statusPosition);

                // Retrieve the selected genres from the movie object
                selectedGenreList = new ArrayList<>(book.getGenre());
                // Reset the selectedGenres array
                selectedGenres = new boolean[genreArray.length];

                // Update selectedGenres based on the stored values
                for (BookGenre genre : selectedGenreList) {
                    for (int i = 0; i < genreArray.length; i++) {
                        if (genreArray[i].equals(genre.getGenre())) {
                            selectedGenres[i] = true;
                            break;
                        }
                    }
                }

                // Update the UI to reflect the selected genres
                updateGenreText();

                if (book.isFavorite()) {
                    favoriteYes.setChecked(true);
                } else {
                    favoriteNo.setChecked(true);
                }

                if (book.isPurchased()) {
                    purchasedYes.setChecked(true);
                } else {
                    purchasedNo.setChecked(true);
                }

                if (book.isWishlist()) {
                    wishlistYes.setChecked(true);
                } else {
                    wishlistNo.setChecked(true);
                }

                if (book.isStarted()) {
                    startedYes.setChecked(true);
                } else {
                    startedNo.setChecked(true);
                }

                if (book.isCompleted()) {
                    completedYes.setChecked(true);
                } else {
                    completedNo.setChecked(true);
                }

                if (book.isBacklog()) {
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
            editBookHeading.setText("Edit Book");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Book");  // Change the title dynamically
            setEditMode(true);
        });

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            saveBookDetails();
            finish(); // Go back to listing page
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            resetToOriginalState();
            isEditMode = false;
            editBookHeading.setText("View Book");
            Objects.requireNonNull(getSupportActionBar()).setTitle("View Book");  // Change the title dynamically
            setEditMode(false);
        });
    }

    private void setEditMode(boolean enabled) {
        bookNameEditText.setEnabled(enabled);
        bookAuthorNameEditText.setEnabled(enabled);
        bookPublicationNameEditText.setEnabled(enabled);
        bookStatusSpinner.setEnabled(enabled);
        bookGenreMultiSelect.setEnabled(enabled);

        favoriteBookGroup.setEnabled(enabled);
        favoriteYes.setEnabled(enabled);
        favoriteNo.setEnabled(enabled);

        wishlistBookGroup.setEnabled(enabled);
        wishlistYes.setEnabled(enabled);
        wishlistNo.setEnabled(enabled);

        startedBookGroup.setEnabled(enabled);
        startedYes.setEnabled(enabled);
        startedNo.setEnabled(enabled);

        completedBookGroup.setEnabled(enabled);
        completedYes.setEnabled(enabled);
        completedNo.setEnabled(enabled);

        backlogBookGroup.setEnabled(enabled);
        backlogYes.setEnabled(enabled);
        backlogNo.setEnabled(enabled);

        purchasedBookGroup.setEnabled(enabled);
        purchasedYes.setEnabled(enabled);
        purchasedNo.setEnabled(enabled);

        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void saveBookDetails() {
        if (currentBook != null) {
            // Read values from input fields
            String bookName = bookNameEditText.getText().toString().trim();
            String bookAuthorName = bookAuthorNameEditText.getText().toString().trim();
            String bookPublicationName = bookPublicationNameEditText.getText().toString().trim();
            String bookStatus = bookStatusSpinner.getSelectedItem().toString();

            boolean isFavorite = favoriteYes.isChecked();
            boolean isWishlist = wishlistYes.isChecked();
            boolean isStarted = startedYes.isChecked();
            boolean isCompleted = completedYes.isChecked();
            boolean isBacklog = backlogYes.isChecked();
            boolean isPurchased = purchasedYes.isChecked();

            currentBook.book.setName(bookName);
            currentBook.book.setAuthor(bookAuthorName);
            currentBook.book.setPublication(bookPublicationName);
            currentBook.book.setStatus(BookStatus.fromStatus(bookStatus));
            currentBook.book.setGenre(selectedGenreList);
            currentBook.book.setFavorite(isFavorite);
            currentBook.book.setWishlist(isWishlist);
            currentBook.book.setStarted(isStarted);
            currentBook.book.setCompleted(isCompleted);
            currentBook.book.setBacklog(isBacklog);
            currentBook.book.setPurchased(isPurchased);

            Executors.newSingleThreadExecutor().execute(() -> {
                BookDatabase.getInstance(this).bookDao().update(currentBook.book);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Game updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_BOOK_ID", currentBook.book.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the activity and go back to the listing page
                });
            });
        }
    }

    private void resetToOriginalState() {
        // Reset fields to original values (if stored)
        bookNameEditText.setText(bookName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Retrieve the new game from the result
            BookNote newBookNote = null;
            if (requestCode == REQUEST_CODE_BOOK_NOTES_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newBookNote = data.getParcelableExtra("NEW_BOOK", BookNote.class);
                Log.d("Book Note Action", "Save:" + newBookNote);

                if (bookNotesAdapter == null) {
                    bookNotesAdapter = new BookNotesAdapter(this, allNotes, null, null);
                    recyclerViewBookNotes.setAdapter(bookNotesAdapter);
                }

                // Add the new game to the list
                if (newBookNote != null) {
                    allNotes.add(newBookNote);
                    bookNotesAdapter.updateBookNotes(allNotes);
                    searchEditText.setText("");
                }

                Log.d("Book Note Action", "List count:" + allNotes.size());

                // Update empty state visibility
                if (allNotes.isEmpty()) {
                    bookNotesEmptyStateMessage.setVisibility(View.VISIBLE);
                    recyclerViewBookNotes.setVisibility(View.GONE);
                } else {
                    bookNotesEmptyStateMessage.setVisibility(View.GONE);
                    recyclerViewBookNotes.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_BOOK_NOTES_EDIT) {
                int updatedGameId = data.getIntExtra("UPDATED_BOOK_ID", -1);
                if (updatedGameId != -1) {
                    bookNotesAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }
        }
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

    @Override
    public void removeItem(int position) {
        BookNote bookNote = bookNotes.get(position);
        bookNotesAdapter.removeBookNote(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            BookDatabase.getInstance(this).bookDao().deleteNote(bookNote); // First delete Book Notes

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
        BookNote bookNote = bookNotes.get(position);
        return bookNote.getStatus().getStatus();
    }

    @Override
    public List<String> getItems() {
        return Arrays.stream(BookNoteStatus.values()).map(BookNoteStatus::getStatus).collect(Collectors.toList());
    }

    @Override
    public void updateItem(int position, String value) {
        BookNote bookNote = bookNotes.get(position);

        // Perform database update in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            bookNote.setStatus(BookNoteStatus.fromStatus(value));
            BookDatabase.getInstance(this).bookDao().updateNote(bookNote);

            // Show a Toast on the main thread after the update is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void filterBooks(String query) {
        String lower = query.toLowerCase();

        List<BookNote> filtered = allNotes.stream()
                .filter(bookWithNote -> {
                    boolean matchesName = bookWithNote.getHeading().toLowerCase().contains(lower);
                    boolean matchesStatus = bookWithNote.getStatus().getStatus().toLowerCase().contains(lower);
                    boolean matchesDescription = bookWithNote.getDescription().toLowerCase().contains(lower);

                    return matchesName || matchesStatus || matchesDescription;
                })
                .collect(Collectors.toList());

        bookNotesAdapter.updateBookNotes(filtered);

        bookNotesEmptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerViewBookNotes.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}