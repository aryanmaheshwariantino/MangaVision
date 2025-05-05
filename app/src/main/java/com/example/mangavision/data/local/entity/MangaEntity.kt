package com.example.mangavision.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class MangaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val thumb: String,
    val lastUpdated: Long = System.currentTimeMillis()
)