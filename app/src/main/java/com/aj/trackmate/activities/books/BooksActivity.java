package com.aj.trackmate.activities.books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.adapters.books.BookAdapter;
import com.aj.trackmate.database.BookDatabase;
import com.aj.trackmate.models.books.Book;
import com.aj.trackmate.models.books.BookFor;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.aj.trackmate.models.books.BookNote;
import com.aj.trackmate.models.books.relations.BookWithNotes;
import com.aj.trackmate.operations.SwipeToDeleteCallback;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_BOOKS_ADD;
import static com.aj.trackmate.constants.RequestCodeConstants.REQUEST_CODE_BOOKS_EDIT;

public class BooksActivity extends AppCompatActivity implements ItemRemovalListener, ItemTouchListener {

    private ListView listView;
    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private List<BookWithNotes> books;
    private TextView title, emptyStateMessage;
    private FloatingActionButton addButton;

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

        title.setText("Books List");
        emptyStateMessage.setText("No Books Available");

        // Get the platform name from the Intent
        String bookFor = getIntent().getStringExtra("CATEGORY");

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
                books = booksList;
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
                    });
                    booksRecyclerView.setAdapter(bookAdapter);
                    bookAdapter.updateGames(books);  // Notify adapter of new data
                }

                // Setup the swipe-to-delete functionality
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(bookAdapter, this, this));
                itemTouchHelper.attachToRecyclerView(booksRecyclerView);
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
                    bookAdapter = new BookAdapter(this, books, null);
                    booksRecyclerView.setAdapter(bookAdapter);
                }

                // Add the new book to the list
                if (newBook != null) {
                    books.add(newBook);
                    bookAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the RecyclerView
                }

                Log.d("Book Action", "List count:" + books.size());

                // Update empty state visibility
                if (books.isEmpty()) {
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
}