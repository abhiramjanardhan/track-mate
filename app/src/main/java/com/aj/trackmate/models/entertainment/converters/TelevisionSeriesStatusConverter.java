package com.aj.trackmate.models.entertainment.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.entertainment.TelevisionSeriesStatus;

public class TelevisionSeriesStatusConverter {
    @TypeConverter
    public static TelevisionSeriesStatus fromString(String status) {
        return status == null ? null : TelevisionSeriesStatus.valueOf(status);
    }

    @TypeConverter
    public static String toString(TelevisionSeriesStatus televisionSeriesStatus) {
        return televisionSeriesStatus == null ? null : televisionSeriesStatus.name();
    }

}
