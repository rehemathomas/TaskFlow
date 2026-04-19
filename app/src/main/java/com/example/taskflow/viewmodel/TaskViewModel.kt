package com.example.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.data.entity.Priority
import com.example.taskflow.data.entity.Subtask
import com.example.taskflow.data.entity.Task
import com.example.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
    data class TaskDeleted(val task: Task) : UiEvent()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
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

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    // Kurekebisha combine ya flows 6 ambayo haitekelezeki —
    // Kotlin ina typed overloads mpaka flows 5 tu.
    // Suluhisho: tunaweka filter/sort state katika data class moja
    // kisha tunafanya combine(filterState, allTasks)
    private data class FilterState(
        val query: String,
        val priority: Priority?,
        val category: String?,
        val showCompleted: Boolean,
        val sort: SortOption
    )

    private val filterState: StateFlow<FilterState> = combine(
        _searchQuery.debounce(300),
        _filterPriority,
        _filterCategory,
        _showCompleted,
        _sortOption
    ) { query, priority, category, showCompleted, sort ->
        FilterState(query, priority, category, showCompleted, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FilterState("", null, null, true, SortOption.DATE_DESC)
    )

    val tasks: StateFlow<List<Task>> = combine(
        filterState,
        repository.getAllTasks()
    ) { filter, allTasks ->
        var result = allTasks

        if (!filter.showCompleted) {
            result = result.filter { !it.isCompleted }
        }

        if (filter.query.isNotBlank()) {
            result = result.filter { task ->
                task.title.contains(filter.query, ignoreCase = true) ||
                        task.description?.contains(filter.query, ignoreCase = true) == true
            }
        }

        if (filter.priority != null) {
            result = result.filter { it.priority == filter.priority }
        }

        if (filter.category != null) {
            result = result.filter { it.category == filter.category }
        }

        when (filter.sort) {
            SortOption.DATE_ASC -> result.sortedBy { it.dueDate?.time ?: Long.MAX_VALUE }
            SortOption.DATE_DESC -> result.sortedByDescending { it.dueDate?.time ?: 0 }
            SortOption.PRIORITY_HIGH_FIRST -> result.sortedBy { it.priority.ordinal }
            SortOption.PRIORITY_LOW_FIRST -> result.sortedByDescending { it.priority.ordinal }
            SortOption.TITLE_A_TO_Z -> result.sortedBy { it.title.lowercase() }
            SortOption.TITLE_Z_TO_A -> result.sortedByDescending { it.title.lowercase() }
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

    val totalTaskCount: StateFlow<Int> = repository.getTotalTaskCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val pendingTaskCount: StateFlow<Int> = repository.getPendingTaskCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val completedTaskCount: StateFlow<Int> = repository.getCompletedTaskCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val overdueTaskCount: StateFlow<Int> = repository.getOverdueTaskCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun getTaskById(taskId: Long): StateFlow<Task?> =
        repository.getTaskById(taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun getSubtasksForTask(taskId: Long) =
        repository.getSubtasksForTask(taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getTaskWithDetails(taskId: Long) =
        repository.getTaskWithDetails(taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun getTasksByDate(startOfDay: Long, endOfDay: Long) =
        repository.getTasksByDate(startOfDay, endOfDay)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getOverdueTasks() = repository.getOverdueTasks()
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
        _searchQuery.value = ""
    }

    fun hasActiveFilters(): Boolean =
        _filterPriority.value != null ||
                _filterCategory.value != null ||
                !_showCompleted.value ||
                _searchQuery.value.isNotBlank()

    fun insertTask(task: Task, onSuccess: (Long) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val taskId = repository.insertTask(task)
                onSuccess(taskId)
                _error.value = null
                _uiEvent.emit(UiEvent.ShowMessage("Task created"))
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create task"
                _uiEvent.emit(UiEvent.ShowError("Failed to create task"))
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
                _uiEvent.emit(UiEvent.ShowMessage("Task updated"))
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update task"
                _uiEvent.emit(UiEvent.ShowError("Failed to update task"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(task: Task, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
                onSuccess()
                _error.value = null
                _uiEvent.emit(UiEvent.TaskDeleted(task))
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete task"
                _uiEvent.emit(UiEvent.ShowError("Failed to delete task"))
            }
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.insertTask(task)
                _uiEvent.emit(UiEvent.ShowMessage("Task restored"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to restore task"))
            }
        }
    }

    fun deleteTaskById(taskId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteTaskById(taskId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete task"
                _uiEvent.emit(UiEvent.ShowError("Failed to delete task"))
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

    fun bulkDelete(taskIds: List<Long>) {
        viewModelScope.launch {
            try {
                repository.deleteTasks(taskIds)
                _uiEvent.emit(UiEvent.ShowMessage("${taskIds.size} tasks deleted"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to delete tasks"))
            }
        }
    }

    fun bulkComplete(taskIds: List<Long>) {
        viewModelScope.launch {
            try {
                repository.completeTasks(taskIds)
                _uiEvent.emit(UiEvent.ShowMessage("${taskIds.size} tasks completed"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to complete tasks"))
            }
        }
    }

    fun insertSubtask(subtask: Subtask) {
        viewModelScope.launch {
            try {
                repository.insertSubtask(subtask)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to add subtask"))
            }
        }
    }

    fun toggleSubtaskCompletion(subtaskId: Long, isDone: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleSubtaskCompletion(subtaskId, isDone)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to update subtask"))
            }
        }
    }

    fun deleteSubtask(subtask: Subtask) {
        viewModelScope.launch {
            try {
                repository.deleteSubtask(subtask)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to delete subtask"))
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

    fun clearAllCompletedTasks() {
        viewModelScope.launch {
            try {
                repository.deleteOldCompletedTasks(daysOld = 0)
                _uiEvent.emit(UiEvent.ShowMessage("Completed tasks cleared"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to clear tasks"))
            }
        }
    }

    private fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            try {
                repository.saveSearch(query)
            } catch (e: Exception) {
                // Silently fail — history is optional
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
