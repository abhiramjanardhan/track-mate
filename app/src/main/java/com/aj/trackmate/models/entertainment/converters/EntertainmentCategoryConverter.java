package com.aj.trackmate.models.entertainment.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.entertainment.EntertainmentCategory;

public class EntertainmentCategoryConverter {
    @TypeConverter
    public static EntertainmentCategory fromString(String language) {
        return language == null ? null : EntertainmentCategory.valueOf(language);
    }

    @TypeConverter
    public static String toString(EntertainmentCategory category) {
        return category == null ? null : category.name();
    }

}
