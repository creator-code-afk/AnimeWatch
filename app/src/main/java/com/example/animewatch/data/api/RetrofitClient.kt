package com.example.animewatch.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Единая точка создания Retrofit-клиента для AniLiberty API v1.
 * Базовый URL и таймауты настроены здесь, чтобы не дублировать конфигурацию.
 */
object RetrofitClient {

    // Новый актуальный домен API (старый api.anilibria.tv/v3 закрыт разработчиками)
    private const val BASE_URL = "https://anilibria.top/api/v1/"

    // Хост для картинок (постеры, превью) — относительные пути из API дополняются этим хостом
    const val IMAGE_HOST = "https://anilibria.top"

    val service: AniLibriaService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AniLibriaService::class.java)
    }
}
