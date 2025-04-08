package com.aj.trackmate.models.application.converters;

import androidx.room.TypeConverter;
import com.aj.trackmate.models.application.Currency;

public class CurrencyConverter {
    @TypeConverter
    public static Currency fromString(String currency) {
        return currency == null ? null : Currency.valueOf(currency);
    }

    @TypeConverter
    public static String toString(Currency currency) {
        return currency == null ? null : currency.name();
    }

}
