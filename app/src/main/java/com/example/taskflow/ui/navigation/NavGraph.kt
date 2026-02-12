package com.example.taskflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.taskflow.ui.screens.*

/**
 * Main navigation graph for Task Flow
 * Defines all navigation routes and their corresponding composables
 *
 * @param navController Navigation controller for managing navigation
 * @param modifier Modifier to be applied to the NavHost
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Tasks,
        modifier = modifier
    ) {
        // Tasks List Screen
        composable(route = Route.Tasks) {
            TaskListScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Route.taskDetail(taskId))
                },
                onAddClick = {
                    navController.navigate(Route.Add)
                }
            )
        }

        // Add Task Screen
        composable(route = Route.Add) {
            AddTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTaskSaved = {
                    navController.popBackStack()
                }
            )
        }

        // Task Detail Screen with arguments
        composable(
            route = Route.TaskDetailWithArgs,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "taskflow://task/{taskId}"
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditTask = {
                    // Navigate to edit mode (same screen, different state)
                }
            )
        }

        // Calendar Screen
        composable(route = Route.Calendar) {
            CalendarScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Route.taskDetail(taskId))
                }
            )
        }

        // Statistics Screen
        composable(route = Route.Stats) {
            StatsScreen()
        }

        // Settings Screen
        composable(route = Route.Settings) {
            SettingsScreen()
        }
    }
}
