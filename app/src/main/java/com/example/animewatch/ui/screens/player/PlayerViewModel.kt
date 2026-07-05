package com.example.animewatch.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val isLoading: Boolean = true,
    val anime: Anime? = null,
    val currentEpisodeNumber: Int = 1,
    val selectedQuality: String = "720p", // выбранная "озвучка/качество" — 480p/720p/1080p
    val errorMessage: String? = null
)

/**
 * ViewModel экрана плеера — загружает данные аниме (если ещё не загружены),
 * определяет текущую серию и ссылку на видео, сохраняет прогресс просмотра в Room.
 */
class PlayerViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun load(animeId: Int, episodeNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, currentEpisodeNumber = episodeNumber)
            val result = repository.getAnimeById(animeId)
            result.onSuccess { anime ->
                _uiState.value = _uiState.value.copy(isLoading = false, anime = anime)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Не удалось загрузить видео")
            }
        }
    }

    fun selectQuality(quality: String) {
        _uiState.value = _uiState.value.copy(selectedQuality = quality)
    }

    fun selectEpisode(episodeNumber: Int) {
        _uiState.value = _uiState.value.copy(currentEpisodeNumber = episodeNumber)
    }

    /** Текущая ссылка на видеопоток для выбранной серии и качества/озвучки */
    fun currentVideoUrl(): String? {
        val anime = _uiState.value.anime ?: return null
        val episode = anime.episodes.find { it.episodeNumber == _uiState.value.currentEpisodeNumber } ?: return null
        val quality = _uiState.value.selectedQuality
        return episode.qualityLinks[quality]
            ?: episode.qualityLinks.values.firstOrNull()
    }

    /** Сохранение прогресса просмотра — вызывается периодически и при выходе с экрана */
    fun saveProgress(positionMs: Long, watchedDeltaSeconds: Long) {
        val anime = _uiState.value.anime ?: return
        viewModelScope.launch {
            repository.updateWatchProgress(
                anime = anime,
                episodeNumber = _uiState.value.currentEpisodeNumber,
                positionMs = positionMs,
                watchedDeltaSeconds = watchedDeltaSeconds
            )
        }
    }
}
