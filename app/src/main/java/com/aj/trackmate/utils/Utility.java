package com.aj.trackmate.utils;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utility {
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.d("JSON Read", "Exception: " + ex.getMessage());
        }

        return json;
    }

    public static JSONObject loadFromJSONObject(Context context, String fileName) {
        JSONObject jsonObject = null;

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            jsonObject = new JSONObject(json);
        } catch (Exception ex) {
            Log.d("JSON Read", "Exception: " + ex.getMessage());
        }

        return jsonObject;
    }
}
