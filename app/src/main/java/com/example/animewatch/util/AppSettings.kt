package com.example.animewatch.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Простое хранилище пользовательских настроек приложения (SharedPreferences).
 * Не связано с Room — это только настройки внешнего вида и поведения плеера,
 * а не пользовательские данные (избранное, история), поэтому отдельный простой механизм.
 */
object AppSettings {

    private const val PREFS_NAME = "animewatch_settings"
    private const val KEY_ACCENT_COLOR = "accent_color"
    private const val KEY_DEFAULT_QUALITY = "default_quality"

    private lateinit var prefs: SharedPreferences

    private val _accentColorKey = MutableStateFlow("purple")
    val accentColorKey: StateFlow<String> = _accentColorKey

    private val _defaultQuality = MutableStateFlow("720p")
    val defaultQuality: StateFlow<String> = _defaultQuality

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _accentColorKey.value = prefs.getString(KEY_ACCENT_COLOR, "purple") ?: "purple"
        _defaultQuality.value = prefs.getString(KEY_DEFAULT_QUALITY, "720p") ?: "720p"
    }

    fun setAccentColor(key: String) {
        _accentColorKey.value = key
        prefs.edit().putString(KEY_ACCENT_COLOR, key).apply()
    }

    fun setDefaultQuality(quality: String) {
        _defaultQuality.value = quality
        prefs.edit().putString(KEY_DEFAULT_QUALITY, quality).apply()
    }
}
