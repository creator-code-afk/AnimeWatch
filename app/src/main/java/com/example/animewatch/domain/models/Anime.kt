package com.example.animewatch.domain.models

/**
 * Доменная модель аниме-релиза.
 * Заполняется на основе ответа AniLibria API и используется во всём UI-слое,
 * не зависящем от деталей сети или базы данных.
 */
data class Anime(
    val id: Int,
    val titleRu: String,
    val titleEn: String?,
    val description: String,
    val posterUrl: String,
    val statusText: String,       // например "В работе" / "Завершён"
    val typeText: String,         // например "TV, 12 эпизодов"
    val genres: List<String>,
    val voiceTeams: List<String>, // список команд озвучки (озвучившие релиз)
    val episodes: List<Episode>,
    val year: Int?
)
