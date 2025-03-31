package com.aj.trackmate.models.game.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.game.GameStatus;

public class GameStatusConverter {
    @TypeConverter
    public static GameStatus fromString(String status) {
        return status == null ? null : GameStatus.valueOf(status);
    }

    @TypeConverter
    public static String toString(GameStatus status) {
        return status == null ? null : status.name();
    }
}
