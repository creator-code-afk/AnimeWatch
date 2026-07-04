package com.example.animewatch.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.animewatch.domain.models.Episode
import com.example.animewatch.ui.theme.AccentPurple
import com.example.animewatch.ui.theme.BackgroundDark
import com.example.animewatch.ui.theme.SurfaceDark
import com.example.animewatch.ui.theme.TextSecondary

/**
 * Экран деталей аниме: обложка, описание, кнопка "Смотреть" и список серий.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    animeId: Int,
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onEpisodeClick: (animeId: Int, episodeNumber: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animeId) {
        viewModel.loadAnime(animeId)
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text(uiState.anime?.titleRu ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = AccentPurple
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.anime == null -> {
                    Text(
                        text = uiState.errorMessage ?: "Ошибка загрузки",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    val anime = uiState.anime!!
                    val lastEpisode = uiState.watchHistory?.lastEpisode ?: 0

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            AsyncImage(
                                model = anime.posterUrl,
                                contentDescription = anime.titleRu,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                            )
                        }
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = anime.titleRu,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                if (!anime.titleEn.isNullOrBlank()) {
                                    Text(
                                        text = anime.titleEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = listOf(anime.typeText, anime.statusText, anime.year?.toString())
                                        .filter { !it.isNullOrBlank() }
                                        .joinToString(" • "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                if (anime.genres.isNotEmpty()) {
                                    Text(
                                        text = anime.genres.joinToString(", "),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                if (anime.voiceTeams.isNotEmpty()) {
                                    Text(
                                        text = "Озвучка: ${anime.voiceTeams.joinToString(", ")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Text(
                                    text = anime.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 12.dp)
                                )

                                Button(
                                    onClick = {
                                        val startEpisode = if (lastEpisode > 0) lastEpisode else (anime.episodes.firstOrNull()?.episodeNumber ?: 1)
                                        onEpisodeClick(anime.id, startEpisode)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Text(
                                        text = if (lastEpisode > 0) "  Продолжить с серии $lastEpisode" else "  Смотреть",
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                Text(
                                    text = "Серии",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                                )
                            }
                        }
                        items(anime.episodes, key = { it.episodeNumber }) { episode ->
                            EpisodeRow(
                                episode = episode,
                                isWatched = episode.episodeNumber <= lastEpisode,
                                onClick = { onEpisodeClick(anime.id, episode.episodeNumber) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeRow(
    episode: Episode,
    isWatched: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .androidxClickable(onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Серия ${episode.episodeNumber}" + (episode.name?.let { " — $it" } ?: ""),
            fontWeight = if (isWatched) FontWeight.Normal else FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Смотреть",
            tint = AccentPurple
        )
    }
}

private fun Modifier.androidxClickable(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable { onClick() })
