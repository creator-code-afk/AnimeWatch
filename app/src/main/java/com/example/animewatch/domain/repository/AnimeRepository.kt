package com.example.animewatch.domain.repository

import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.models.WatchHistory
import com.example.animewatch.domain.models.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * Контракт репозитория — единая точка доступа к данным аниме и локальной статистике.
 * Реализация (AnimeRepositoryImpl) прячет детали Retrofit/Room от ViewModel.
 */
interface AnimeRepository {

    // --- Сетевые операции (AniLibria) ---
    suspend fun getUpdates(): Result<List<Anime>>
    suspend fun getPopular(): Result<List<Anime>>
    suspend fun searchAnime(query: String): Result<List<Anime>>
    suspend fun getAnimeById(id: Int): Result<Anime>

    // --- Избранное ---
    fun getFavorites(): Flow<List<Anime>>
    suspend fun addToFavorites(anime: Anime)
    suspend fun removeFromFavorites(animeId: Int)
    fun isFavorite(animeId: Int): Flow<Boolean>

    // --- История просмотра / статистика ---
    fun getWatchHistory(): Flow<List<WatchHistory>>
    fun getWatchHistoryFor(animeId: Int): Flow<WatchHistory?>
    suspend fun updateWatchProgress(
        anime: Anime,
        episodeNumber: Int,
        positionMs: Long,
        watchedDeltaSeconds: Long
    )
    suspend fun setWatchStatus(animeId: Int, status: WatchStatus)
    suspend fun getTotalWatchedSeconds(): Long
    suspend fun getTotalEpisodesWatched(): Int

    // --- Экспорт / импорт базы данных ---
    suspend fun exportDatabase(destinationPath: String): Result<Unit>
    suspend fun importDatabase(sourcePath: String): Result<Unit>

    // --- Сброс данных (для экрана "Настройки") ---
    suspend fun clearAllData()
}
