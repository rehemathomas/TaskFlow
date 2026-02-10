package com.example.taskflow.data.dao

import androidx.room.*
import com.example.taskflow.data.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SearchHistory entity
 * Manages recent search queries for suggestions
 */
@Dao
interface SearchHistoryDao {

    /**
     * Get recent search queries ordered by most recent
     * @param limit Maximum number of results to return
     * @return Flow emitting list of recent searches
     */
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistory>>

    /**
     * Search history entries matching a query
     * @param query Search query to match
     * @param limit Maximum number of results
     * @return Flow emitting matching search history
     */
    @Query("SELECT * FROM search_history WHERE query LIKE '%' || :query || '%' ORDER BY searchedAt DESC LIMIT :limit")
    fun searchHistory(query: String, limit: Int = 5): Flow<List<SearchHistory>>

    /**
     * Check if a query already exists in history
     * @param query Query to check
     * @return Existing SearchHistory entry or null
     */
    @Query("SELECT * FROM search_history WHERE query = :query")
    suspend fun getSearchByQuery(query: String): SearchHistory?

    /**
     * Insert a new search history entry
     * @param searchHistory Search history to insert
     * @return Row ID of inserted entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(searchHistory: SearchHistory): Long

    /**
     * Delete a search history entry
     * @param searchHistory Entry to delete
     */
    @Delete
    suspend fun deleteSearch(searchHistory: SearchHistory)

    /**
     * Clear all search history
     */
    @Query("DELETE FROM search_history")
    suspend fun clearAllHistory()

    /**
     * Delete old search history entries
     * @param beforeDate Delete entries older than this date
     */
    @Query("DELETE FROM search_history WHERE searchedAt < :beforeDate")
    suspend fun deleteOldSearches(beforeDate: Long)

    /**
     * Save or update a search query
     * If query exists, updates its timestamp; otherwise inserts new entry
     * @param query Search query to save
     */
    @Transaction
    suspend fun saveSearch(query: String) {
        val existing = getSearchByQuery(query)
        if (existing != null) {
            deleteSearch(existing)
        }
        insertSearch(SearchHistory(query = query))
    }
}
