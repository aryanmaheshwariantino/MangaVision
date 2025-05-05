package com.example.mangavision.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mangavision.data.local.dao.MangaDao
import com.example.mangavision.data.local.dao.UserDao
import com.example.mangavision.data.local.entity.MangaEntity
import com.example.mangavision.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, MangaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MangaVisionDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mangaDao(): MangaDao
}

