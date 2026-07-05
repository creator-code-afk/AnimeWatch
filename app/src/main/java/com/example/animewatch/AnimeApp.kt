package com.example.animewatch

import android.app.Application
import com.example.animewatch.di.AppContainer
import com.example.animewatch.util.AppSettings

/**
 * Класс приложения — хранит единственный экземпляр AppContainer,
 * доступный из любого места (например, во ViewModelFactory), и инициализирует
 * хранилище пользовательских настроек (тема, качество видео по умолчанию).
 */
class AnimeApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        AppSettings.init(this)
        container = AppContainer(this)
    }
}
