package com.example.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.data.entity.Priority
import com.example.taskflow.data.entity.Task
import com.example.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for managing task list state and operations
 * Provides reactive state management with StateFlow
 */
class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filter states
    private val _filterPriority = MutableStateFlow<Priority?>(null)
    val filterPriority: StateFlow<Priority?> = _filterPriority.asStateFlow()

    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory.asStateFlow()

    private val _showCompleted = MutableStateFlow(true)
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

    // Sort option state
    private val _sortOption = MutableStateFlow(SortOption.DATE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Combined flow of filtered and sorted tasks
     * Reactively updates when any filter or sort option changes
     */
    @OptIn(FlowPreview::class)
    val tasks: StateFlow<List<Task>> = combine(
        _searchQuery.debounce(300), // Debounce search input
        _filterPriority,
        _filterCategory,
        _showCompleted,
        _sortOption,
        repository.getAllTasks()
    ) { query, priority, category, showCompleted, sort, allTasks ->
        var filteredTasks = allTasks

        // Apply completion filter
        if (!showCompleted) {
            filteredTasks = filteredTasks.filter { !it.isCompleted }
        }

        // Apply search filter
        if (query.isNotBlank()) {
            filteredTasks = filteredTasks.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.description?.contains(query, ignoreCase = true) == true
            }
        }

        // Apply priority filter
        if (priority != null) {
            filteredTasks = filteredTasks.filter { it.priority == priority }
        }

        // Apply category filter
        if (category != null) {
            filteredTasks = filteredTasks.filter { it.category == category }
        }

        // Apply sorting
        when (sort) {
            SortOption.DATE_ASC -> filteredTasks.sortedBy { it.dueDate?.time ?: Long.MAX_VALUE }
            SortOption.DATE_DESC -> filteredTasks.sortedByDescending { it.dueDate?.time ?: 0 }
            SortOption.PRIORITY_HIGH_FIRST -> filteredTasks.sortedBy { it.priority.ordinal }
            SortOption.PRIORITY_LOW_FIRST -> filteredTasks.sortedByDescending { it.priority.ordinal }
            SortOption.TITLE_A_TO_Z -> filteredTasks.sortedBy { it.title.lowercase() }
            SortOption.TITLE_Z_TO_A -> filteredTasks.sortedByDescending { it.title.lowercase() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Get all unique categories
     */
    val categories: StateFlow<List<String>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            saveSearchQuery(query)
        }
    }

    /**
     * Set priority filter
     */
    fun setFilterPriority(priority: Priority?) {
        _filterPriority.value = priority
    }

    /**
     * Set category filter
     */
    fun setFilterCategory(category: String?) {
        _filterCategory.value = category
    }

    /**
     * Toggle show completed tasks
     */
    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }

    /**
     * Set sort option
     */
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    /**
     * Insert a new task
     */
    fun insertTask(task: Task, onSuccess: (Long) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val taskId = repository.insertTask(task)
                onSuccess(taskId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create task"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update an existing task
     */
    fun updateTask(task: Task, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateTask(task.copy(updatedAt = Date()))
                onSuccess()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update task"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete a task
     */
    fun deleteTask(task: Task, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteTask(task)
                onSuccess()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete task"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggle task completion status
     */
    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleTaskCompletion(taskId, isCompleted)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update task"
            }
        }
    }

    /**
     * Save search query to history
     */
    private fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            try {
                repository.saveSearch(query)
            } catch (e: Exception) {
                // Silently fail for search history
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
}
