package com.example.animewatch.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogUiState(
    val isLoading: Boolean = true,
    val items: List<Anime> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel экрана "Каталог". Пока собирает подборку из уже доступных
 * методов API (последние + случайные релизы) — полноценный каталог с
 * фильтром по жанрам появится, когда будет подключён отдельный эндпоинт жанров.
 */
class CatalogViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val latest = repository.getUpdates().getOrDefault(emptyList())
            val random = repository.getPopular().getOrDefault(emptyList())
            val combined = (latest + random).distinctBy { it.id }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                items = combined,
                errorMessage = if (latest.isEmpty() && random.isEmpty()) "Не удалось загрузить каталог" else null
            )
        }
    }
}
