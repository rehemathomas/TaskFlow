package com.example.taskflow.ui.navigation

/**
 * Navigation routes for all screens in the app
 * Uses object pattern for type-safety and easy access
 */
object Route {
    /**
     * Main task list screen (home screen)
     */
    const val Tasks = "tasks"

    /**
     * Add new task screen
     */
    const val Add = "add"

    /**
     * Task detail screen with task ID parameter
     * Use: "task_detail/{taskId}"
     * Navigate: navController.navigate("task_detail/$taskId")
     */
    const val TaskDetail = "task_detail"
    const val TaskDetailWithArgs = "task_detail/{taskId}"

    /**
     * Build task detail route with task ID
     * @param taskId ID of the task to view
     * @return Complete route string
     */
    fun taskDetail(taskId: Long): String = "task_detail/$taskId"

    /**
     * Calendar view screen
     */
    const val Calendar = "calendar"

    /**
     * Statistics screen
     */
    const val Stats = "stats"

    /**
     * Settings screen
     */
    const val Settings = "settings"
}
