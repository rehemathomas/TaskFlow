package com.example.taskflow.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity representing a task in the database
 * Indexes are added for frequently queried columns to optimize performance
 *
 * @property id Unique identifier for the task
 * @property title Task title (required)
 * @property description Optional detailed description
 * @property priority Task priority level (HIGH, MEDIUM, LOW)
 * @property dueDate Optional due date
 * @property dueTime Optional due time (stored as string HH:mm)
 * @property category Optional category for grouping tasks
 * @property isCompleted Completion status
 * @property createdAt Timestamp when task was created
 * @property updatedAt Timestamp when task was last updated
 * @property completedAt Timestamp when task was marked complete
 * @property recurrencePattern Optional recurrence pattern (DAILY, WEEKLY, MONTHLY)
 * @property recurrenceInterval Interval for recurrence (e.g., every 2 days)
 */
@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["isCompleted"]),
        Index(value = ["dueDate"]),
        Index(value = ["priority"]),
        Index(value = ["category"]),
        Index(value = ["createdAt"])
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Date? = null,
    val dueTime: String? = null, // Format: HH:mm
    val category: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val completedAt: Date? = null,

    // Recurrence fields (added in Week 3)
    val recurrencePattern: String? = null, // DAILY, WEEKLY, MONTHLY
    val recurrenceInterval: Int = 1 // Every X days/weeks/months
)
