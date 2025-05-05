package com.example.mangavision.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangavision.domain.model.Manga
import com.example.mangavision.domain.model.MangaDetails
import com.example.mangavision.domain.model.Resource
import com.example.mangavision.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _mangaList = MutableStateFlow<Resource<List<Manga>>>(Resource.Loading())
    val mangaList: StateFlow<Resource<List<Manga>>> = _mangaList.asStateFlow()

    private val _mangaDetails = MutableStateFlow<Resource<MangaDetails>>(Resource.Loading())
    val mangaDetails: StateFlow<Resource<MangaDetails>> = _mangaDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMangaList(
        page: Int = 1,
        genres: String? = null,
        nsfw: Boolean? = null,
        type: String = "all"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            mangaRepository.getMangaList(
                page = page,
                genres = genres,
                nsfw = nsfw,
                type = type
            )
                .catch { e ->
                    _mangaList.value = Resource.Error(e.message ?: "Unknown error")
                }
                .collect { resource ->
                    _mangaList.value = resource
                }
            _isLoading.value = false
        }
    }

    fun loadMangaDetails(mangaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            mangaRepository.getMangaDetails(mangaId)
                .catch { e ->
                    _mangaDetails.value = Resource.Error(e.message ?: "Unknown error")
                }
                .collect { resource ->
                    _mangaDetails.value = resource
                }
            _isLoading.value = false
        }
    }

    fun getMangaById(mangaId: String): Manga? {
        val currentList = mangaList.value
        return if (currentList is Resource.Success) {
            currentList.data?.find { it.id == mangaId }
        } else null
    }
}