package com.aj.trackmate.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.SubApplication;
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.Movie;
import com.aj.trackmate.models.entertainment.Music;
import com.aj.trackmate.models.entertainment.TelevisionSeries;
import com.aj.trackmate.models.entertainment.dao.EntertainmentDao;
import com.aj.trackmate.models.entertainment.dao.MovieDao;
import com.aj.trackmate.models.entertainment.dao.MusicDao;
import com.aj.trackmate.models.entertainment.dao.TelevisionSeriesDao;
import com.aj.trackmate.utils.ConfigManager;

import java.util.concurrent.Executors;

@Database(entities = {Entertainment.class, Movie.class, Music.class, TelevisionSeries.class}, version = 1, exportSchema = false)
public abstract class EntertainmentDatabase extends RoomDatabase {
    private static EntertainmentDatabase instance;

    public abstract EntertainmentDao entertainmentDao();
    public abstract MovieDao movieDao();
    public abstract MusicDao musicDao();
    public abstract TelevisionSeriesDao televisionSeriesDao();

    public static synchronized EntertainmentDatabase getInstance(Context context) {
        if (instance == null) {
            // Load configuration settings
            ConfigManager.loadConfig(context);

            Builder<EntertainmentDatabase> builder = Room.databaseBuilder(
                    context.getApplicationContext(),
                    EntertainmentDatabase.class, "entertainment_database"
            );

            // Enable foreign key constraints
            builder.addCallback(new RoomDatabase.Callback() {
                @Override
                public void onOpen(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    db.execSQL("PRAGMA foreign_keys=ON;");
                }
            });

            // Apply query logging conditionally
            if (ConfigManager.isQueryLoggingEnabled()) {
                builder.setQueryCallback((sqlQuery, bindArgs) -> {
                    Log.d("Entertainment Query", "SQL Query: " + sqlQuery + " Args: " + bindArgs);
                }, Executors.newSingleThreadExecutor());
            }

            instance = builder.build();
        }
        return instance;
    }
}
