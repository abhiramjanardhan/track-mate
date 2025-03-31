package com.aj.trackmate.models.entertainment.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.entertainment.MovieGenre;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MovieGenreConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromGenreList(List<MovieGenre> movieGenres) {
        return gson.toJson(movieGenres);
    }

    @TypeConverter
    public static List<MovieGenre> toGenreList(String genreJson) {
        Type listType = new TypeToken<List<MovieGenre>>() {}.getType();
        return gson.fromJson(genreJson, listType);
    }
}
