package com.example.animewatch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {

    @Query("SELECT * FROM watch_history ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE animeId = :animeId LIMIT 1")
    fun getByAnimeId(animeId: Int): Flow<WatchHistoryEntity?>

    @Query("SELECT * FROM watch_history WHERE animeId = :animeId LIMIT 1")
    suspend fun getByAnimeIdOnce(animeId: Int): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WatchHistoryEntity)

    @Query("UPDATE watch_history SET status = :status, updatedAt = :updatedAt WHERE animeId = :animeId")
    suspend fun updateStatus(animeId: Int, status: String, updatedAt: Long)

    @Query("SELECT COALESCE(SUM(totalWatchedSeconds), 0) FROM watch_history")
    suspend fun getTotalWatchedSeconds(): Long

    @Query("SELECT COALESCE(SUM(totalEpisodesWatched), 0) FROM watch_history")
    suspend fun getTotalEpisodesWatched(): Int
}
