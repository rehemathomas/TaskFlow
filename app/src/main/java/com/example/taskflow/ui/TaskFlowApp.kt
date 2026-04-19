package com.example.taskflow.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.ui.navigation.NavGraph
import com.example.taskflow.ui.navigation.Route
import com.example.taskflow.viewmodel.TaskViewModel
import com.example.taskflow.viewmodel.TaskViewModelFactory

@Composable
fun TaskFlowApp(repository: TaskRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(repository)
    )

    val overdueCount by viewModel.overdueTaskCount.collectAsState()

    val showBottomBar = currentDestination?.route in listOf(
        Route.Tasks,
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
                                BadgedBox(
                                    badge = {
                                        if (item.route == Route.Tasks && overdueCount > 0) {
                                            Badge { Text(overdueCount.toString()) }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                }
                            },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
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
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Route.Tasks,
        icon = Icons.Default.List,
        label = "Tasks"
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
