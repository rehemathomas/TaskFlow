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
import com.example.taskflow.viewmodel.TaskViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Tasks,
        modifier = modifier
    ) {
        composable(route = Route.Tasks) {
            TaskListScreen(
                viewModel = viewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Route.taskDetail(taskId))
                },
                onAddClick = {
                    navController.navigate(Route.Add)
                }
            )
        }

        composable(route = Route.Add) {
            AddTaskScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTaskSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Route.EditTaskWithArgs,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            AddTaskScreen(
                viewModel = viewModel,
                editTaskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTaskSaved = {
                    navController.popBackStack()
                }
            )
        }

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
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditTask = { id ->
                    navController.navigate(Route.editTask(id))
                }
            )
        }

        composable(route = Route.Calendar) {
            CalendarScreen(
                viewModel = viewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Route.taskDetail(taskId))
                }
            )
        }

        composable(route = Route.Stats) {
            StatsScreen(viewModel = viewModel)
        }

        composable(route = Route.Settings) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}
