package com.aj.trackmate.models.application.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.CategoryEnum;

public class ApplicationCategoryConverter {
    @TypeConverter
    public static CategoryEnum fromString(String category) {
        return category == null ? null : CategoryEnum.valueOf(category);
    }

    @TypeConverter
    public static String toString(CategoryEnum category) {
        return category == null ? null : category.name();
    }

}
