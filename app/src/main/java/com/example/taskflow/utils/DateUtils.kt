package com.example.taskflow.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for date formatting and manipulation
 */
object DateUtils {

    private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateTimeFormatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())

    /**
     * Format date to readable string (e.g., "Jan 15, 2026")
     */
    fun formatDate(date: Date): String {
        return dateFormatter.format(date)
    }

    /**
     * Format time to readable string (e.g., "2:30 PM")
     */
    fun formatTime(date: Date): String {
        return timeFormatter.format(date)
    }

    /**
     * Format date and time to readable string
     */
    fun formatDateTime(date: Date): String {
        return dateTimeFormatter.format(date)
    }

    /**
     * Check if date is today
     */
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val compareDate = Calendar.getInstance().apply { time = date }

        return today.get(Calendar.YEAR) == compareDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == compareDate.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Check if date is tomorrow
     */
    fun isTomorrow(date: Date): Boolean {
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val compareDate = Calendar.getInstance().apply { time = date }

        return tomorrow.get(Calendar.YEAR) == compareDate.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == compareDate.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Check if date is in the past
     */
    fun isPast(date: Date): Boolean {
        return date.before(Date())
    }

    /**
     * Get relative time string (e.g., "Today", "Tomorrow", "Jan 15")
     */
    fun getRelativeTimeString(date: Date): String {
        return when {
            isToday(date) -> "Today"
            isTomorrow(date) -> "Tomorrow"
            else -> formatDate(date)
        }
    }

    /**
     * Get start of day (00:00:00)
     */
    fun getStartOfDay(date: Date = Date()): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    /**
     * Get end of day (23:59:59)
     */
    fun getEndOfDay(date: Date = Date()): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
    }

    /**
     * Add days to date
     */
    fun addDays(date: Date, days: Int): Date {
        return Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, days)
        }.time
    }

    /**
     * Get days between two dates
     */
    fun daysBetween(date1: Date, date2: Date): Int {
        val diff = date2.time - date1.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}
