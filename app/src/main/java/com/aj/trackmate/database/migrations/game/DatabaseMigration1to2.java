package com.aj.trackmate.database.migrations.game;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration1to2 {
    // Migration from version 1 to 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // SQL query to add the new column
            database.execSQL("ALTER TABLE games ADD COLUMN purchaseMode TEXT");
        }
    };
}
