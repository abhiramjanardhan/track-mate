package com.aj.trackmate.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.aj.trackmate.database.migrations.application.DatabaseMigration1to2;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.Category;
import com.aj.trackmate.models.application.SubApplication;
import com.aj.trackmate.models.application.dao.ApplicationDao;
import com.aj.trackmate.models.application.dao.CategoryDao;
import com.aj.trackmate.models.application.dao.SubApplicationDao;
import com.aj.trackmate.utils.ConfigManager;

import java.util.concurrent.Executors;

@Database(entities = {Category.class, Application.class, SubApplication.class}, version = 2, exportSchema = false)
public abstract class ApplicationDatabase extends RoomDatabase {
    private static ApplicationDatabase instance;

    public abstract CategoryDao categoryDao();
    public abstract ApplicationDao applicationDao();
    public abstract SubApplicationDao subApplicationDao();

    public static synchronized ApplicationDatabase getInstance(Context context) {
        if (instance == null) {
            // Load configuration settings
            ConfigManager.loadConfig(context);

            RoomDatabase.Builder<ApplicationDatabase> builder = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ApplicationDatabase.class, "application_database"
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
                    Log.d("Application Query", "SQL Query: " + sqlQuery + " Args: " + bindArgs);
                }, Executors.newSingleThreadExecutor());
            }

            instance = builder.build();
        }
        return instance;
    }
}
