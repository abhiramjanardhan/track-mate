package com.aj.trackmate.models.game.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.game.Platform;

public class GamePlatformConverter {
    @TypeConverter
    public static Platform fromString(String platform) {
        return platform == null ? null : Platform.valueOf(platform);
    }

    @TypeConverter
    public static String toString(Platform platform) {
        return platform == null ? null : platform.name();
    }
}
