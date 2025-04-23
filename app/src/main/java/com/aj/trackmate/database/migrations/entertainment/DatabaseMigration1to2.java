package com.aj.trackmate.database.migrations.entertainment;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration1to2 {
    // Migration from version 1 to 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add 'favorite' column to category table
            database.execSQL("ALTER TABLE movies ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE music ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE television_series ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
        }
    };
}
