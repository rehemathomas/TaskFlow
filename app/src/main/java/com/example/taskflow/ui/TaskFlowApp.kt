package com.example.taskflow.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskflow.ui.navigation.NavGraph
import com.example.taskflow.ui.navigation.Route

/**
 * Main app composable that provides the navigation structure
 * Includes bottom navigation bar for top-level destinations
 */
@Composable
fun TaskFlowApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if bottom bar should be shown
    val showBottomBar = currentDestination?.route in listOf(
        Route.Tasks,
        Route.Add,
        Route.Calendar,
        Route.Stats,
        Route.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to start destination
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies
                                    launchSingleTop = true
                                    // Restore state
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Bottom navigation item data class
 */
private data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

/**
 * List of bottom navigation items
 */
private val bottomNavItems = listOf(
    BottomNavItem(
        route = Route.Tasks,
        icon = Icons.Default.List,
        label = "Tasks"
    ),
    BottomNavItem(
        route = Route.Add,
        icon = Icons.Default.Add,
        label = "Add"
    ),
    BottomNavItem(
        route = Route.Calendar,
        icon = Icons.Default.CalendarMonth,
        label = "Calendar"
    ),
    BottomNavItem(
        route = Route.Stats,
        icon = Icons.Default.ShowChart,
        label = "Stats"
    ),
    BottomNavItem(
        route = Route.Settings,
        icon = Icons.Default.Settings,
        label = "Settings"
    )
)
