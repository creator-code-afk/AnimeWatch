package com.example.animewatch.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.models.WatchStatus
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Состояние экрана "Главная" */
data class HomeUiState(
    val isLoading: Boolean = true,
    val updates: List<Anime> = emptyList(),
    val popular: List<Anime> = emptyList(),
    val continueWatching: List<Anime> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
        observeContinueWatching()
    }

    // Собираем аниме со статусом "В процессе" из локальной истории просмотра
    private fun observeContinueWatching() {
        viewModelScope.launch {
            repository.getWatchHistory().collectLatest { history ->
                val continueList = history
                    .filter { it.status == WatchStatus.WATCHING }
                    .sortedByDescending { it.updatedAt }
                    .map { h ->
                        Anime(
                            id = h.animeId,
                            titleRu = h.animeTitle,
                            titleEn = null,
                            description = "",
                            posterUrl = h.posterUrl,
                            statusText = "",
                            typeText = "",
                            genres = emptyList(),
                            voiceTeams = emptyList(),
                            episodes = emptyList(),
                            year = null
                        )
                    }
                _uiState.value = _uiState.value.copy(continueWatching = continueList)
            }
        }
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val updatesResult = repository.getUpdates()
            val popularResult = repository.getPopular()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                updates = updatesResult.getOrDefault(emptyList()),
                popular = popularResult.getOrDefault(emptyList()),
                errorMessage = if (updatesResult.isFailure && popularResult.isFailure) {
                    "Не удалось загрузить данные. Проверьте подключение к интернету."
                } else null
            )
        }
    }
}
