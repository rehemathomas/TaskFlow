package com.example.taskflow.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for tags that can be applied to multiple tasks
 *
 * @property id Unique identifier for the tag
 * @property name Tag name (unique)
 * @property color Optional color code for visual distinction
 */
@Entity(
    tableName = "tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val color: String? = null // Hex color code (e.g., "#FF5722")
)
