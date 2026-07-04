package com.example.animewatch.data.api

import com.example.animewatch.domain.models.Anime
import com.example.animewatch.domain.models.Episode

/**
 * Преобразование сетевых DTO AniLibria в доменные модели приложения.
 */
fun TitleDto.toDomain(): Anime {
    val posterPath = posters.original?.url ?: posters.medium?.url ?: posters.small?.url ?: ""
    val posterUrl = if (posterPath.startsWith("http")) posterPath else RetrofitClient.IMAGE_HOST + posterPath

    val episodesList = player?.list?.values
        ?.mapNotNull { ep ->
            val num = ep.episode ?: return@mapNotNull null
            val links = buildMap {
                ep.hls?.sd?.let { put("SD", fixHlsUrl(it, player.host)) }
                ep.hls?.hd?.let { put("HD", fixHlsUrl(it, player.host)) }
                ep.hls?.fhd?.let { put("FHD", fixHlsUrl(it, player.host)) }
            }
            Episode(
                animeId = id,
                episodeNumber = num,
                name = ep.name,
                previewUrl = ep.preview?.let { if (it.startsWith("http")) it else RetrofitClient.IMAGE_HOST + it },
                qualityLinks = links
            )
        }
        ?.sortedBy { it.episodeNumber }
        ?: emptyList()

    return Anime(
        id = id,
        titleRu = names.ru,
        titleEn = names.en,
        description = description.orEmpty(),
        posterUrl = posterUrl,
        statusText = status?.string ?: "Неизвестно",
        typeText = type?.fullString ?: "",
        genres = genres.orEmpty(),
        voiceTeams = team?.voice.orEmpty(),
        episodes = episodesList,
        year = season?.year
    )
}

// Ссылки на HLS могут приходить относительными — дополняем хостом стрим-сервера
private fun fixHlsUrl(url: String, host: String?): String {
    if (url.startsWith("http")) return url
    val h = host ?: "cache.libria.fun"
    return "https://$h$url"
}

fun TitleListResponse.toDomainList(): List<Anime> = list.map { it.toDomain() }
