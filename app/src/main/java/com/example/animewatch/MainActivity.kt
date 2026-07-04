package com.example.animewatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.animewatch.ui.navigation.AnimeNavGraph
import com.example.animewatch.ui.theme.AnimeWatchTheme

/**
 * Единственная Activity приложения (архитектура single-activity + Compose Navigation).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as AnimeApp).container.animeRepository

        setContent {
            AnimeWatchTheme {
                AnimeNavGraph(repository = repository)
            }
        }
    }
}
