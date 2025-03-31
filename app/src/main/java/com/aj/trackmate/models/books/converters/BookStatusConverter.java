package com.aj.trackmate.models.books.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.books.BookStatus;

public class BookStatusConverter {
    @TypeConverter
    public static BookStatus fromString(String bookStatus) {
        return bookStatus == null ? null : BookStatus.valueOf(bookStatus);
    }

    @TypeConverter
    public static String toString(BookStatus bookStatus) {
        return bookStatus == null ? null : bookStatus.name();
    }

}
