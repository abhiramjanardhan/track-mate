package com.aj.trackmate.managers.filter;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class FilterIntentBuilder {
    private final Map<String, String> filters = new HashMap<>();

    public FilterIntentBuilder set(String key, String value) {
        filters.put(key, value);
        return this;
    }

    public Intent buildIntent() {
        Intent intent = new Intent();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        return intent;
    }

    public static Map<String, String> extract(Intent intent) {
        Map<String, String> extracted = new HashMap<>();
        if (intent == null) return extracted;

        for (String key : FilterBarManager.getAllFilterKeys()) {
            String value = intent.getStringExtra(key);
            if (value != null) {
                extracted.put(key, value);
            }
        }
        return extracted;
    }
}
