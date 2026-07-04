package com.example.animewatch

import android.app.Application
import com.example.animewatch.di.AppContainer

/**
 * Класс приложения — хранит единственный экземпляр AppContainer,
 * доступный из любого места (например, во ViewModelFactory).
 */
class AnimeApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
