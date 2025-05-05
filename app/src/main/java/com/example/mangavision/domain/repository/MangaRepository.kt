package com.example.mangavision.domain.repository

import com.example.mangavision.domain.model.Manga
import com.example.mangavision.domain.model.MangaDetails
import com.example.mangavision.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface MangaRepository {
    fun getMangaList(
        page: Int = 1,
        genres: String? = null,
        nsfw: Boolean? = null,
        type: String = "all"
    ): Flow<Resource<List<Manga>>>
    
    fun getMangaDetails(mangaId: String): Flow<Resource<MangaDetails>>
}