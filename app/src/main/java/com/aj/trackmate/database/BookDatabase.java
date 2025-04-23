package com.aj.trackmate.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.aj.trackmate.database.migrations.books.DatabaseMigration1to2;
import com.aj.trackmate.models.books.Book;
import com.aj.trackmate.models.books.BookNote;
import com.aj.trackmate.models.books.dao.BookDao;
import com.aj.trackmate.utils.ConfigManager;

import java.util.concurrent.Executors;

@Database(entities = {Book.class, BookNote.class}, version = 2, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    private static BookDatabase instance;

    public abstract BookDao bookDao();

    public static synchronized BookDatabase getInstance(Context context) {
        if (instance == null) {
            // Load configuration settings
            ConfigManager.loadConfig(context);

            Builder<BookDatabase> builder = Room.databaseBuilder(
                    context.getApplicationContext(),
                    BookDatabase.class, "book_database"
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
                    Log.d("Book Query", "SQL Query: " + sqlQuery + " Args: " + bindArgs);
                }, Executors.newSingleThreadExecutor());
            }

            instance = builder.build();
        }
        return instance;
    }
}
