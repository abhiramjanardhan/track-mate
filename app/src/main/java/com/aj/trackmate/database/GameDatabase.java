package com.aj.trackmate.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.aj.trackmate.database.migrations.game.DatabaseMigration1to2;
import com.aj.trackmate.database.migrations.game.DatabaseMigration2to3;
import com.aj.trackmate.database.migrations.game.DatabaseMigration3to4;
import com.aj.trackmate.database.migrations.game.DatabaseMigration4to5;
import com.aj.trackmate.models.game.DownloadableContent;
import com.aj.trackmate.models.game.Game;
import com.aj.trackmate.models.game.dao.GameDao;
import com.aj.trackmate.utils.ConfigManager;

import java.util.concurrent.Executors;

@Database(entities = {Game.class, DownloadableContent.class}, version = 5, exportSchema = false)
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
            ).addMigrations(DatabaseMigration1to2.MIGRATION_1_2)
                    .addMigrations(DatabaseMigration2to3.MIGRATION_2_3)
                    .addMigrations(DatabaseMigration3to4.MIGRATION_3_4)
                    .addMigrations(DatabaseMigration4to5.MIGRATION_4_5);

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
