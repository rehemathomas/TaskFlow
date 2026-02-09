package com.example.taskflow.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for subtasks that belong to a parent task
 * Foreign key ensures referential integrity with cascade delete
 *
 * @property id Unique identifier for the subtask
 * @property taskId ID of the parent task
 * @property title Subtask title
 * @property isDone Completion status
 * @property position Order position for display (lower = higher priority)
 */
@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE // Delete subtasks when parent task is deleted
        )
    ],
    indices = [
        Index(value = ["taskId"]),
        Index(value = ["position"])
    ]
)
data class Subtask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val taskId: Long,
    val title: String,
    val isDone: Boolean = false,
    val position: Int = 0 // For ordering subtasks
)
