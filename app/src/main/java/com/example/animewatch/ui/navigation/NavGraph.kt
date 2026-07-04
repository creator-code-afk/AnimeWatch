package com.example.animewatch.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.animewatch.domain.repository.AnimeRepository
import com.example.animewatch.ui.screens.detail.DetailScreen
import com.example.animewatch.ui.screens.detail.DetailViewModel
import com.example.animewatch.ui.screens.favorites.FavoritesScreen
import com.example.animewatch.ui.screens.favorites.FavoritesViewModel
import com.example.animewatch.ui.screens.home.HomeScreen
import com.example.animewatch.ui.screens.home.HomeViewModel
import com.example.animewatch.ui.screens.player.PlayerScreen
import com.example.animewatch.ui.screens.player.PlayerViewModel
import com.example.animewatch.ui.screens.search.SearchScreen
import com.example.animewatch.ui.screens.search.SearchViewModel
import com.example.animewatch.ui.screens.stats.StatsScreen
import com.example.animewatch.ui.screens.stats.StatsViewModel
import com.example.animewatch.ui.theme.AccentPurple
import com.example.animewatch.ui.theme.BackgroundDark
import com.example.animewatch.ui.theme.SurfaceDark
import com.example.animewatch.util.ViewModelFactory

/** Маршруты навигации приложения */
object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val STATS = "stats"
    const val DETAIL = "detail/{animeId}"
    const val PLAYER = "player/{animeId}/{episodeNumber}"

    fun detail(animeId: Int) = "detail/$animeId"
    fun player(animeId: Int, episodeNumber: Int) = "player/$animeId/$episodeNumber"
}

// Экраны, на которых показывается нижняя навигационная панель
private val bottomBarRoutes = setOf(Routes.HOME, Routes.FAVORITES, Routes.STATS)

@Composable
fun AnimeNavGraph(repository: AnimeRepository) {
    val navController = rememberNavController()
    val factory = ViewModelFactory(repository)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                NavigationBar(containerColor = SurfaceDark) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = { navController.navigateSingleTop(Routes.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
                        label = { Text("Главная") },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPurple,
                            indicatorColor = BackgroundDark
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.FAVORITES,
                        onClick = { navController.navigateSingleTop(Routes.FAVORITES) },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                        label = { Text("Избранное") },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPurple,
                            indicatorColor = BackgroundDark
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.STATS,
                        onClick = { navController.navigateSingleTop(Routes.STATS) },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Статистика") },
                        label = { Text("Статистика") },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPurple,
                            indicatorColor = BackgroundDark
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Routes.HOME) {
                val vm: HomeViewModel = viewModel(factory = factory)
                HomeScreen(
                    viewModel = vm,
                    onAnimeClick = { anime -> navController.navigate(Routes.detail(anime.id)) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) }
                )
            }
            composable(Routes.SEARCH) {
                val vm: SearchViewModel = viewModel(factory = factory)
                SearchScreen(
                    viewModel = vm,
                    onAnimeClick = { anime -> navController.navigate(Routes.detail(anime.id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.FAVORITES) {
                val vm: FavoritesViewModel = viewModel(factory = factory)
                FavoritesScreen(
                    viewModel = vm,
                    onAnimeClick = { anime -> navController.navigate(Routes.detail(anime.id)) }
                )
            }
            composable(Routes.STATS) {
                val vm: StatsViewModel = viewModel(factory = factory)
                StatsScreen(viewModel = vm)
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("animeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val animeId = backStackEntry.arguments?.getInt("animeId") ?: return@composable
                val vm: DetailViewModel = viewModel(factory = factory)
                DetailScreen(
                    animeId = animeId,
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onEpisodeClick = { id, episode -> navController.navigate(Routes.player(id, episode)) }
                )
            }
            composable(
                route = Routes.PLAYER,
                arguments = listOf(
                    navArgument("animeId") { type = NavType.IntType },
                    navArgument("episodeNumber") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val animeId = backStackEntry.arguments?.getInt("animeId") ?: return@composable
                val episodeNumber = backStackEntry.arguments?.getInt("episodeNumber") ?: 1
                val vm: PlayerViewModel = viewModel(factory = factory)
                PlayerScreen(
                    animeId = animeId,
                    episodeNumber = episodeNumber,
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Переход на элемент нижней навигации с очисткой промежуточного стека (без дублирования экранов)
private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
