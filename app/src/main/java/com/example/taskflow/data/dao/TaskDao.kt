package com.example.taskflow.data.dao

import androidx.room.*
import com.example.taskflow.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY createdAt DESC")
    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(category: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<Task?>

    @Query("""
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
        ORDER BY createdAt DESC
    """)
    fun searchTasks(query: String): Flow<List<Task>>

    @Query("SELECT DISTINCT category FROM tasks WHERE category IS NOT NULL ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("""
        SELECT * FROM tasks 
        WHERE dueDate BETWEEN :startOfDay AND :endOfDay 
        ORDER BY dueTime ASC
    """)
    fun getTasksByDate(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    @Query("""
        SELECT * FROM tasks 
        WHERE dueDate < :currentTime AND isCompleted = 0 
        ORDER BY dueDate ASC
    """)
    fun getOverdueTasks(currentTime: Long): Flow<List<Task>>

    @Query("""
        SELECT * FROM tasks 
        WHERE dueDate BETWEEN :startDate AND :endDate 
        ORDER BY dueDate ASC, dueTime ASC
    """)
    fun getTasksInDateRange(startDate: Long, endDate: Long): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate < :currentTime AND isCompleted = 0")
    fun getOverdueTaskCount(currentTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithDetails(taskId: Long): Flow<TaskWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM tasks WHERE isCompleted = 1 AND completedAt < :beforeDate")
    suspend fun deleteOldCompletedTasks(beforeDate: Long)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :taskId")
    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    suspend fun deleteTasks(taskIds: List<Long>)

    @Query("UPDATE tasks SET isCompleted = 1, completedAt = :completedAt WHERE id IN (:taskIds)")
    suspend fun completeTasks(taskIds: List<Long>, completedAt: Long)
}

data class TaskWithDetails(
    @Embedded val task: Task,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subtasks: List<Subtask>,

    @Relation(
        parentColumn = "id",
        entityColumn = "tagId",
        associateBy = Junction(
            value = TaskTagCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)
