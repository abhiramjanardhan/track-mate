package com.aj.trackmate.models.game.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.game.DownloadableContentType;

public class DownloadableContentTypeConverter {
    @TypeConverter
    public static DownloadableContentType fromString(String downloadableContentType) {
        return downloadableContentType == null ? null : DownloadableContentType.valueOf(downloadableContentType);
    }

    @TypeConverter
    public static String toString(DownloadableContentType downloadableContentType) {
        return downloadableContentType == null ? null : downloadableContentType.name();
    }
}
