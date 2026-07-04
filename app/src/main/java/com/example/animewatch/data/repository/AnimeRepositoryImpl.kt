package com.example.animewatch.data.repository

import android.content.Context
import com.example.animewatch.data.api.AniLibriaService
import com.example.animewatch.data.api.toDomain
import com.example.animewatch.data.api.toDomainList
import com.example.animewatch.data.local.AppDatabase
import com.example.animewatch.data.local.DatabaseExportImport
import com.example.animewatch.data.local.FavoriteEntity
import com.example.animewatch.data.local.WatchHistoryEntity
import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.models.WatchHistory
import com.example.animewatch.domain.models.WatchStatus
import com.example.animewatch.domain.repository.AnimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Реализация репозитория: объединяет сетевые вызовы AniLibria (Retrofit)
 * и локальное хранилище (Room). ViewModel-и работают только с этим классом
 * через интерфейс AnimeRepository, не зная про источник данных.
 */
class AnimeRepositoryImpl(
    context: Context,
    private val api: AniLibriaService
) : AnimeRepository {

    private val appContext = context.applicationContext
    private val db = AppDatabase.getInstance(appContext)
    private val favoriteDao = db.favoriteDao()
    private val historyDao = db.watchHistoryDao()

    // ---------- Сеть ----------

    override suspend fun getUpdates(): Result<List<Anime>> = withContext(Dispatchers.IO) {
        runCatching { api.getUpdates().toDomainList() }
    }

    override suspend fun getPopular(): Result<List<Anime>> = withContext(Dispatchers.IO) {
        runCatching { api.getRandomTitles().toDomainList() }
    }

    override suspend fun searchAnime(query: String): Result<List<Anime>> = withContext(Dispatchers.IO) {
        runCatching { api.searchTitles(query).toDomainList() }
    }

    override suspend fun getAnimeById(id: Int): Result<Anime> = withContext(Dispatchers.IO) {
        runCatching { api.getTitleById(id).toDomain() }
    }

    // ---------- Избранное ----------

    override fun getFavorites(): Flow<List<Anime>> =
        favoriteDao.getAll().map { list ->
            list.map { fav ->
                Anime(
                    id = fav.animeId,
                    titleRu = fav.titleRu,
                    titleEn = null,
                    description = "",
                    posterUrl = fav.posterUrl,
                    statusText = "",
                    typeText = "",
                    genres = emptyList(),
                    voiceTeams = emptyList(),
                    episodes = emptyList(),
                    year = null
                )
            }
        }

    override suspend fun addToFavorites(anime: Anime) = withContext(Dispatchers.IO) {
        favoriteDao.insert(
            FavoriteEntity(
                animeId = anime.id,
                titleRu = anime.titleRu,
                posterUrl = anime.posterUrl,
                addedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeFromFavorites(animeId: Int) = withContext(Dispatchers.IO) {
        favoriteDao.deleteById(animeId)
    }

    override fun isFavorite(animeId: Int): Flow<Boolean> = favoriteDao.isFavorite(animeId)

    // ---------- История просмотра / статистика ----------

    override fun getWatchHistory(): Flow<List<WatchHistory>> =
        historyDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getWatchHistoryFor(animeId: Int): Flow<WatchHistory?> =
        historyDao.getByAnimeId(animeId).map { it?.toDomain() }

    override suspend fun updateWatchProgress(
        anime: Anime,
        episodeNumber: Int,
        positionMs: Long,
        watchedDeltaSeconds: Long
    ) = withContext(Dispatchers.IO) {
        val existing = historyDao.getByAnimeIdOnce(anime.id)
        val isNewEpisode = existing == null || episodeNumber > existing.lastEpisode
        val entity = WatchHistoryEntity(
            animeId = anime.id,
            animeTitle = anime.titleRu,
            posterUrl = anime.posterUrl,
            status = existing?.status ?: WatchStatus.WATCHING.name,
            lastEpisode = maxOf(episodeNumber, existing?.lastEpisode ?: 0),
            lastPositionMs = positionMs,
            totalWatchedSeconds = (existing?.totalWatchedSeconds ?: 0L) + watchedDeltaSeconds,
            totalEpisodesWatched = (existing?.totalEpisodesWatched ?: 0) + if (isNewEpisode) 1 else 0,
            updatedAt = System.currentTimeMillis()
        )
        historyDao.upsert(entity)
    }

    override suspend fun setWatchStatus(animeId: Int, status: WatchStatus) = withContext(Dispatchers.IO) {
        historyDao.updateStatus(animeId, status.name, System.currentTimeMillis())
    }

    override suspend fun getTotalWatchedSeconds(): Long = withContext(Dispatchers.IO) {
        historyDao.getTotalWatchedSeconds()
    }

    override suspend fun getTotalEpisodesWatched(): Int = withContext(Dispatchers.IO) {
        historyDao.getTotalEpisodesWatched()
    }

    // ---------- Экспорт / импорт ----------

    override suspend fun exportDatabase(destinationPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        DatabaseExportImport.export(appContext, destinationPath)
    }

    override suspend fun importDatabase(sourcePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        DatabaseExportImport.import(appContext, sourcePath)
    }
}

private fun WatchHistoryEntity.toDomain(): WatchHistory = WatchHistory(
    animeId = animeId,
    animeTitle = animeTitle,
    posterUrl = posterUrl,
    status = WatchStatus.valueOf(status),
    lastEpisode = lastEpisode,
    lastPositionMs = lastPositionMs,
    totalWatchedSeconds = totalWatchedSeconds,
    totalEpisodesWatched = totalEpisodesWatched,
    updatedAt = updatedAt
)
