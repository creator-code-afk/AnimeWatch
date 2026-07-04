package com.example.animewatch.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.ui.components.AnimeCarousel
import com.example.animewatch.ui.theme.BackgroundDark

/**
 * Главный экран — карусели "Продолжить просмотр", "Обновления", "Популярное".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAnimeClick: (Anime) -> Unit,
    onSearchClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("AnimeWatch") },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                ),
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.continueWatching.isNotEmpty()) {
                            item {
                                AnimeCarousel(
                                    title = "Продолжить просмотр",
                                    items = uiState.continueWatching,
                                    onAnimeClick = onAnimeClick
                                )
                            }
                        }
                        item {
                            AnimeCarousel(
                                title = "Обновления",
                                items = uiState.updates,
                                onAnimeClick = onAnimeClick
                            )
                        }
                        item {
                            AnimeCarousel(
                                title = "Популярное",
                                items = uiState.popular,
                                onAnimeClick = onAnimeClick
                            )
                        }
                        item {
                            // Небольшой отступ в конце списка
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(24.dp))
                        }
                    }
                }
            }
        }
    }
}
