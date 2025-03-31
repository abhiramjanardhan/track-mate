package com.aj.trackmate.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.aj.trackmate.database.migrations.game.DatabaseMigration1to2;
import com.aj.trackmate.models.game.DownloadableContent;
import com.aj.trackmate.models.game.Game;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.dao.GameDao;
import com.aj.trackmate.utils.ConfigManager;

import java.util.Arrays;
import java.util.concurrent.Executors;

@Database(entities = {Game.class, DownloadableContent.class}, version = 2, exportSchema = false)
public abstract class GameDatabase extends RoomDatabase {
    private static GameDatabase instance;

    public abstract GameDao gameDao();

    public static synchronized GameDatabase getInstance(Context context) {
        if (instance == null) {
            // Load configuration settings
            ConfigManager.loadConfig(context);

            RoomDatabase.Builder<GameDatabase> builder = Room.databaseBuilder(
                    context.getApplicationContext(),
                    GameDatabase.class, "game_database"
            ).addMigrations(DatabaseMigration1to2.MIGRATION_1_2);

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
                    Log.d("Game Query", "SQL Query: " + sqlQuery + " Args: " + bindArgs);
                }, Executors.newSingleThreadExecutor());
            }

            instance = builder.build();
        }
        return instance;
    }
}
