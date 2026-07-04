package com.example.animewatch.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<Anime> = emptyList(),
    val errorMessage: String? = null
)

/** ViewModel экрана поиска — ищет аниме по названию через AniLibria API */
class SearchViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.searchAnime(query)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                results = result.getOrDefault(emptyList()),
                errorMessage = result.exceptionOrNull()?.let { "Ошибка поиска. Попробуйте ещё раз." }
            )
        }
    }
}
