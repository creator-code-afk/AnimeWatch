package com.example.animewatch.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.models.WatchHistory
import com.example.animewatch.domain.models.WatchStatus
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class StatsUiState(
    val totalWatchedHours: Double = 0.0,
    val totalEpisodesWatched: Int = 0,
    val watchingCount: Int = 0,
    val completedCount: Int = 0,
    val droppedCount: Int = 0,
    val history: List<WatchHistory> = emptyList()
)

/** ViewModel экрана статистики — считает суммарное время просмотра и количество серий */
class StatsViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.getWatchHistory().collectLatest { history ->
                val totalSeconds = history.sumOf { it.totalWatchedSeconds }
                _uiState.value = StatsUiState(
                    totalWatchedHours = totalSeconds / 3600.0,
                    totalEpisodesWatched = history.sumOf { it.totalEpisodesWatched },
                    watchingCount = history.count { it.status == WatchStatus.WATCHING },
                    completedCount = history.count { it.status == WatchStatus.COMPLETED },
                    droppedCount = history.count { it.status == WatchStatus.DROPPED },
                    history = history
                )
            }
        }
    }

    fun setStatus(animeId: Int, status: WatchStatus) {
        viewModelScope.launch {
            repository.setWatchStatus(animeId, status)
        }
    }

    fun exportDatabase(destinationPath: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.exportDatabase(destinationPath)
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }

    fun importDatabase(sourcePath: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.importDatabase(sourcePath)
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }
}
