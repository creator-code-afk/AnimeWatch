package com.example.animewatch.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Единая точка создания Retrofit-клиента для AniLibria API.
 * Базовый URL и таймауты настроены здесь, чтобы не дублировать конфигурацию.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.anilibria.tv/v3/"

    // Базовый хост, к которому нужно добавлять относительные ссылки на постеры от AniLibria
    const val IMAGE_HOST = "https://anilibria.tv"

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
