package com.example.taskflow.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity for task reminders
 * Stores information about scheduled notifications
 *
 * @property id Unique identifier for the reminder
 * @property taskId ID of the associated task
 * @property triggerAt Timestamp when reminder should trigger
 * @property isTriggered Whether the reminder has already fired
 */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["taskId"]),
        Index(value = ["triggerAt"]),
        Index(value = ["isTriggered"])
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val taskId: Long,
    val triggerAt: Date,
    val isTriggered: Boolean = false
)
