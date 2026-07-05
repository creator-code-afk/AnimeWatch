package com.example.animewatch.data.api

import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.models.Episode

/**
 * Преобразование сетевых DTO AniLiberty v1 в доменные модели приложения.
 */
fun ReleaseDto.toDomain(): Anime {
    val posterUrl = fixUrl(poster?.original() ?: "")

    val episodesList = episodes
        ?.mapNotNull { ep ->
            val ordinal = ep.ordinal ?: return@mapNotNull null
            val links = buildMap {
                ep.hls_480?.let { put("480p", fixUrl(it)) }
                ep.hls_720?.let { put("720p", fixUrl(it)) }
                ep.hls_1080?.let { put("1080p", fixUrl(it)) }
            }
            Episode(
                animeId = id,
                episodeId = ep.id,
                episodeNumber = Math.round(ordinal).toInt(),
                ordinalRaw = ordinal,
                name = ep.name ?: ep.name_english,
                previewUrl = ep.preview?.original()?.let { fixUrl(it) },
                qualityLinks = links
            )
        }
        ?.sortedBy { it.ordinalRaw }
        ?: emptyList()

    return Anime(
        id = id,
        titleRu = name.main,
        titleEn = name.english,
        description = description.orEmpty(),
        posterUrl = posterUrl,
        statusText = "", // в новом API отдельного статуса "в работе/завершён" не пришло в примере ответа
        typeText = type?.description ?: type?.value.orEmpty(),
        genres = emptyList(), // жанры временно не парсятся — формат поля не подтверждён точно
        voiceTeams = emptyList(), // список участников приходит через отдельный эндпоинт /members
        episodes = episodesList,
        year = year
    )
}

fun ReleaseListDto.toDomainList(): List<Anime> = this.map { it.toDomain() }

// Берём лучшее доступное изображение из объекта постера/превью
private fun PosterDto.original(): String? =
    optimized?.preview ?: preview ?: optimized?.thumbnail ?: thumbnail

// Относительные пути из API дополняются хостом картинок/видео
private fun fixUrl(url: String): String {
    if (url.isBlank()) return ""
    return if (url.startsWith("http")) url else RetrofitClient.IMAGE_HOST + url
}
