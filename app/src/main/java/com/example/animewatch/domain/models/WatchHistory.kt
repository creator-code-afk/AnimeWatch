package com.example.animewatch.domain.models

/**
 * Статус просмотра конкретного аниме пользователем.
 */
enum class WatchStatus {
    WATCHING,   // В процессе
    COMPLETED,  // Просмотрено
    DROPPED     // Брошено
}

/**
 * Доменная модель записи истории просмотра одного аниме.
 * lastEpisode — номер последней просмотренной серии (для "продолжить просмотр").
 * totalWatchedSeconds — суммарное время просмотра в секундах (для статистики в часах).
 */
data class WatchHistory(
    val animeId: Int,
    val animeTitle: String,
    val posterUrl: String,
    val status: WatchStatus,
    val lastEpisode: Int,
    val lastPositionMs: Long,
    val totalWatchedSeconds: Long,
    val totalEpisodesWatched: Int,
    val updatedAt: Long
)
