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

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterPriority = MutableStateFlow<Priority?>(null)
    val filterPriority: StateFlow<Priority?> = _filterPriority.asStateFlow()

    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory.asStateFlow()

    private val _showCompleted = MutableStateFlow(true)
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DATE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    @OptIn(FlowPreview::class)
    val tasks: StateFlow<List<Task>> = combine(
        _searchQuery.debounce(300),
        _filterPriority,
        _filterCategory,
        _showCompleted,
        _sortOption,
        repository.getAllTasks()
    ) { query, priority, category, showCompleted, sort, allTasks ->
        var filteredTasks = allTasks

        if (!showCompleted) {
            filteredTasks = filteredTasks.filter { !it.isCompleted }
        }

        if (query.isNotBlank()) {
            filteredTasks = filteredTasks.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.description?.contains(query, ignoreCase = true) == true
            }
        }

        if (priority != null) {
            filteredTasks = filteredTasks.filter { it.priority == priority }
        }

        if (category != null) {
            filteredTasks = filteredTasks.filter { it.category == category }
        }

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

    val categories: StateFlow<List<String>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            saveSearchQuery(query)
        }
    }

    fun setFilterPriority(priority: Priority?) {
        _filterPriority.value = priority
    }

    fun setFilterCategory(category: String?) {
        _filterCategory.value = category
    }

    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }

    fun setShowCompleted(show: Boolean) {
        _showCompleted.value = show
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun clearFilters() {
        _filterPriority.value = null
        _filterCategory.value = null
        _showCompleted.value = true
    }

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

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleTaskCompletion(taskId, isCompleted)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update task"
            }
        }
    }

    fun addTagsToTask(taskId: Long, tagNames: List<String>) {
        viewModelScope.launch {
            try {
                repository.addTagsToTask(taskId, tagNames)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add tags"
            }
        }
    }

    private fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            try {
                repository.saveSearch(query)
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
