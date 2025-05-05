package com.example.mangavision.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mangavision.data.local.dao.MangaDao
import com.example.mangavision.data.local.dao.UserDao
import com.example.mangavision.data.local.entity.MangaEntity
import com.example.mangavision.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, MangaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MangaVisionDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mangaDao(): MangaDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create new table with updated column names
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS manga_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        title TEXT NOT NULL,
                        summary TEXT NOT NULL,
                        thumb TEXT NOT NULL,
                        lastUpdated INTEGER NOT NULL
                    )
                """)
                // 2. Copy data from old table to new table, mapping old columns to new ones
                database.execSQL("""
                    INSERT INTO manga_new (id, title, summary, thumb, lastUpdated)
                    SELECT id, title, description, coverurl, lastUpdated FROM manga
                """)
                // 3. Drop old table
                database.execSQL("DROP TABLE manga")
                // 4. Rename new table to original name
                database.execSQL("ALTER TABLE manga_new RENAME TO manga")
            }
        }
    }
}

