package com.aj.trackmate.models.books.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.books.Book;
import com.aj.trackmate.models.books.BookNote;

import java.util.ArrayList;
import java.util.List;

public class BookWithNotes implements Parcelable {
    @Embedded
    public Book book;

    @Relation(
            parentColumn = "id",
            entityColumn = "bookId"
    )
    public List<BookNote> notes;

    public BookWithNotes() {
        book = new Book();
        notes = new ArrayList<>();
    }

    protected BookWithNotes(Parcel in) {
        book = in.readParcelable(Book.class.getClassLoader());
        notes = new ArrayList<>();
        in.readTypedList(notes, BookNote.CREATOR);
    }

    public static final Creator<BookWithNotes> CREATOR = new Creator<BookWithNotes>() {
        @Override
        public BookWithNotes createFromParcel(Parcel in) {
            return new BookWithNotes(in);
        }

        @Override
        public BookWithNotes[] newArray(int size) {
            return new BookWithNotes[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(book, flags);
        dest.writeTypedList(notes);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
