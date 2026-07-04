package com.example.animewatch.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit-интерфейс для работы с публичным AniLibria API v3.
 * Базовый URL задаётся в RetrofitClient: https://api.anilibria.tv/v3/
 */
interface AniLibriaService {

    // Список последних обновлённых релизов — используется для главной карусели "Обновления"
    @GET("title/updates")
    suspend fun getUpdates(
        @Query("limit") limit: Int = 20
    ): TitleListResponse

    // Поиск аниме по названию
    @GET("title/search")
    suspend fun searchTitles(
        @Query("search") query: String,
        @Query("limit") limit: Int = 30
    ): TitleListResponse

    // Получение одного релиза по id (для экрана деталей)
    @GET("title")
    suspend fun getTitleById(
        @Query("id") id: Int
    ): TitleDto

    // Список популярных / случайных релизов — вторая карусель на главном экране
    @GET("title/random")
    suspend fun getRandomTitles(
        @Query("limit") limit: Int = 15
    ): TitleListResponse
}
