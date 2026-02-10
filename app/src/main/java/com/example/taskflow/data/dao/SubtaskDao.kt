package com.example.taskflow.data.dao

import androidx.room.*
import com.example.taskflow.data.entity.Subtask
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Subtask entity
 * Manages subtasks associated with parent tasks
 */
@Dao
interface SubtaskDao {

    /**
     * Get all subtasks for a specific task, ordered by position
     * @param taskId Parent task ID
     * @return Flow emitting list of subtasks
     */
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY position ASC, id ASC")
    fun getSubtasksForTask(taskId: Long): Flow<List<Subtask>>

    /**
     * Get a single subtask by ID
     * @param subtaskId Subtask ID
     * @return Flow emitting single subtask or null
     */
    @Query("SELECT * FROM subtasks WHERE id = :subtaskId")
    fun getSubtaskById(subtaskId: Long): Flow<Subtask?>

    /**
     * Get count of incomplete subtasks for a task
     * @param taskId Parent task ID
     * @return Count of incomplete subtasks
     */
    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId AND isDone = 0")
    suspend fun getIncompleteSubtaskCount(taskId: Long): Int

    /**
     * Get count of all subtasks for a task
     * @param taskId Parent task ID
     * @return Total count of subtasks
     */
    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId")
    suspend fun getTotalSubtaskCount(taskId: Long): Int

    /**
     * Insert a new subtask
     * @param subtask Subtask to insert
     * @return Row ID of inserted subtask
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: Subtask): Long

    /**
     * Update an existing subtask
     * @param subtask Subtask with updated values
     */
    @Update
    suspend fun updateSubtask(subtask: Subtask)

    /**
     * Delete a subtask
     * @param subtask Subtask to delete
     */
    @Delete
    suspend fun deleteSubtask(subtask: Subtask)

    /**
     * Delete subtask by ID
     * @param subtaskId ID of subtask to delete
     */
    @Query("DELETE FROM subtasks WHERE id = :subtaskId")
    suspend fun deleteSubtaskById(subtaskId: Long)

    /**
     * Delete all subtasks for a specific task
     * @param taskId Parent task ID
     */
    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubtasksForTask(taskId: Long)

    /**
     * Toggle subtask completion status
     * @param subtaskId ID of subtask to toggle
     * @param isDone New completion status
     */
    @Query("UPDATE subtasks SET isDone = :isDone WHERE id = :subtaskId")
    suspend fun toggleSubtaskCompletion(subtaskId: Long, isDone: Boolean)

    /**
     * Update positions of multiple subtasks (for reordering)
     * @param subtaskPositions Map of subtask ID to new position
     */
    @Transaction
    suspend fun updateSubtaskPositions(subtaskPositions: Map<Long, Int>) {
        subtaskPositions.forEach { (subtaskId, position) ->
            updateSubtaskPosition(subtaskId, position)
        }
    }

    /**
     * Update position of a single subtask
     * @param subtaskId Subtask ID
     * @param position New position
     */
    @Query("UPDATE subtasks SET position = :position WHERE id = :subtaskId")
    suspend fun updateSubtaskPosition(subtaskId: Long, position: Int)
}
