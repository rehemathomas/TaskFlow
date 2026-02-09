package com.example.taskflow.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity for storing recent search queries
 * Used to provide search suggestions
 *
 * @property id Unique identifier
 * @property query The search query text
 * @property searchedAt Timestamp of when the search was performed
 */
@Entity(
    tableName = "search_history",
    indices = [Index(value = ["searchedAt"])]
)
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val query: String,
    val searchedAt: Date = Date()
)
