package com.example.animewatch.ui.screens.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.animewatch.ui.theme.AccentPurple
import kotlinx.coroutines.delay

/**
 * Экран плеера — ExoPlayer на весь экран с элементами управления:
 * пауза/воспроизведение (встроено в PlayerView), выбор серии, выбор озвучки/качества.
 */
@Composable
fun PlayerScreen(
    animeId: Int,
    episodeNumber: Int,
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animeId, episodeNumber) {
        viewModel.load(animeId, episodeNumber)
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    var qualityMenuExpanded by remember { mutableStateOf(false) }
    var episodeMenuExpanded by remember { mutableStateOf(false) }

    // Пересоздаём медиа-элемент при смене серии/качества
    val videoUrl = viewModel.currentVideoUrl()
    LaunchedEffect(videoUrl) {
        videoUrl?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    // Периодическое сохранение прогресса просмотра (каждые 10 секунд) для статистики
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(10_000)
            if (exoPlayer.isPlaying) {
                viewModel.saveProgress(exoPlayer.currentPosition, watchedDeltaSeconds = 10L)
            }
        }
    }

    // Освобождаем плеер и сохраняем итоговый прогресс при уходе с экрана / сворачивании приложения
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.saveProgress(exoPlayer.currentPosition, watchedDeltaSeconds = 0L)
                exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.saveProgress(exoPlayer.currentPosition, watchedDeltaSeconds = 0L)
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = true // встроенные элементы: пауза, перемотка, полноэкранный режим
                }
            }
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AccentPurple
            )
        }

        // Верхняя панель: назад, номер серии, выбор серии и качества/озвучки
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
            }
            Text(
                text = "Серия ${uiState.currentEpisodeNumber}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp).weight(1f)
            )

            // Выбор серии
            Box {
                IconButton(onClick = { episodeMenuExpanded = true }) {
                    Text("Серии", color = Color.White)
                }
                DropdownMenu(expanded = episodeMenuExpanded, onDismissRequest = { episodeMenuExpanded = false }) {
                    uiState.anime?.episodes?.forEach { ep ->
                        DropdownMenuItem(
                            text = { Text("Серия ${ep.episodeNumber}") },
                            onClick = {
                                viewModel.selectEpisode(ep.episodeNumber)
                                episodeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Выбор озвучки/качества (SD/HD/FHD)
            Box {
                IconButton(onClick = { qualityMenuExpanded = true }) {
                    Text(uiState.selectedQuality, color = AccentPurple)
                }
                DropdownMenu(expanded = qualityMenuExpanded, onDismissRequest = { qualityMenuExpanded = false }) {
                    val availableQualities = uiState.anime?.episodes
                        ?.find { it.episodeNumber == uiState.currentEpisodeNumber }
                        ?.qualityLinks?.keys ?: setOf("480p", "720p", "1080p")
                    availableQualities.forEach { quality ->
                        DropdownMenuItem(
                            text = { Text(quality) },
                            onClick = {
                                viewModel.selectQuality(quality)
                                qualityMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
