package com.example.animewatch.ui.theme

import androidx.compose.ui.graphics.Color

// Тёмно-фиолетовая палитра приложения (базовая, используется по умолчанию)
val AccentPurple = Color(0xFF7C4DFF)      // Акцентный цвет (кнопки, выделения)
val PrimaryDark = Color(0xFF5B2D8E)       // Тёмный фиолетовый (шапки, активные элементы)
val BackgroundDark = Color(0xFF0D0B14)    // Фон приложения
val SurfaceDark = Color(0xFF1A1625)       // Поверхности карточек
val TextPrimary = Color(0xFFE8E0F0)       // Основной текст
val TextSecondary = Color(0xFFA89BB8)     // Вторичный текст (описания, подписи)

// Дополнительные вспомогательные цвета
val ErrorRed = Color(0xFFCF6679)
val DividerColor = Color(0xFF2A2438)

/**
 * Набор доступных акцентных цветов для настройки внешнего вида приложения
 * (экран "Настройки"). Ключ используется при сохранении выбора в AppSettings.
 */
data class AccentOption(val key: String, val label: String, val color: Color, val dark: Color)

val AccentOptions = listOf(
    AccentOption("purple", "Фиолетовый", Color(0xFF7C4DFF), Color(0xFF5B2D8E)),
    AccentOption("blue", "Синий", Color(0xFF4D9FFF), Color(0xFF2D5B8E)),
    AccentOption("pink", "Розовый", Color(0xFFFF4D9F), Color(0xFF8E2D5B)),
    AccentOption("green", "Зелёный", Color(0xFF4DFF9F), Color(0xFF2D8E5B)),
    AccentOption("orange", "Оранжевый", Color(0xFFFF9F4D), Color(0xFF8E5B2D))
)

fun accentByKey(key: String): AccentOption =
    AccentOptions.find { it.key == key } ?: AccentOptions.first()
