package com.example.taskflow.data.entity

/**
 * Enum representing task priority levels
 * Used for filtering and sorting tasks by importance
 *
 * @property HIGH Critical tasks requiring immediate attention
 * @property MEDIUM Tasks with moderate importance
 * @property LOW Tasks that can be completed when time permits
 */
enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}
