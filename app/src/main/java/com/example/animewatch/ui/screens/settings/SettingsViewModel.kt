package com.example.animewatch.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animewatch.domain.repository.AnimeRepository
import com.example.animewatch.util.AppSettings
import kotlinx.coroutines.launch

/** ViewModel экрана настроек — управляет темой, качеством видео и сбросом данных */
class SettingsViewModel(private val repository: AnimeRepository) : ViewModel() {

    val accentColorKey = AppSettings.accentColorKey
    val defaultQuality = AppSettings.defaultQuality

    fun setAccentColor(key: String) {
        AppSettings.setAccentColor(key)
    }

    fun setDefaultQuality(quality: String) {
        AppSettings.setDefaultQuality(quality)
    }

    fun clearAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllData()
            onDone()
        }
    }
}
