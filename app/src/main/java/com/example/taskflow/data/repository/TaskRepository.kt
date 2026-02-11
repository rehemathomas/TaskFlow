package com.example.taskflow.data.repository

import com.example.taskflow.data.dao.*
import com.example.taskflow.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository class for task-related data operations
 *
 * This class acts as a single source of truth for task data,
 * abstracting the data sources (Room database) from the rest of the app.
 * It provides a clean API for the ViewModel layer.
 *
 * @property taskDao Data access object for tasks
 * @property subtaskDao Data access object for subtasks
 * @property tagDao Data access object for tags
 * @property reminderDao Data access object for reminders
 * @property searchHistoryDao Data access object for search history
 */
class TaskRepository(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val tagDao: TagDao,
    private val reminderDao: ReminderDao,
    private val searchHistoryDao: SearchHistoryDao
) {

    // ==================== Task Operations ====================

    /**
     * Get all tasks as a Flow for reactive UI updates
     */
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    /**
     * Get tasks filtered by completion status
     */
    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<Task>> =
        taskDao.getTasksByCompletion(isCompleted)

    /**
     * Get tasks filtered by priority
     */
    fun getTasksByPriority(priority: Priority): Flow<List<Task>> =
        taskDao.getTasksByPriority(priority)

    /**
     * Get tasks filtered by category
     */
    fun getTasksByCategory(category: String): Flow<List<Task>> =
        taskDao.getTasksByCategory(category)

    /**
     * Get a single task by ID
     */
    fun getTaskById(taskId: Long): Flow<Task?> = taskDao.getTaskById(taskId)

    /**
     * Search tasks by query
     */
    fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)

    /**
     * Get all unique categories
     */
    fun getAllCategories(): Flow<List<String>> = taskDao.getAllCategories()

    /**
     * Get tasks due on a specific date
     */
    fun getTasksByDate(date: Date): Flow<List<Task>> =
        taskDao.getTasksByDate(date.time)

    /**
     * Get overdue tasks
     */
    fun getOverdueTasks(): Flow<List<Task>> =
        taskDao.getOverdueTasks(System.currentTimeMillis())

    /**
     * Get tasks in a date range
     */
    fun getTasksInDateRange(startDate: Date, endDate: Date): Flow<List<Task>> =
        taskDao.getTasksInDateRange(startDate.time, endDate.time)

    /**
     * Insert a new task
     * @return ID of the inserted task
     */
    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    /**
     * Update an existing task
     */
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    /**
     * Delete a task
     */
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    /**
     * Delete task by ID
     */
    suspend fun deleteTaskById(taskId: Long) = taskDao.deleteTaskById(taskId)

    /**
     * Toggle task completion status
     */
    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.toggleTaskCompletion(taskId, isCompleted, completedAt)
    }

    /**
     * Bulk delete tasks
     */
    suspend fun deleteTasks(taskIds: List<Long>) = taskDao.deleteTasks(taskIds)

    /**
     * Bulk complete tasks
     */
    suspend fun completeTasks(taskIds: List<Long>) {
        taskDao.completeTasks(taskIds, System.currentTimeMillis())
    }

    /**
     * Delete old completed tasks (for cleanup worker)
     */
    suspend fun deleteOldCompletedTasks(daysOld: Int = 30) {
        val cutoffDate = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        taskDao.deleteOldCompletedTasks(cutoffDate)
    }

    // ==================== Subtask Operations ====================

    /**
     * Get subtasks for a task
     */
    fun getSubtasksForTask(taskId: Long): Flow<List<Subtask>> =
        subtaskDao.getSubtasksForTask(taskId)

    /**
     * Get subtask by ID
     */
    fun getSubtaskById(subtaskId: Long): Flow<Subtask?> =
        subtaskDao.getSubtaskById(subtaskId)

    /**
     * Get incomplete subtask count
     */
    suspend fun getIncompleteSubtaskCount(taskId: Long): Int =
        subtaskDao.getIncompleteSubtaskCount(taskId)

    /**
     * Get total subtask count
     */
    suspend fun getTotalSubtaskCount(taskId: Long): Int =
        subtaskDao.getTotalSubtaskCount(taskId)

    /**
     * Insert a subtask
     */
    suspend fun insertSubtask(subtask: Subtask): Long =
        subtaskDao.insertSubtask(subtask)

    /**
     * Update a subtask
     */
    suspend fun updateSubtask(subtask: Subtask) =
        subtaskDao.updateSubtask(subtask)

    /**
     * Delete a subtask
     */
    suspend fun deleteSubtask(subtask: Subtask) =
        subtaskDao.deleteSubtask(subtask)

    /**
     * Toggle subtask completion
     */
    suspend fun toggleSubtaskCompletion(subtaskId: Long, isDone: Boolean) =
        subtaskDao.toggleSubtaskCompletion(subtaskId, isDone)

    /**
     * Update subtask positions (for reordering)
     */
    suspend fun updateSubtaskPositions(subtaskPositions: Map<Long, Int>) =
        subtaskDao.updateSubtaskPositions(subtaskPositions)

    // ==================== Tag Operations ====================

    /**
     * Get all tags
     */
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    /**
     * Get tags for a task
     */
    fun getTagsForTask(taskId: Long): Flow<List<Tag>> =
        tagDao.getTagsForTask(taskId)

    /**
     * Insert a tag
     */
    suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)

    /**
     * Get or create tag by name
     */
    suspend fun getOrCreateTag(tagName: String): Long =
        tagDao.getOrCreateTag(tagName)

    /**
     * Link tag to task
     */
    suspend fun linkTagToTask(taskId: Long, tagId: Long) {
        tagDao.insertTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))
    }

    /**
     * Unlink tag from task
     */
    suspend fun unlinkTagFromTask(taskId: Long, tagId: Long) {
        tagDao.deleteTaskTagCrossRef(taskId, tagId)
    }

    /**
     * Delete all tags for a task
     */
    suspend fun deleteAllTagsForTask(taskId: Long) {
        tagDao.deleteAllTagsForTask(taskId)
    }

    // ==================== Reminder Operations ====================

    /**
     * Get reminders for a task
     */
    fun getRemindersForTask(taskId: Long): Flow<List<Reminder>> =
        reminderDao.getRemindersForTask(taskId)

    /**
     * Get pending reminders
     */
    fun getPendingReminders(): Flow<List<Reminder>> =
        reminderDao.getPendingReminders()

    /**
     * Get reminders to trigger
     */
    suspend fun getRemindersToTrigger(): List<Reminder> =
        reminderDao.getRemindersToTrigger(System.currentTimeMillis())

    /**
     * Insert a reminder
     */
    suspend fun insertReminder(reminder: Reminder): Long =
        reminderDao.insertReminder(reminder)

    /**
     * Delete reminder
     */
    suspend fun deleteReminder(reminder: Reminder) =
        reminderDao.deleteReminder(reminder)

    /**
     * Mark reminder as triggered
     */
    suspend fun markReminderAsTriggered(reminderId: Long) =
        reminderDao.markReminderAsTriggered(reminderId)

    /**
     * Delete old triggered reminders
     */
    suspend fun deleteOldTriggeredReminders(daysOld: Int = 7) {
        val cutoffDate = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        reminderDao.deleteOldTriggeredReminders(cutoffDate)
    }

    // ==================== Search History Operations ====================

    /**
     * Get recent searches
     */
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistory>> =
        searchHistoryDao.getRecentSearches(limit)

    /**
     * Search history
     */
    fun searchHistory(query: String, limit: Int = 5): Flow<List<SearchHistory>> =
        searchHistoryDao.searchHistory(query, limit)

    /**
     * Save search query
     */
    suspend fun saveSearch(query: String) =
        searchHistoryDao.saveSearch(query)

    /**
     * Clear all search history
     */
    suspend fun clearAllSearchHistory() =
        searchHistoryDao.clearAllHistory()

    /**
     * Delete old searches
     */
    suspend fun deleteOldSearches(daysOld: Int = 30) {
        val cutoffDate = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        searchHistoryDao.deleteOldSearches(cutoffDate)
    }
}
