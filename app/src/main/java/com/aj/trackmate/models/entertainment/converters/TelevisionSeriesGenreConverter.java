package com.aj.trackmate.models.entertainment.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.entertainment.TelevisionSeriesGenre;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TelevisionSeriesGenreConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromGenreList(List<TelevisionSeriesGenre> televisionSeriesGenres) {
        return gson.toJson(televisionSeriesGenres);
    }

    @TypeConverter
    public static List<TelevisionSeriesGenre> toGenreList(String genreJson) {
        Type listType = new TypeToken<List<TelevisionSeriesGenre>>() {}.getType();
        return gson.fromJson(genreJson, listType);
    }
}
