package com.aj.trackmate.models.game.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.game.GamePurchaseMode;
import com.aj.trackmate.models.game.GameStatus;

public class GamePurchaseModeConverter {
    @TypeConverter
    public static GamePurchaseMode fromString(String status) {
        return status == null ? null : GamePurchaseMode.valueOf(status);
    }

    @TypeConverter
    public static String toString(GamePurchaseMode status) {
        return status == null ? null : status.name();
    }
}
