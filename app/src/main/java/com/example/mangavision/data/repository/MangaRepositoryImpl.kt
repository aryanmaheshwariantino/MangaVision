package com.example.mangavision.data.repository

import com.example.mangavision.data.local.dao.MangaDao
import com.example.mangavision.data.local.entity.MangaEntity
import com.example.mangavision.data.remote.api.MangaApiResponse
import com.example.mangavision.data.remote.api.MangaApiService
import com.example.mangavision.data.remote.api.MangaDetailsResponse
import com.example.mangavision.di.IoDispatcher
import com.example.mangavision.domain.model.Manga
import com.example.mangavision.domain.model.MangaDetails
import com.example.mangavision.domain.model.Resource
import com.example.mangavision.domain.repository.MangaRepository
import com.example.mangavision.util.Logging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepositoryImpl @Inject constructor(
    private val mangaDao: MangaDao,
    private val api: MangaApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : MangaRepository {

    override fun getMangaList(
        page: Int,
        genres: String?,
        nsfw: Boolean?,
        type: String
    ): Flow<Resource<List<Manga>>> = flow {
        try {
            val params = mapOf<String, Any?>(
                "page" to page,
                "genres" to genres,
                "nsfw" to nsfw,
                "type" to type
            )
            Logging.logApiRequest("getMangaList", params)
            val response = api.getMangaList(
                page = page,
                genres = genres,
                nsfw = nsfw,
                type = type
            )
            if (response.isSuccessful) {
                val mangaListResponse = response.body()
                val mangaList = mangaListResponse?.data?.map { apiManga ->
                    Manga(
                        id = apiManga.id,
                        title = apiManga.title,
                        summary = apiManga.summary,
                        thumb = apiManga.thumb,
                        lastUpdated = apiManga.lastUpdated
                    )
                } ?: emptyList()
                // Save to DB for offline access
                mangaDao.insertAll(mangaList.map { it.toEntity() })
                Logging.logApiSuccess("getMangaList", "Retrieved ${mangaList.size} manga items (Page ${mangaListResponse?.page ?: 1} of ${mangaListResponse?.total ?: 0})")
                emit(Resource.Success(mangaList))
            } else {
                val cached = mangaDao.getAllManga().map { it.toManga() }
                if (cached.isNotEmpty()) {
                    emit(Resource.Success(cached))
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    val errorMessage = "Failed to fetch manga list: ${response.code()}. Error: $errorBody"
                    Logging.logApiError("getMangaList", errorMessage)
                    emit(Resource.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            val cached = mangaDao.getAllManga().map { it.toManga() }
            if (cached.isNotEmpty()) {
                emit(Resource.Success(cached))
            } else {
                val errorMessage = "Error occurred: ${e.message ?: "Unknown error"}. Stack trace: ${e.stackTraceToString()}"
                Logging.logApiError("getMangaList", errorMessage)
                emit(Resource.Error(errorMessage))
            }
        }
    }.flowOn(dispatcher)

    override fun getMangaDetails(mangaId: String): Flow<Resource<MangaDetails>> = flow {
        try {
            Logging.logApiRequest("getMangaDetails", mapOf("mangaId" to mangaId))
            val response = api.getMangaDetails(mangaId)
            if (response.isSuccessful) {
                val apiDetails = response.body()
                if (apiDetails != null) {
                    val details = MangaDetails(
                        id = apiDetails.id,
                        title = apiDetails.title,
                        summary = apiDetails.summary,
                        thumb = apiDetails.thumb,
                        chapters = apiDetails.chapters.map { chapter ->
                            MangaDetails.Chapter(
                                number = chapter.number,
                                title = chapter.title,
                                date = chapter.date
                            )
                        },
                        genres = apiDetails.genres
                    )
                    Logging.logApiSuccess("getMangaDetails", "Retrieved details for manga: ${details.title}")
                    emit(Resource.Success(details))
                } else {
                    val errorMessage = "Manga details not found"
                    Logging.logApiError("getMangaDetails", errorMessage)
                    emit(Resource.Error(errorMessage))
                }
            } else {
                val errorMessage = "Failed to fetch manga details: ${response.code()}"
                Logging.logApiError("getMangaDetails", errorMessage)
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown error occurred"
            Logging.logApiError("getMangaDetails", errorMessage)
            emit(Resource.Error(errorMessage))
        }
    }.flowOn(dispatcher)
}

private fun Manga.toEntity(): MangaEntity {
    return MangaEntity(
        id = this.id,
        title = this.title,
        summary = this.summary ?: "",
        thumb = this.thumb ?: "",
        lastUpdated = this.lastUpdated
    )
}

private fun MangaEntity.toManga(): Manga {
    return Manga(
        id = this.id,
        title = this.title,
        thumb = this.thumb,
        summary = this.summary,
        lastUpdated = this.lastUpdated
    )
}

// Extension functions for model mapping
private fun MangaApiResponse.toManga(): Manga {
    return Manga(
        id = this.id,
        title = this.title,
        thumb = this.thumb,
        summary = this.summary,
        lastUpdated = System.currentTimeMillis()
    )
}

private fun MangaDetailsResponse.toMangaDetails(): MangaDetails {
    return MangaDetails(
        id = this.id,
        title = this.title,
        thumb = this.thumb,
        summary = this.summary,
        chapters = this.chapters.map { chapter ->
            MangaDetails.Chapter(
                number = chapter.number,
                title = chapter.title,
                date = chapter.date
            )
        },
        genres = this.genres
    )
}

private fun MangaApiResponse.toMangaEntity(): MangaEntity {
    return MangaEntity(
        id = this.id,
        title = this.title,
        summary = this.summary?:"",
        thumb = this.thumb?:"",
        lastUpdated = System.currentTimeMillis()
    )
}