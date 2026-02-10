package com.example.taskflow.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskflow.data.dao.*
import com.example.taskflow.data.entity.*

/**
 * Room database for Task Flow application
 *
 * This is the main database class that serves as the central access point
 * for the underlying SQLite database. It includes all entities and provides
 * access to all DAOs.
 *
 * @property taskDao Access to task-related database operations
 * @property subtaskDao Access to subtask-related database operations
 * @property tagDao Access to tag-related database operations
 * @property reminderDao Access to reminder-related database operations
 * @property searchHistoryDao Access to search history database operations
 */
@Database(
    entities = [
        Task::class,
        Subtask::class,
        Tag::class,
        TaskTagCrossRef::class,
        Reminder::class,
        SearchHistory::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun tagDao(): TagDao
    abstract fun reminderDao(): ReminderDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "task_flow_database"

        /**
         * Get singleton instance of the database
         * Uses double-checked locking pattern for thread safety
         *
         * @param context Application context
         * @return Database instance
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // For development; use proper migrations in production
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Clear database instance (useful for testing)
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
