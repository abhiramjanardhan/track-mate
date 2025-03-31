package com.aj.trackmate.utils;

import android.content.Context;
import com.aj.trackmate.models.application.ApplicationData;
import com.google.gson.Gson;

import static com.aj.trackmate.utils.Utility.loadJSONFromAsset;

public class ApplicationDataProvider {
    private static final String APPLICATION_FILE = "application.json";

    public static ApplicationData loadApplicationData(Context context) {
        Gson gson = new Gson();
        String json = loadJSONFromAsset(context, APPLICATION_FILE);

        return gson.fromJson(json, ApplicationData.class);
    }
}
