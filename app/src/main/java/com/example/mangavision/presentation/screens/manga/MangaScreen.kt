package com.example.mangavision.presentation.screens.manga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mangavision.domain.model.Manga
import com.example.mangavision.domain.model.Resource
import com.example.mangavision.presentation.viewmodel.MangaViewModel
import com.google.gson.Gson

@Composable
fun MangaScreen(
    onMangaClick: (String) -> Unit,
    viewModel: MangaViewModel = hiltViewModel()
) {
    val mangaList by viewModel.mangaList.collectAsState()
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        viewModel.loadMangaList()
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF1A1A1A))
                .padding(padding)
        ) {
            when (mangaList) {
                is Resource.Success -> {
                    val mangaItems = (mangaList as Resource.Success<List<Manga>>).data ?: emptyList()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3), // 3 columns grid
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(mangaItems) { manga ->
                            MangaGridItem(
                                manga = manga,
                                onClick = { onMangaClick(gson.toJson(manga)) }
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = (mangaList as Resource.Error<List<Manga>>).message ?: "Unknown error",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun MangaGridItem(
    manga: Manga,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .height(180.dp)
    ) {
        AsyncImage(
            model = manga.thumb,
            contentScale = ContentScale.FillBounds,
            contentDescription = manga.title,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}