package com.aj.trackmate.database.migrations.game;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration2to3 {
    // Migration from version 2 to 3
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            // SQL query to add the new column
            database.execSQL("ALTER TABLE games ADD COLUMN amount REAL NOT NULL DEFAULT 0.0");
            database.execSQL("ALTER TABLE games ADD COLUMN currency TEXT DEFAULT 'INR'");
            database.execSQL("ALTER TABLE game_downloadable_content ADD COLUMN amount REAL NOT NULL DEFAULT 0.0");
            database.execSQL("ALTER TABLE game_downloadable_content ADD COLUMN currency TEXT DEFAULT 'INR'");
        }
    };
}
