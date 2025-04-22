package com.aj.trackmate.activities.books;

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
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.books.BookAdapter;
import com.aj.trackmate.database.BookDatabase;
import com.aj.trackmate.managers.filter.FilterBarManager;
import com.aj.trackmate.managers.filter.FilterBottomSheetDialog;
import com.aj.trackmate.models.books.*;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.aj.trackmate.models.books.relations.BookWithNotes;
import com.aj.trackmate.operations.LongPressCallBack;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.aj.trackmate.operations.templates.ItemUpdateListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_BOOKS_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_BOOKS_EDIT;

public class BooksActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener, ItemUpdateListener {

    private ListView listView;
    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private List<BookWithNotes> books;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

    private EditText searchEditText;
    private List<BookWithNotes> allBooks;
    private Map<String, String> selectedFilters = new HashMap<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        // Remove default action bar to prevent duplicate toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        booksRecyclerView = findViewById(R.id.recyclerViewBooks);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.booksFloatingButton);
        title = findViewById(R.id.booksTitle);
        emptyStateMessage = findViewById(R.id.booksEmptyStateMessage);

        searchEditText = findViewById(R.id.searchEditText);

        // Get the platform name from the Intent
        String bookFor = getIntent().getStringExtra("CATEGORY");

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

        findViewById(R.id.advancedBooksFilters).setOnClickListener(v -> {
            FilterBottomSheetDialog bottomSheet = new FilterBottomSheetDialog(bookFor, selectedFilters, new FilterBottomSheetDialog.FilterListener() {
                @Override
                public void onApplyFilters(Map<String, String> filters) {
                    selectedFilters = filters;
                    applyFilters(filters); // Your existing method
                }

                @Override
                public void onClearFilters() {
                    bookAdapter.updateBooks(allBooks); // Reset
                    bookAdapter.sortBooks();
                    emptyStateMessage.setVisibility(allBooks.isEmpty() ? View.VISIBLE : View.GONE);
                    booksRecyclerView.setVisibility(allBooks.isEmpty() ? View.GONE : View.VISIBLE);
                }
            });

            bottomSheet.show(getSupportFragmentManager(), "BooksFilterBottomSheet");
        });

        title.setText("Books List");
        emptyStateMessage.setText("No Books Available");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(bookFor);  // Change the title dynamically
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button if needed
        }

        // Handle the "Add" button click
        addButton.setOnClickListener(v -> {
            // Launch a new activity or dialog to add a new book
            Intent intent = new Intent(this, AddBookActivity.class);
            intent.putExtra("CATEGORY", bookFor);
            startActivityForResult(intent, REQUEST_CODE_BOOKS_ADD); // Request code to identify the result
        });

        if (bookFor != null) {
            BookDatabase.getInstance(this).bookDao().getBooksWithNotesByFor(BookFor.fromGenre(bookFor)).observe(this, booksList -> {
                allBooks = booksList;
                books = new ArrayList<>(allBooks);
                Log.d("Books", "List: " + books.size());

                if (books == null || books.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    booksRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    booksRecyclerView.setVisibility(View.VISIBLE);

                    bookAdapter = new BookAdapter(this, books, book -> {
                        Intent intent = new Intent(BooksActivity.this, EditBookActivity.class);
                        intent.putExtra("BOOK_ID", book.book.getId());
                        intent.putExtra("BOOK_NAME", book.book.getName());
                        intent.putExtra("BOOK_STATUS", book.book.getStatus().getStatus());
                        startActivityForResult(intent, REQUEST_CODE_BOOKS_EDIT);
                    }, (view, position) -> {
                        LongPressCallBack longPressCallBack = new LongPressCallBack(bookAdapter, this, this, this);
                        longPressCallBack.handleLongPress(view, position, "Book");
                    });
                    booksRecyclerView.setAdapter(bookAdapter);
                    bookAdapter.updateBooks(books);  // Notify adapter of new data
                    bookAdapter.sortBooks();
                }
            });
        } else {
            books = new ArrayList<>();
            emptyStateMessage.setVisibility(View.VISIBLE);
            booksRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Retrieve the new book from the result
            BookWithNotes newBook = null;
            if (requestCode == REQUEST_CODE_BOOKS_ADD && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                newBook = data.getParcelableExtra("NEW_BOOK", BookWithNotes.class);
                Log.d("Book Action", "Save:" + newBook);

                if (bookAdapter == null) {
                    bookAdapter = new BookAdapter(this, allBooks, null, null);
                    booksRecyclerView.setAdapter(bookAdapter);
                }

                // Add the new book to the list
                if (newBook != null) {
                    allBooks.add(newBook);
                    bookAdapter.updateBooks(allBooks);  // Notify the adapter to refresh the RecyclerView
                    searchEditText.setText("");
                }

                Log.d("Book Action", "List count:" + allBooks.size());

                // Update empty state visibility
                if (allBooks.isEmpty()) {
                    emptyStateMessage.setVisibility(View.VISIBLE);
                    booksRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateMessage.setVisibility(View.GONE);
                    booksRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            if (requestCode == REQUEST_CODE_BOOKS_EDIT) {
                int updatedBookId = data.getIntExtra("UPDATED_BOOK_ID", -1);
                if (updatedBookId != -1) {
                    bookAdapter.notifyDataSetChanged(); // Refresh RecyclerView
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
        BookWithNotes bookWithNotes = books.get(position);
        Book book = bookWithNotes.book;
        List<BookNote> notes = bookWithNotes.notes;
        bookAdapter.removeBook(position);

        // Perform database deletion in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            // Assuming you have a game object and gameDao configured for database
            notes.forEach(note -> {
                BookDatabase.getInstance(this).bookDao().deleteNote(note); // First delete all DLCs
            });
            BookDatabase.getInstance(this).bookDao().delete(book);  // Deleting the item from the database

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
        BookWithNotes bookWithNotes = books.get(position);
        return bookWithNotes.book.getStatus().getStatus();
    }

    @Override
    public List<String> getItems() {
        return Arrays.stream(BookStatus.values()).map(BookStatus::getStatus).collect(Collectors.toList());
    }

    @Override
    public void updateItem(int position, String value) {
        BookWithNotes bookWithNotes = books.get(position);
        Book book = bookWithNotes.book;

        // Perform database update in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            book.setStatus(BookStatus.fromStatus(value));
            BookDatabase.getInstance(this).bookDao().update(book);

            // Show a Toast on the main thread after the update is successful
            runOnUiThread(() -> {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void filterBooks(String query) {
        String lower = query.toLowerCase();

        List<BookWithNotes> filtered = allBooks.stream()
                .filter(bookWithNotes -> {
                    Book book = bookWithNotes.book;

                    boolean matchesName = book.getName().toLowerCase().contains(lower);
                    boolean matchesStatus = book.getStatus().getStatus().toLowerCase().contains(lower);
                    boolean matchesAuthor = book.getAuthor().toLowerCase().contains(lower);
                    boolean matchesPublication = book.getPublication().toLowerCase().contains(lower);

                    // Match genre
                    boolean matchesGenre = book.getGenre().stream()
                            .map(BookGenre::getGenre)  // assuming getGenre() returns the string name like "Fantasy"
                            .anyMatch(genre -> genre.toLowerCase().contains(lower));

                    return matchesName || matchesStatus || matchesAuthor || matchesPublication || matchesGenre;
                })
                .collect(Collectors.toList());

        bookAdapter.updateBooks(filtered);

        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        booksRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void applyFilters(Map<String, String> filters) {
        List<BookWithNotes> filtered = allBooks.stream().filter(bookWithNotes -> {
            Book b = bookWithNotes.book;

            boolean status = Objects.equals(filters.get(FilterBarManager.FILTER_STATUS), "All") || b.getStatus().getStatus().equalsIgnoreCase(filters.get(FilterBarManager.FILTER_STATUS));
            boolean genre = Objects.equals(filters.get(FilterBarManager.FILTER_GENRE), "All") || b.getGenre().contains(BookGenre.fromGenre(filters.get(FilterBarManager.FILTER_GENRE)));
            boolean backlog = Objects.equals(filters.get(FilterBarManager.FILTER_BACKLOG), "All") || Objects.requireNonNull(filters.get(FilterBarManager.FILTER_BACKLOG)).equalsIgnoreCase("Yes") == b.isBacklog();
            boolean watchlist = Objects.equals(filters.get(FilterBarManager.FILTER_WATCHLIST), "All") || Objects.requireNonNull(filters.get(FilterBarManager.FILTER_WATCHLIST)).equalsIgnoreCase("Yes") == b.isWishlist();

            return status && genre && backlog && watchlist;
        }).collect(Collectors.toList());

        // Sorting Logic
        String sortBy = filters.get(FilterBarManager.FILTER_SORTING);
        if (sortBy != null) {
            switch (sortBy) {
                case "Name":
                    filtered.sort((a, b) -> a.book.getName().compareToIgnoreCase(b.book.getName()));
                    break;
                default:
                    bookAdapter.sortBooks();
                    break;
            }
        } else {
            bookAdapter.sortBooks();
        }

        bookAdapter.updateBooks(filtered);
        emptyStateMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        booksRecyclerView.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}