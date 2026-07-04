package com.example.animewatch.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.models.WatchHistory
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val anime: Anime? = null,
    val isFavorite: Boolean = false,
    val watchHistory: WatchHistory? = null,
    val errorMessage: String? = null
)

/** ViewModel экрана деталей аниме — загружает полную информацию и подписывается на избранное/историю */
class DetailViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadAnime(animeId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.getAnimeById(animeId)
            result.onSuccess { anime ->
                _uiState.value = _uiState.value.copy(isLoading = false, anime = anime)
                observeFavorite(animeId)
                observeHistory(animeId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Не удалось загрузить информацию об аниме"
                )
            }
        }
    }

    private fun observeFavorite(animeId: Int) {
        viewModelScope.launch {
            repository.isFavorite(animeId).collectLatest { isFav ->
                _uiState.value = _uiState.value.copy(isFavorite = isFav)
            }
        }
    }

    private fun observeHistory(animeId: Int) {
        viewModelScope.launch {
            repository.getWatchHistoryFor(animeId).collectLatest { history ->
                _uiState.value = _uiState.value.copy(watchHistory = history)
            }
        }
    }

    fun toggleFavorite() {
        val anime = _uiState.value.anime ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                repository.removeFromFavorites(anime.id)
            } else {
                repository.addToFavorites(anime)
            }
        }
    }
}
