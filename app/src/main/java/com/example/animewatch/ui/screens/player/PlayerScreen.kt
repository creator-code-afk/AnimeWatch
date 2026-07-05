package com.example.animewatch.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

/**
 * Экран плеера — ExoPlayer на весь экран с элементами управления:
 * пауза/перемотка встроены в PlayerView, плюс выбор серии и качества.
 * Экран автоматически разворачивается в горизонтальную ориентацию и скрывает
 * системные панели (статус-бар/навигацию) для полноэкранного просмотра, как
 * это делают большинство видео-приложений.
 */
@Composable
fun PlayerScreen(
    animeId: Int,
    episodeNumber: Int,
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animeId, episodeNumber) {
        viewModel.load(animeId, episodeNumber)
    }

    // Разворачиваем экран в горизонтальную ориентацию и скрываем системные панели
    // на всё время просмотра, возвращаем как было при выходе с экрана.
    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        activity?.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            activity?.requestedOrientation = originalOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
            }
        }
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
                    useController = true // встроенные элементы: пауза, перемотка
                    // Важно: FIT сохраняет пропорции видео (letterbox), а не растягивает
                    // и не искажает картинку под размер экрана.
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
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

            // Выбор озвучки/качества
            Box {
                IconButton(onClick = { qualityMenuExpanded = true }) {
                    Text(uiState.selectedQuality, color = MaterialTheme.colorScheme.primary)
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
