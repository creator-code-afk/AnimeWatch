package com.example.animewatch.data.api

/**
 * DTO-модели ответа нового API AniLiberty v1 (https://anilibria.top/api/v1/...).
 * Старый api.anilibria.tv/v3 закрыт разработчиками, поэтому используется новый домен.
 */

data class ReleaseDto(
    val id: Int,
    val type: ValueDescriptionDto?,
    val year: Int?,
    val name: ReleaseNameDto,
    val alias: String,
    val season: ValueDescriptionDto?,
    val poster: PosterDto?,
    val description: String?,
    val episodes: List<EpisodeDto>? = null
)

data class ReleaseNameDto(
    val main: String,
    val english: String?,
    val alternative: String?
)

data class ValueDescriptionDto(
    val value: String?,
    val description: String?
)

data class PosterDto(
    val preview: String?,
    val thumbnail: String?,
    val optimized: PosterOptimizedDto?
)

data class PosterOptimizedDto(
    val preview: String?,
    val thumbnail: String?
)

data class EpisodeDto(
    val id: String,             // UUID серии
    val name: String?,
    val name_english: String?,
    val ordinal: Double?,       // номер серии, может быть дробным
    val preview: PosterDto?,
    val hls_480: String?,
    val hls_720: String?,
    val hls_1080: String?,
    val duration: Int?,
    val release_id: Int?
)

/**
 * Ответ поиска (/app/search/releases) — по документации структура похожа на
 * список релизов. Если формат окажется другим (например, обёрнутым в "data"),
 * поправить парсинг здесь.
 */
typealias ReleaseListDto = List<ReleaseDto>
