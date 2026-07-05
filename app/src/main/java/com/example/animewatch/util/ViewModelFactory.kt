package com.example.animewatch.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.animewatch.domain.repository.AnimeRepository
import com.example.animewatch.ui.screens.catalog.CatalogViewModel
import com.example.animewatch.ui.screens.detail.DetailViewModel
import com.example.animewatch.ui.screens.favorites.FavoritesViewModel
import com.example.animewatch.ui.screens.home.HomeViewModel
import com.example.animewatch.ui.screens.player.PlayerViewModel
import com.example.animewatch.ui.screens.search.SearchViewModel
import com.example.animewatch.ui.screens.settings.SettingsViewModel
import com.example.animewatch.ui.screens.stats.StatsViewModel

/**
 * Общая фабрика ViewModel-ей — передаёт репозиторий в конструктор каждой ViewModel.
 * Используется вместо Hilt, т.к. DI-фреймворк не входит в требования задачи.
 */
class ViewModelFactory(private val repository: AnimeRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(repository) as T
            DetailViewModel::class.java -> DetailViewModel(repository) as T
            PlayerViewModel::class.java -> PlayerViewModel(repository) as T
            StatsViewModel::class.java -> StatsViewModel(repository) as T
            FavoritesViewModel::class.java -> FavoritesViewModel(repository) as T
            SearchViewModel::class.java -> SearchViewModel(repository) as T
            SettingsViewModel::class.java -> SettingsViewModel(repository) as T
            CatalogViewModel::class.java -> CatalogViewModel(repository) as T
            else -> throw IllegalArgumentException("Неизвестный класс ViewModel: ${modelClass.name}")
        }
    }
}
