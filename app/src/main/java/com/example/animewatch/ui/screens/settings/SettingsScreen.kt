package com.example.animewatch.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.animewatch.ui.theme.AccentOptions
import com.example.animewatch.ui.theme.BackgroundDark
import com.example.animewatch.ui.theme.SurfaceDark

/**
 * Экран настроек: выбор акцентного цвета приложения, качества видео по умолчанию
 * и сброс локальных данных (избранное, история просмотра).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val accentKey by viewModel.accentColorKey.collectAsState()
    val quality by viewModel.defaultQuality.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var showClearedMessage by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Акцентный цвет", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Влияет на кнопки, выделения и нижнюю навигацию",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AccentOptions.forEach { option ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(option.color)
                            .clickable { viewModel.setAccentColor(option.key) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (option.key == accentKey) {
                            Icon(Icons.Default.Check, contentDescription = option.label, tint = Color.White)
                        }
                    }
                }
            }

            Text(
                text = "Качество видео по умолчанию",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
            )
            listOf("480p", "720p", "1080p").forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setDefaultQuality(option) }
                        .padding(vertical = 6.dp)
                ) {
                    RadioButton(
                        selected = quality == option,
                        onClick = { viewModel.setDefaultQuality(option) }
                    )
                    Text(text = option, modifier = Modifier.padding(start = 4.dp))
                }
            }

            Text(
                text = "Данные",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (showClearedMessage) "Данные очищены" else "Удалить избранное и историю просмотра",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Очистить данные")
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить данные?") },
            text = { Text("Избранное и вся история просмотра будут удалены без возможности восстановления.") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllData { showClearedMessage = true }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Удалить") }
            },
            dismissButton = {
                Button(onClick = { showClearDialog = false }) { Text("Отмена") }
            }
        )
    }
}
