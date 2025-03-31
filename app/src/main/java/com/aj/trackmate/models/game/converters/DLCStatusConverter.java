package com.aj.trackmate.models.game.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.game.DLCStatus;

public class DLCStatusConverter {
    @TypeConverter
    public static DLCStatus fromString(String status) {
        return status == null ? null : DLCStatus.valueOf(status);
    }

    @TypeConverter
    public static String toString(DLCStatus status) {
        return status == null ? null : status.name();
    }
}
