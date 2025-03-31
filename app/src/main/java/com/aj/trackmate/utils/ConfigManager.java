package com.aj.trackmate.utils;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;

import static com.aj.trackmate.utils.Utility.loadFromJSONObject;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static boolean enableQueryLogging = false; // Default value

    public static void loadConfig(Context context) {
        try {
            JSONObject jsonObject = loadFromJSONObject(context, CONFIG_FILE);

            // Read the configuration value
            enableQueryLogging = jsonObject.optBoolean("enable_query_logging", false);
        } catch (Exception e) {
            Log.e("ConfigManager", "Error reading config file", e);
        }
    }

    public static boolean isQueryLoggingEnabled() {
        return enableQueryLogging;
    }
}
