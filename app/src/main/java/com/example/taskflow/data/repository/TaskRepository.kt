package com.example.taskflow.data.repository

import com.example.taskflow.data.dao.*
import com.example.taskflow.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TaskRepository(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val tagDao: TagDao,
    private val reminderDao: ReminderDao,
    private val searchHistoryDao: SearchHistoryDao
) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<Task>> =
        taskDao.getTasksByCompletion(isCompleted)

    fun getTasksByPriority(priority: Priority): Flow<List<Task>> =
        taskDao.getTasksByPriority(priority)

    fun getTasksByCategory(category: String): Flow<List<Task>> =
        taskDao.getTasksByCategory(category)

    fun getTaskById(taskId: Long): Flow<Task?> = taskDao.getTaskById(taskId)

    fun getTaskWithDetails(taskId: Long): Flow<TaskWithDetails?> =
        taskDao.getTaskWithDetails(taskId)

    fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)

    fun getAllCategories(): Flow<List<String>> = taskDao.getAllCategories()

    fun getTasksByDate(date: Date): Flow<List<Task>> =
        taskDao.getTasksByDate(date.time)

    fun getOverdueTasks(): Flow<List<Task>> =
        taskDao.getOverdueTasks(System.currentTimeMillis())

    fun getTasksInDateRange(startDate: Date, endDate: Date): Flow<List<Task>> =
        taskDao.getTasksInDateRange(startDate.time, endDate.time)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(taskId: Long) = taskDao.deleteTaskById(taskId)

    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.toggleTaskCompletion(taskId, isCompleted, completedAt)
    }

    suspend fun deleteTasks(taskIds: List<Long>) = taskDao.deleteTasks(taskIds)

    suspend fun completeTasks(taskIds: List<Long>) {
        taskDao.completeTasks(taskIds, System.currentTimeMillis())
    }

    suspend fun deleteOldCompletedTasks(daysOld: Int = 30) {
        val cutoffDate = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        taskDao.deleteOldCompletedTasks(cutoffDate)
    }

    fun getSubtasksForTask(taskId: Long): Flow<List<Subtask>> =
        subtaskDao.getSubtasksForTask(taskId)

    fun getSubtaskById(subtaskId: Long): Flow<Subtask?> =
        subtaskDao.getSubtaskById(subtaskId)

    suspend fun getIncompleteSubtaskCount(taskId: Long): Int =
        subtaskDao.getIncompleteSubtaskCount(taskId)

    suspend fun getTotalSubtaskCount(taskId: Long): Int =
        subtaskDao.getTotalSubtaskCount(taskId)

    suspend fun insertSubtask(subtask: Subtask): Long =
        subtaskDao.insertSubtask(subtask)

    suspend fun updateSubtask(subtask: Subtask) =
        subtaskDao.updateSubtask(subtask)

    suspend fun deleteSubtask(subtask: Subtask) =
        subtaskDao.deleteSubtask(subtask)

    suspend fun toggleSubtaskCompletion(subtaskId: Long, isDone: Boolean) =
        subtaskDao.toggleSubtaskCompletion(subtaskId, isDone)

    suspend fun updateSubtaskPositions(subtaskPositions: Map<Long, Int>) =
        subtaskDao.updateSubtaskPositions(subtaskPositions)

    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    fun getTagsForTask(taskId: Long): Flow<List<Tag>> =
        tagDao.getTagsForTask(taskId)

    suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)

    suspend fun getOrCreateTag(tagName: String): Long =
        tagDao.getOrCreateTag(tagName)

    suspend fun linkTagToTask(taskId: Long, tagId: Long) {
        tagDao.insertTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))
    }

    suspend fun unlinkTagFromTask(taskId: Long, tagId: Long) {
        tagDao.deleteTaskTagCrossRef(taskId, tagId)
    }

    suspend fun deleteAllTagsForTask(taskId: Long) {
        tagDao.deleteAllTagsForTask(taskId)
    }

    suspend fun addTagsToTask(taskId: Long, tagNames: List<String>) {
        tagNames.forEach { tagName ->
            val tagId = getOrCreateTag(tagName)
            linkTagToTask(taskId, tagId)
        }
    }

    fun getRemindersForTask(taskId: Long): Flow<List<Reminder>> =
        reminderDao.getRemindersForTask(taskId)

    fun getPendingReminders(): Flow<List<Reminder>> =
        reminderDao.getPendingReminders()

    suspend fun getRemindersToTrigger(): List<Reminder> =
        reminderDao.getRemindersToTrigger(System.currentTimeMillis())

    suspend fun insertReminder(reminder: Reminder): Long =
        reminderDao.insertReminder(reminder)

    suspend fun deleteReminder(reminder: Reminder) =
        reminderDao.deleteReminder(reminder)

    suspend fun markReminderAsTriggered(reminderId: Long) =
        reminderDao.markReminderAsTriggered(reminderId)

    suspend fun deleteOldTriggeredReminders(daysOld: Int = 7) {
        val cutoffDate = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        reminderDao.deleteOldTriggeredReminders(cutoffDate)
    }

    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistory>> =
        searchHistoryDao.getRecentSearches(limit)

    fun searchHistory(query: String, limit: Int = 5): Flow<List<SearchHistory>> =
        searchHistoryDao.searchHistory(query, limit)

    suspend fun saveSearch(query: String) =
        searchHistoryDao.saveSearch(query)

    suspend fun clearAllSearchHistory() =
        searchHistoryDao.clearAllHistory()

    suspend fun deleteOldSearches(daysOld: Int = 30) {
        val cutoffDate = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        searchHistoryDao.deleteOldSearches(cutoffDate)
    }
}
