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

/**
 * Main activity for Task Flow application
 * Sets up the app with Material 3 theme and navigation
 */
class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize preferences manager
        preferencesManager = PreferencesManager(applicationContext)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        setContent {
            // Collect dark mode preference
            val darkMode by preferencesManager.darkMode.collectAsState(initial = false)

            TaskFlowTheme(darkTheme = darkMode) {
                TaskFlowApp()
            }
        }
    }
}
