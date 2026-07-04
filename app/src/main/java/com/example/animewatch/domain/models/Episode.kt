package com.example.animewatch.domain.models

/**
 * Доменная модель серии аниме.
 * qualityLinks — карта "качество -> ссылка на HLS-поток" (sd/hd/fhd),
 * которая в интерфейсе плеера используется как выбор "озвучки/качества".
 */
data class Episode(
    val animeId: Int,
    val episodeNumber: Int,
    val name: String?,
    val previewUrl: String?,
    val qualityLinks: Map<String, String> // "sd" -> url, "hd" -> url, "fhd" -> url
)
