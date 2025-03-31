package com.aj.trackmate.models.entertainment.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.entertainment.Language;
import com.aj.trackmate.models.entertainment.MoviePlatform;

public class LanguageConverter {
    @TypeConverter
    public static Language fromString(String language) {
        return language == null ? null : Language.valueOf(language);
    }

    @TypeConverter
    public static String toString(Language language) {
        return language == null ? null : language.name();
    }

}
