package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskflow.ui.components.*
import com.example.taskflow.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val filterPriority by viewModel.filterPriority.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val showCompleted by viewModel.showCompleted.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val totalCount by viewModel.totalTaskCount.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is com.example.taskflow.viewmodel.UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is com.example.taskflow.viewmodel.UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is com.example.taskflow.viewmodel.UiEvent.TaskDeleted -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Task deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.restoreTask(event.task)
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            selectedPriority = filterPriority,
            selectedCategory = filterCategory,
            categories = categories,
            showCompleted = showCompleted,
            onPrioritySelected = { viewModel.setFilterPriority(it) },
            onCategorySelected = { viewModel.setFilterCategory(it) },
            onShowCompletedChanged = { viewModel.setShowCompleted(it) },
            onDismiss = { showFilterSheet = false }
        )
    }

    if (showSortSheet) {
        SortBottomSheet(
            currentSort = sortOption,
            onSortSelected = { viewModel.setSortOption(it) },
            onDismiss = { showSortSheet = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Task Flow")
                        if (totalCount > 0) {
                            val shown = tasks.size
                            Text(
                                text = if (viewModel.hasActiveFilters() && shown != totalCount) {
                                    "$shown of $totalCount tasks"
                                } else {
                                    "$totalCount task${if (totalCount != 1) "s" else ""}"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (viewModel.hasActiveFilters()) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text("Clear", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter tasks",
                            tint = if (filterPriority != null || filterCategory != null || !showCompleted) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort tasks"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text("Add Task") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBarWithSuggestions(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                tasks.isEmpty() -> {
                    EmptyState(
                        message = if (viewModel.hasActiveFilters()) {
                            "No tasks match your filters"
                        } else {
                            "No tasks yet — tap Add Task to get started"
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = tasks,
                            key = { it.id }
                        ) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onTaskClick(task.id) },
                                onCheckedChange = { checked ->
                                    viewModel.toggleTaskCompletion(task.id, checked)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
