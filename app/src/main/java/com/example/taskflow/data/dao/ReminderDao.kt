package com.example.taskflow.data.dao

import androidx.room.*
import com.example.taskflow.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Reminder entity
 * Manages task reminders and notifications
 */
@Dao
interface ReminderDao {

    /**
     * Get all reminders for a specific task
     * @param taskId Task ID
     * @return Flow emitting list of reminders
     */
    @Query("SELECT * FROM reminders WHERE taskId = :taskId ORDER BY triggerAt ASC")
    fun getRemindersForTask(taskId: Long): Flow<List<Reminder>>

    /**
     * Get pending reminders (not yet triggered)
     * @return Flow emitting list of pending reminders
     */
    @Query("SELECT * FROM reminders WHERE isTriggered = 0 ORDER BY triggerAt ASC")
    fun getPendingReminders(): Flow<List<Reminder>>

    /**
     * Get reminders that should be triggered (trigger time has passed and not yet triggered)
     * @param currentTime Current timestamp
     * @return List of reminders to trigger
     */
    @Query("SELECT * FROM reminders WHERE triggerAt <= :currentTime AND isTriggered = 0")
    suspend fun getRemindersToTrigger(currentTime: Long): List<Reminder>

    /**
     * Insert a new reminder
     * @param reminder Reminder to insert
     * @return Row ID of inserted reminder
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    /**
     * Update an existing reminder
     * @param reminder Reminder with updated values
     */
    @Update
    suspend fun updateReminder(reminder: Reminder)

    /**
     * Delete a reminder
     * @param reminder Reminder to delete
     */
    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    /**
     * Delete reminder by ID
     * @param reminderId ID of reminder to delete
     */
    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: Long)

    /**
     * Delete all reminders for a specific task
     * @param taskId Task ID
     */
    @Query("DELETE FROM reminders WHERE taskId = :taskId")
    suspend fun deleteRemindersForTask(taskId: Long)

    /**
     * Mark reminder as triggered
     * @param reminderId ID of reminder to mark as triggered
     */
    @Query("UPDATE reminders SET isTriggered = 1 WHERE id = :reminderId")
    suspend fun markReminderAsTriggered(reminderId: Long)

    /**
     * Delete old triggered reminders
     * @param beforeDate Delete reminders triggered before this date
     */
    @Query("DELETE FROM reminders WHERE isTriggered = 1 AND triggerAt < :beforeDate")
    suspend fun deleteOldTriggeredReminders(beforeDate: Long)
}
