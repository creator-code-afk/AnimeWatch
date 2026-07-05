package com.example.animewatch.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animewatch.domain.models.WatchStatus
import com.example.animewatch.ui.theme.BackgroundDark
import com.example.animewatch.ui.theme.SurfaceDark
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экран статистики: суммарное время просмотра, количество серий,
 * распределение по статусам, а также кнопки экспорта/импорта базы данных.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Статистика") },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Часов просмотрено",
                        value = String.format(Locale.getDefault(), "%.1f", uiState.totalWatchedHours),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Серий просмотрено",
                        value = uiState.totalEpisodesWatched.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(title = "В процессе", value = uiState.watchingCount.toString(), modifier = Modifier.weight(1f))
                    StatCard(title = "Просмотрено", value = uiState.completedCount.toString(), modifier = Modifier.weight(1f))
                    StatCard(title = "Брошено", value = uiState.droppedCount.toString(), modifier = Modifier.weight(1f))
                }
            }

            item {
                Text(text = "Экспорт / импорт данных", style = MaterialTheme.typography.titleLarge)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            val exportDir = File(context.getExternalFilesDir(null), "export")
                            val fileName = "animewatch_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.db"
                            val destination = File(exportDir, fileName).absolutePath
                            viewModel.exportDatabase(destination) { success, error ->
                                // В реальном приложении здесь стоит показать Snackbar/Toast с результатом
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Экспорт БД")
                    }
                    Button(
                        onClick = {
                            // Импорт файла из каталога export текущего приложения (для простоты; в продакшене — через SAF)
                            val importFile = File(context.getExternalFilesDir(null), "export")
                                .listFiles()?.maxByOrNull { it.lastModified() }
                            importFile?.let {
                                viewModel.importDatabase(it.absolutePath) { success, error -> }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Импорт БД")
                    }
                }
            }

            item {
                Text(text = "История просмотра", style = MaterialTheme.typography.titleLarge)
            }
            items(uiState.history, key = { it.animeId }) { history ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = history.animeTitle, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Серия ${history.lastEpisode} • ${statusLabel(history.status)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            StatusButton("В процессе") { viewModel.setStatus(history.animeId, WatchStatus.WATCHING) }
                            StatusButton("Просмотрено") { viewModel.setStatus(history.animeId, WatchStatus.COMPLETED) }
                            StatusButton("Брошено") { viewModel.setStatus(history.animeId, WatchStatus.DROPPED) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
            Text(text = title, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StatusButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall)
    }
}

private fun statusLabel(status: WatchStatus): String = when (status) {
    WatchStatus.WATCHING -> "В процессе"
    WatchStatus.COMPLETED -> "Просмотрено"
    WatchStatus.DROPPED -> "Брошено"
}
