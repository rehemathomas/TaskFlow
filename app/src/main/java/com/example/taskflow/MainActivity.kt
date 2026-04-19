package com.example.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.taskflow.data.database.AppDatabase
import com.example.taskflow.data.preferences.PreferencesManager
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.ui.TaskFlowApp
import com.example.taskflow.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(applicationContext)

        val database = AppDatabase.getInstance(applicationContext)
        taskRepository = TaskRepository(
            taskDao = database.taskDao(),
            subtaskDao = database.subtaskDao(),
            tagDao = database.tagDao(),
            reminderDao = database.reminderDao(),
            searchHistoryDao = database.searchHistoryDao()
        )

        enableEdgeToEdge()

        setContent {
            val darkMode by preferencesManager.darkMode.collectAsState(initial = false)

            TaskFlowTheme(darkTheme = darkMode) {
                TaskFlowApp(repository = taskRepository)
            }
        }
    }
}
