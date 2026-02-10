package com.example.taskflow.data.dao

import androidx.room.*
import com.example.taskflow.data.entity.Tag
import com.example.taskflow.data.entity.TaskTagCrossRef
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Tag entity and TaskTagCrossRef
 * Manages tags and their relationships with tasks
 */
@Dao
interface TagDao {

    /**
     * Get all tags ordered alphabetically
     * @return Flow emitting list of all tags
     */
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    /**
     * Get tags for a specific task
     * @param taskId Task ID
     * @return Flow emitting tags associated with the task
     */
    @Query("""
        SELECT tags.* FROM tags
        INNER JOIN task_tag_cross_ref ON tags.id = task_tag_cross_ref.tagId
        WHERE task_tag_cross_ref.taskId = :taskId
        ORDER BY tags.name ASC
    """)
    fun getTagsForTask(taskId: Long): Flow<List<Tag>>

    /**
     * Get a tag by name
     * @param name Tag name
     * @return Tag or null if not found
     */
    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?

    /**
     * Insert a new tag
     * @param tag Tag to insert
     * @return Row ID of inserted tag
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag): Long

    /**
     * Update an existing tag
     * @param tag Tag with updated values
     */
    @Update
    suspend fun updateTag(tag: Tag)

    /**
     * Delete a tag
     * @param tag Tag to delete
     */
    @Delete
    suspend fun deleteTag(tag: Tag)

    /**
     * Link a tag to a task
     * @param crossRef Cross-reference containing task and tag IDs
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTagCrossRef(crossRef: TaskTagCrossRef)

    /**
     * Unlink a tag from a task
     * @param taskId Task ID
     * @param tagId Tag ID
     */
    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun deleteTaskTagCrossRef(taskId: Long, tagId: Long)

    /**
     * Delete all tag associations for a task
     * @param taskId Task ID
     */
    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId")
    suspend fun deleteAllTagsForTask(taskId: Long)

    /**
     * Check if a tag is associated with a task
     * @param taskId Task ID
     * @param tagId Tag ID
     * @return True if association exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId)")
    suspend fun isTagLinkedToTask(taskId: Long, tagId: Long): Boolean

    /**
     * Get or create a tag by name
     * If tag exists, returns existing tag ID; otherwise creates new tag
     * @param tagName Name of the tag
     * @return Tag ID
     */
    @Transaction
    suspend fun getOrCreateTag(tagName: String): Long {
        val existingTag = getTagByName(tagName)
        return if (existingTag != null) {
            existingTag.id
        } else {
            insertTag(Tag(name = tagName))
        }
    }
}
