package com.example.taskflow

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import com.example.taskflow.data.database.AppDatabase

/**
 * Application class for Task Flow
 * Initializes app-wide dependencies and notification channels
 */
class TaskFlowApplication : Application() {

    // Lazy initialization of database
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Create notification channels
        createNotificationChannels()
    }

    /**
     * Create notification channels for different priority levels
     * Required for Android 8.0 (API level 26) and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService<NotificationManager>()

            // High priority channel
            val highChannel = NotificationChannel(
                "task_high_priority",
                "High Priority Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for high priority tasks"
                enableVibration(true)
            }

            // Medium priority channel
            val mediumChannel = NotificationChannel(
                "task_medium_priority",
                "Medium Priority Tasks",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for medium priority tasks"
            }

            // Low priority channel
            val lowChannel = NotificationChannel(
                "task_low_priority",
                "Low Priority Tasks",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for low priority tasks"
            }

            notificationManager?.createNotificationChannels(
                listOf(highChannel, mediumChannel, lowChannel)
            )
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        // Clear memory cache when memory is low
        if (level >= TRIM_MEMORY_MODERATE) {
            // TODO: Clear caches
        }
    }
}
