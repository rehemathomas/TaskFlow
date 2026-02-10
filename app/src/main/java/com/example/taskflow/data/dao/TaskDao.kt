package com.example.taskflow.data.dao

import androidx.room.*
import com.example.taskflow.data.entity.Priority
import com.example.taskflow.data.entity.Task
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Task entity
 * Provides reactive Flow-based queries for observing database changes
 */
@Dao
interface TaskDao {

    /**
     * Get all tasks ordered by creation date (newest first)
     * @return Flow emitting list of tasks whenever data changes
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    /**
     * Get tasks filtered by completion status
     * @param isCompleted Filter by completion status
     * @return Flow emitting filtered tasks
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY createdAt DESC")
    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<Task>>

    /**
     * Get tasks filtered by priority
     * @param priority Priority level to filter by
     * @return Flow emitting filtered tasks
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>

    /**
     * Get tasks filtered by category
     * @param category Category name to filter by
     * @return Flow emitting filtered tasks
     */
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(category: String): Flow<List<Task>>

    /**
     * Get a single task by ID
     * @param taskId Task ID
     * @return Flow emitting single task or null
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<Task?>

    /**
     * Search tasks by title or description
     * @param query Search query (will be wrapped with % for LIKE search)
     * @return Flow emitting matching tasks
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTasks(query: String): Flow<List<Task>>

    /**
     * Get all unique categories from tasks
     * @return Flow emitting list of category names
     */
    @Query("SELECT DISTINCT category FROM tasks WHERE category IS NOT NULL ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    /**
     * Get tasks due on a specific date
     * @param date Date to filter by (as Long timestamp)
     * @return Flow emitting tasks due on specified date
     */
    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY dueTime ASC")
    fun getTasksByDate(date: Long): Flow<List<Task>>

    /**
     * Get overdue tasks (due date in past and not completed)
     * @param currentTime Current timestamp
     * @return Flow emitting overdue tasks
     */
    @Query("SELECT * FROM tasks WHERE dueDate < :currentTime AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getOverdueTasks(currentTime: Long): Flow<List<Task>>

    /**
     * Get tasks for a date range (for calendar view)
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return Flow emitting tasks in range
     */
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate ORDER BY dueDate ASC, dueTime ASC")
    fun getTasksInDateRange(startDate: Long, endDate: Long): Flow<List<Task>>

    /**
     * Insert a new task
     * @param task Task to insert
     * @return Row ID of inserted task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    /**
     * Update an existing task
     * @param task Task with updated values
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Delete a task
     * @param task Task to delete
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Delete task by ID
     * @param taskId ID of task to delete
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    /**
     * Delete all completed tasks older than specified date
     * Used for cleanup worker
     * @param beforeDate Delete tasks completed before this date
     */
    @Query("DELETE FROM tasks WHERE isCompleted = 1 AND completedAt < :beforeDate")
    suspend fun deleteOldCompletedTasks(beforeDate: Long)

    /**
     * Toggle task completion status
     * @param taskId ID of task to toggle
     * @param isCompleted New completion status
     * @param completedAt Timestamp when completed (or null if uncompleting)
     */
    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :taskId")
    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean, completedAt: Long?)

    /**
     * Bulk delete tasks by IDs
     * @param taskIds List of task IDs to delete
     */
    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    suspend fun deleteTasks(taskIds: List<Long>)

    /**
     * Bulk complete tasks by IDs
     * @param taskIds List of task IDs to mark as complete
     * @param completedAt Timestamp when completed
     */
    @Query("UPDATE tasks SET isCompleted = 1, completedAt = :completedAt WHERE id IN (:taskIds)")
    suspend fun completeTasks(taskIds: List<Long>, completedAt: Long)
}
