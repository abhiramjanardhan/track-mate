package com.aj.trackmate.models.books.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.books.BookGenre;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class BookGenreConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromGenreList(List<BookGenre> bookGenres) {
        return gson.toJson(bookGenres);
    }

    @TypeConverter
    public static List<BookGenre> toGenreList(String genreJson) {
        Type listType = new TypeToken<List<BookGenre>>() {}.getType();
        return gson.fromJson(genreJson, listType);
    }
}
