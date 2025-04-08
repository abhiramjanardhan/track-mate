package com.aj.trackmate.database.migrations.game;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Calendar;

public class DatabaseMigration3to4 {
    // Migration from version 3 to 4
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add the new 'year' column with default current year
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            // SQL query to add the new column
            database.execSQL("ALTER TABLE games ADD COLUMN year INTEGER NOT NULL DEFAULT " + currentYear);
            database.execSQL("ALTER TABLE game_downloadable_content ADD COLUMN year INTEGER NOT NULL DEFAULT " + currentYear);
        }
    };
}
