package com.aj.trackmate.models.game.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.game.GamePurchaseMode;
import com.aj.trackmate.models.game.GamePurchaseType;

public class GamePurchaseTypeConverter {
    @TypeConverter
    public static GamePurchaseType fromString(String status) {
        return status == null ? null : GamePurchaseType.valueOf(status);
    }

    @TypeConverter
    public static String toString(GamePurchaseType status) {
        return status == null ? null : status.name();
    }
}
