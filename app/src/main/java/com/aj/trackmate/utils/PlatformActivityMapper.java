package com.aj.trackmate.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import com.aj.trackmate.activities.books.BooksActivity;
import com.aj.trackmate.activities.entertainment.EntertainmentCategoryActivity;
import com.aj.trackmate.activities.entertainment.MoviesActivity;
import com.aj.trackmate.activities.entertainment.MusicActivity;
import com.aj.trackmate.activities.entertainment.TelevisionSeriesActivity;
import com.aj.trackmate.activities.game.GamePlatformActivity;
import com.aj.trackmate.models.application.SubApplication;

import java.util.*;

import static com.aj.trackmate.constants.CategoryConstants.*;
import static com.aj.trackmate.constants.RequestCodeConstants.*;

public class PlatformActivityMapper {
    private static final Map<String, Class<?>> platformActivityMap = new HashMap<>();
    private static final Map<String, Class<?>> platformSubActivityMap = new HashMap<>();
    private static final Map<String, Integer> requestCodeMap = new HashMap<>();

    static {
        platformActivityMap.put(GAME_PLAY_STATION, GamePlatformActivity.class);
        platformActivityMap.put(GAME_NINTENDO, GamePlatformActivity.class);
        platformActivityMap.put(GAME_XBOX, GamePlatformActivity.class);
        platformActivityMap.put(GAME_PC, GamePlatformActivity.class);
        platformActivityMap.put(BOOKS_READING, BooksActivity.class);
        platformActivityMap.put(BOOKS_WRITING, BooksActivity.class);
        platformActivityMap.put(ENTERTAINMENT_MOVIES, MoviesActivity.class);
        platformActivityMap.put(ENTERTAINMENT_MUSIC, MusicActivity.class);
        platformActivityMap.put(ENTERTAINMENT_TV_SERIES, TelevisionSeriesActivity.class);

        platformSubActivityMap.put(ENTERTAINMENT_MOVIES, EntertainmentCategoryActivity.class);
        platformSubActivityMap.put(ENTERTAINMENT_MUSIC, EntertainmentCategoryActivity.class);
        platformSubActivityMap.put(ENTERTAINMENT_TV_SERIES, EntertainmentCategoryActivity.class);

        requestCodeMap.put(GAME_PLAY_STATION, REQUEST_CODE_GAME_PLAYSTATION);
        requestCodeMap.put(GAME_NINTENDO, REQUEST_CODE_GAME_NINTENDO);
        requestCodeMap.put(GAME_XBOX, REQUEST_CODE_GAME_XBOX);
        requestCodeMap.put(GAME_PC, REQUEST_CODE_GAME_PC);
        requestCodeMap.put(BOOKS_READING, REQUEST_CODE_BOOKS_READING);
        requestCodeMap.put(BOOKS_WRITING, REQUEST_CODE_BOOKS_WRITING);
        requestCodeMap.put(ENTERTAINMENT_MOVIES, REQUEST_CODE_ENTERTAINMENT_MOVIES);
        requestCodeMap.put(ENTERTAINMENT_MUSIC, REQUEST_CODE_ENTERTAINMENT_MUSIC);
        requestCodeMap.put(ENTERTAINMENT_TV_SERIES, REQUEST_CODE_ENTERTAINMENT_TV_SERIES);
    }

    // Method to get the corresponding activity class based on the platform
    public static Class<?> getActivityClassForPlatform(String platform) {
        return platformActivityMap.get(platform);
    }

    public static Class<?> getActivityClassForSubPlatform(String platform) {
        return platformSubActivityMap.get(platform);
    }

    public static int getRequestCode(String platform) {
        return requestCodeMap.get(platform);
    }

    // Start the activity dynamically
    public static void startPlatformActivity(Context context, String category) {
        Log.d("Platform Mapper", "Category: " + category);
        Class<?> activityClass = getActivityClassForPlatform(category);
        if (activityClass != null) {
            Intent intent = new Intent(context, activityClass);
            intent.putExtra("CATEGORY", category);
            context.startActivity(intent);
        } else {
            // Handle the case where no activity is mapped for the platform
            Log.e("PlatformActivityMapper", "No activity found for platform: " + category);
        }
    }

    public static void startPlatformActivity(Context context, String category, List<SubApplication> subApplications) {
        Log.d("Platform Mapper", "Category: " + category);
        Class<?> activityClass = getActivityClassForSubPlatform(category);
        if (activityClass != null) {
            // If subcategories exist, navigate to SubCategoryActivity
            Intent intent = new Intent(context, activityClass);
            intent.putParcelableArrayListExtra("SUBCATEGORIES", (ArrayList<? extends Parcelable>) subApplications);
            intent.putExtra("CATEGORY_NAME", category);
            context.startActivity(intent);
        } else {
            // Handle the case where no activity is mapped for the platform
            Log.e("PlatformActivityMapper", "No sub activity found for platform: " + category);
        }
    }

    public static void startPlatformActivity(Context context, String applicationName, String category) {
        Log.d("Platform Mapper", "Application Name: " + applicationName);
        Log.d("Platform Mapper", "Category: " + category);
        Class<?> activityClass = getActivityClassForPlatform(applicationName);
        if (activityClass != null) {
            Intent intent = new Intent(context, activityClass);
            intent.putExtra("CATEGORY", category);
            context.startActivity(intent);
        } else {
            // Handle the case where no activity is mapped for the platform
            Log.e("PlatformActivityMapper", "No activity found for platform: " + category);
        }
    }
}
