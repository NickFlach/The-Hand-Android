package com.thehand.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thehand.android.ui.screens.*

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    data object Ledger : Screen("ledger", "Ledger", { Icon(Icons.Default.Book, "Ledger") })
    data object Archive : Screen("archive", "Archive", { Icon(Icons.Default.Archive, "Archive") })
    data object Patterns : Screen("patterns", "Patterns", { Icon(Icons.Default.BarChart, "Patterns") })
    data object Threads : Screen("threads", "Threads", { Icon(Icons.Default.AccountTree, "Threads") })
    data object Settings : Screen("settings", "Settings", { Icon(Icons.Default.Settings, "Settings") })
}

val bottomNavItems = listOf(
    Screen.Ledger,
    Screen.Archive,
    Screen.Patterns,
    Screen.Threads,
    Screen.Settings
)

@Composable
fun TheHandApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = screen.icon,
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Ledger.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Ledger.route) { LedgerScreen(navController) }
            composable(Screen.Archive.route) { ArchiveScreen(navController) }
            composable(Screen.Patterns.route) { PatternsScreen() }
            composable(Screen.Threads.route) { ThreadsScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable("new_entry") { NewEntryScreen(navController) }
            composable("entry/{entryId}") { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull()
                if (entryId != null) {
                    EntryDetailScreen(navController, entryId)
                }
            }
        }
    }
}
