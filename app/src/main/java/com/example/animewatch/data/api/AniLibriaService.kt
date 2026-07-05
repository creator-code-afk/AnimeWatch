package com.example.animewatch.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit-интерфейс для нового API AniLiberty v1.
 * Базовый URL задаётся в RetrofitClient: https://anilibria.top/api/v1/
 * Старый api.anilibria.tv/v3 официально закрыт (deprecated), поэтому используем новый домен.
 */
interface AniLibriaService {

    // Последние обновлённые релизы — карусель "Обновления" на главном экране
    @GET("anime/releases/latest")
    suspend fun getLatest(): ReleaseListDto

    // Случайные релизы — карусель "Популярное" на главном экране
    @GET("anime/releases/random")
    suspend fun getRandom(): ReleaseListDto

    // Поиск релизов по названию
    @GET("app/search/releases")
    suspend fun searchReleases(
        @Query("query") query: String
    ): ReleaseListDto

    // Получение одного релиза по id (для экрана деталей)
    @GET("anime/releases/{idOrAlias}")
    suspend fun getReleaseById(
        @Path("idOrAlias") id: Int
    ): ReleaseDto
}
