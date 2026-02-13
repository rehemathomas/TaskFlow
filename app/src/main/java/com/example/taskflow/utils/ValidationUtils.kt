package com.example.taskflow.utils

/**
 * Utility functions for input validation
 */
object ValidationUtils {

    /**
     * Validate task title
     * @return Error message if invalid, null if valid
     */
    fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "Title cannot be empty"
            title.length > 100 -> "Title must be less than 100 characters"
            else -> null
        }
    }

    /**
     * Validate task description
     * @return Error message if invalid, null if valid
     */
    fun validateDescription(description: String): String? {
        return when {
            description.length > 500 -> "Description must be less than 500 characters"
            else -> null
        }
    }

    /**
     * Validate category name
     * @return Error message if invalid, null if valid
     */
    fun validateCategory(category: String): String? {
        return when {
            category.length > 50 -> "Category must be less than 50 characters"
            else -> null
        }
    }

    /**
     * Validate tag name
     * @return Error message if invalid, null if valid
     */
    fun validateTag(tag: String): String? {
        return when {
            tag.isBlank() -> "Tag cannot be empty"
            tag.length > 30 -> "Tag must be less than 30 characters"
            !tag.matches(Regex("^[a-zA-Z0-9_-]+$")) -> "Tag can only contain letters, numbers, hyphens, and underscores"
            else -> null
        }
    }

    /**
     * Validate time string (HH:mm format)
     * @return true if valid
     */
    fun isValidTime(time: String): Boolean {
        return time.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))
    }

    /**
     * Sanitize user input (remove extra whitespace)
     */
    fun sanitizeInput(input: String): String {
        return input.trim().replace(Regex("\\s+"), " ")
    }
}
