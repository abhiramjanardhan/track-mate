package com.aj.trackmate.models.books.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.books.BookNoteStatus;

public class BookNoteStatusConverter {
    @TypeConverter
    public static BookNoteStatus fromString(String bookStatus) {
        return bookStatus == null ? null : BookNoteStatus.valueOf(bookStatus);
    }

    @TypeConverter
    public static String toString(BookNoteStatus bookStatus) {
        return bookStatus == null ? null : bookStatus.name();
    }

}
