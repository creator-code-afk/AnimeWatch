package com.example.animewatch.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<Anime> = emptyList()
)

/** ViewModel экрана избранного — подписывается на локальную таблицу favorites (Room) */
class FavoritesViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFavorites().collectLatest { list ->
                _uiState.value = FavoritesUiState(favorites = list)
            }
        }
    }

    fun removeFavorite(animeId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(animeId)
        }
    }
}
