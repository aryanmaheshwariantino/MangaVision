package com.example.mangavision.domain.model

// domain/model/Resource.kt
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val isFromCache: Boolean = false
) {
    class Success<T>(data: T, isFromCache: Boolean = false) : Resource<T>(data, isFromCache = isFromCache)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}