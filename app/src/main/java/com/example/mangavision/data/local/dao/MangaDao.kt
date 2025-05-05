package com.example.mangavision.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mangavision.data.local.entity.MangaEntity

@Dao
interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: MangaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mangaList: List<MangaEntity>)

    @Query("SELECT * FROM manga ORDER BY lastUpdated DESC")
    suspend fun getAllManga(): List<MangaEntity>

    @Query("DELETE FROM manga")
    suspend fun clearAll()
}
