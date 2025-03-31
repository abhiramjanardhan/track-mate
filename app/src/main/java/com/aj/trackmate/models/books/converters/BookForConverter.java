package com.aj.trackmate.models.books.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.books.BookFor;

public class BookForConverter {
    @TypeConverter
    public static BookFor fromString(String bookFor) {
        return bookFor == null ? null : BookFor.valueOf(bookFor);
    }

    @TypeConverter
    public static String toString(BookFor bookFor) {
        return bookFor == null ? null : bookFor.name();
    }

}
