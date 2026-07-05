package com.example.animewatch.domain.models

/**
 * Доменная модель серии аниме.
 * episodeNumber — номер серии, приведённый к целому числу для совместимости
 * с навигацией и локальной БД (в API AniLiberty номер серии — дробное число,
 * так как встречаются "серии" вроде пролога с номером 12.5; для таких случаев
 * точный дробный номер хранится в ordinalRaw, а в name обычно есть понятное название).
 * qualityLinks — карта "качество -> ссылка на HLS-поток" (480p/720p/1080p),
 * которая в интерфейсе плеера используется как выбор "озвучки/качества".
 */
data class Episode(
    val animeId: Int,
    val episodeId: String,       // UUID серии в новом API
    val episodeNumber: Int,      // номер серии, округлённый до целого
    val ordinalRaw: Double,      // точный номер серии из API (может быть дробным)
    val name: String?,
    val previewUrl: String?,
    val qualityLinks: Map<String, String> // "480p" -> url, "720p" -> url, "1080p" -> url
)
