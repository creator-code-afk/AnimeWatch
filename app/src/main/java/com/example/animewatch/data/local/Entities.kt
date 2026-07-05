package com.example.animewatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room-сущность избранного аниме.
 * Храним минимально необходимые поля для отображения в списке избранного офлайн.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val animeId: Int,
    val titleRu: String,
    val posterUrl: String,
    val addedAt: Long
)

/**
 * Room-сущность истории просмотра.
 * Одна запись на одно аниме — обновляется по мере просмотра серий.
 */
@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val animeId: Int,
    val animeTitle: String,
    val posterUrl: String,
    val status: String,          // хранится как строка enum WatchStatus
    val lastEpisode: Int,
    val lastPositionMs: Long,
    val totalWatchedSeconds: Long,
    val totalEpisodesWatched: Int,
    val updatedAt: Long
)
