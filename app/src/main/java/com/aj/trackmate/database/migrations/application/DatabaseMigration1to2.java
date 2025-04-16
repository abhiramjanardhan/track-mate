package com.aj.trackmate.database.migrations.application;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration1to2 {
    // Migration from version 1 to 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add 'visible' column to category table
            database.execSQL("ALTER TABLE category ADD COLUMN visible INTEGER NOT NULL DEFAULT 1");

            // Add 'visible' column to application table
            database.execSQL("ALTER TABLE application ADD COLUMN visible INTEGER NOT NULL DEFAULT 1");

            // Add 'visible' column to sub_application table
            database.execSQL("ALTER TABLE sub_application ADD COLUMN visible INTEGER NOT NULL DEFAULT 1");
        }
    };
}
