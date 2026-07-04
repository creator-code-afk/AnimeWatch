package com.example.animewatch.data.api

import com.google.gson.annotations.SerializedName

/**
 * DTO-модели ответа AniLibria API v3 (https://api.anilibria.tv/v3/...).
 * Названия полей соответствуют официальному JSON-формату API.
 */

data class TitleListResponse(
    val list: List<TitleDto> = emptyList()
)

data class TitleDto(
    val id: Int,
    val names: NamesDto,
    val description: String?,
    val posters: PostersDto,
    val status: StatusDto?,
    val type: TypeDto?,
    val genres: List<String>? = null,
    val team: TeamDto?,
    val season: SeasonDto?,
    val player: PlayerDto?
)

data class NamesDto(
    val ru: String,
    val en: String?
)

data class PostersDto(
    val original: PosterUrlDto?,
    val medium: PosterUrlDto?,
    val small: PosterUrlDto?
)

data class PosterUrlDto(
    val url: String?
)

data class StatusDto(
    val string: String?,
    val code: Int?
)

data class TypeDto(
    @SerializedName("full_string") val fullString: String?,
    val episodes: Int?,
    val length: Int?
)

data class TeamDto(
    val voice: List<String>? = null,
    val translator: List<String>? = null
)

data class SeasonDto(
    val year: Int?
)

data class PlayerDto(
    val host: String?,
    val episodes: EpisodesRangeDto?,
    val list: Map<String, PlayerEpisodeDto>? = null
)

data class EpisodesRangeDto(
    val first: Int?,
    val last: Int?
)

data class PlayerEpisodeDto(
    val episode: Int?,
    val name: String?,
    val preview: String?,
    val hls: HlsDto?
)

data class HlsDto(
    val fhd: String?,
    val hd: String?,
    val sd: String?
)
