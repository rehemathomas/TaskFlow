package com.example.taskflow.data.database

import androidx.room.TypeConverter
import com.example.taskflow.data.entity.Priority
import java.util.Date

/**
 * Room TypeConverters for converting complex types to primitive types for database storage
 *
 * These converters enable Room to store Date and Priority enum types
 * by converting them to Long (timestamp) and String respectively
 */
class Converters {

    /**
     * Convert Date to Long timestamp for database storage
     * @param date Date object to convert
     * @return Timestamp in milliseconds, or null if date is null
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    /**
     * Convert Long timestamp to Date object
     * @param timestamp Milliseconds since epoch
     * @return Date object, or null if timestamp is null
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    /**
     * Convert Priority enum to String for database storage
     * @param priority Priority enum value
     * @return String representation of priority
     */
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    /**
     * Convert String to Priority enum
     * @param value String representation of priority
     * @return Priority enum value
     */
    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }
}
