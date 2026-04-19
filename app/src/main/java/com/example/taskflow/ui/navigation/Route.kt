package com.example.taskflow.ui.navigation

object Route {
    const val Tasks = "tasks"
    const val Add = "add"

    const val TaskDetail = "task_detail"
    const val TaskDetailWithArgs = "task_detail/{taskId}"
    fun taskDetail(taskId: Long): String = "task_detail/$taskId"

    const val EditTask = "edit_task"
    const val EditTaskWithArgs = "edit_task/{taskId}"
    fun editTask(taskId: Long): String = "edit_task/$taskId"

    const val Calendar = "calendar"
    const val Stats = "stats"
    const val Settings = "settings"
}
