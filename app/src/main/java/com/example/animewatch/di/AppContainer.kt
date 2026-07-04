package com.example.animewatch.di

import android.content.Context
import com.example.animewatch.data.api.RetrofitClient
import com.example.animewatch.data.repository.AnimeRepositoryImpl
import com.example.animewatch.domain.repository.AnimeRepository

/**
 * Простой контейнер ручного внедрения зависимостей (без Hilt/Dagger).
 * Создаёт единственный экземпляр репозитория на всё приложение.
 */
class AppContainer(context: Context) {
    val animeRepository: AnimeRepository by lazy {
        AnimeRepositoryImpl(context = context, api = RetrofitClient.service)
    }
}
