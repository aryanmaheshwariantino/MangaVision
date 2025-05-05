package com.example.mangavision.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// data/remote/api/MangaApiService.kt
// data/remote/api/MangaApi.kt
interface MangaApiService {
    companion object {
        const val BASE_URL = "https://mangaverse-api.p.rapidapi.com/"
//        const val API_KEY = "6d0d9971e2msh999581d6f834ef4p1059a3jsn176cfc5b31ed"
        const val API_KEY = "8ae952315dmshe23196540068384p14c68ajsnadcfb0348a2f"
        const val API_HOST = "mangaverse-api.p.rapidapi.com"
    }

    @GET("manga/fetch")
    suspend fun getMangaList(
        @Query("page") page: Int,
        @Query("genres") genres: String? = null,
        @Query("nsfw") nsfw: Boolean? = null,
        @Query("type") type: String? = "all",
        @Header("X-RapidAPI-Key") apiKey: String = API_KEY,
        @Header("X-RapidAPI-Host") host: String = API_HOST,
        @Header("Accept") accept: String = "application/json"
    ): Response<MangaListResponse>

    @GET("manga/{id}")
    suspend fun getMangaDetails(
        @Path("id") mangaId: String,
        @Header("X-RapidAPI-Key") apiKey: String = API_KEY,
        @Header("X-RapidAPI-Host") host: String = API_HOST,
        @Header("Accept") accept: String = "application/json"
    ): Response<MangaDetailsResponse>
}

// Response data classes
data class MangaListResponse(
    @SerializedName("data") val data: List<MangaApiResponse>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("limit") val limit: Int
)

data class MangaApiResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("thumb") val thumb: String? = null,
    @SerializedName("lastUpdated") val lastUpdated: Long = System.currentTimeMillis()
)

data class MangaDetailsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("thumb") val thumb: String? = null,
    @SerializedName("chapters") val chapters: List<ChapterResponse> = emptyList(),
    @SerializedName("genres") val genres: List<String> = emptyList()
)

data class ChapterResponse(
    @SerializedName("number") val number: Int,
    @SerializedName("title") val title: String,
    @SerializedName("date") val date: String
)