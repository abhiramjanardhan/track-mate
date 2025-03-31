package com.aj.trackmate.models.entertainment.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.entertainment.MovieStatus;

public class MovieStatusConverter {
    @TypeConverter
    public static MovieStatus fromString(String status) {
        return status == null ? null : MovieStatus.valueOf(status);
    }

    @TypeConverter
    public static String toString(MovieStatus movieStatus) {
        return movieStatus == null ? null : movieStatus.name();
    }

}
