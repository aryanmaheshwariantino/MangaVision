package com.example.mangavision.domain.model

// domain/model/Manga.kt
data class Manga(
    val id: String,
    val title: String,
    val summary: String? = null,
    val thumb: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

// domain/model/MangaDetails.kt
data class MangaDetails(
    val id: String,
    val title: String,
    val summary: String? = null,
    val thumb: String? = null,
    val chapters: List<Chapter> = emptyList(),
    val genres: List<String> = emptyList()
) {
    data class Chapter(
        val number: Int,
        val title: String,
        val date: String
    )
}