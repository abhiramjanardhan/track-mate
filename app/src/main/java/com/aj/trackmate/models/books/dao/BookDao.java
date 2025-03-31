package com.aj.trackmate.models.books.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.books.Book;
import com.aj.trackmate.models.books.BookFor;
import com.aj.trackmate.models.books.BookNote;
import com.aj.trackmate.models.books.relations.BookWithNotes;
import com.aj.trackmate.models.game.DownloadableContent;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.relations.GameWithDownloadableContent;

import java.util.List;

@Dao
public interface BookDao {
    @Insert
    long insert(Book book);

    @Update
    int update(Book book);

    @Delete
    void delete(Book book);

    @Insert
    long insertNote(BookNote bookNote);

    @Update
    int updateNote(BookNote bookNote);

    @Delete
    void deleteNote(BookNote bookNote);

    @Query("SELECT * FROM books WHERE bookFor = :bookFor")
    LiveData<List<Book>> getBooksByFor(BookFor bookFor);

    @Query("SELECT * FROM books WHERE genre LIKE '%' || :genre || '%'")
    LiveData<List<Book>> getBooksByGenre(String genre);

    @Query("SELECT * FROM books WHERE wishlist = 1")
    LiveData<List<Book>> getBooksInWishlist();

    @Query("SELECT * FROM books WHERE started = 1")
    LiveData<List<Book>> getBooksStarted();

    @Query("SELECT * FROM books WHERE completed = 1")
    LiveData<List<Book>> getBooksCompleted();

    @Query("SELECT * FROM books")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    LiveData<Book> getBookById(int bookId);

    @Query("SELECT * FROM book_notes WHERE bookId = :bookId")
    LiveData<List<BookNote>> getBookNotesByBookId(int bookId);

    @Query("SELECT * FROM book_notes WHERE id = :id and bookId = :bookId LIMIT 1")
    LiveData<BookNote> getBookNoteById(int bookId, int id);

    @Transaction
    @Query("SELECT * FROM books")
    LiveData<List<BookWithNotes>> getAllBooksWithNotes();

    @Transaction
    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    LiveData<BookWithNotes> getBooksWithNotesById(int bookId);

    @Transaction
    @Query("SELECT * FROM books WHERE bookFor = :bookFor")
    LiveData<List<BookWithNotes>> getBooksWithNotesByFor(BookFor bookFor);
}
