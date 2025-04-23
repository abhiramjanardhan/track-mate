package com.aj.trackmate.database.migrations.game;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration4to5 {
    // Migration from version 1 to 2
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add 'favorite' column to category table
            database.execSQL("ALTER TABLE games ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
        }
    };
}
