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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Task
import com.example.taskflow.ui.components.EmptyState
import com.example.taskflow.ui.components.SearchBarWithSuggestions
import com.example.taskflow.ui.components.TaskCard

/**
 * Main task list screen
 * Displays all tasks with search, filter, and sort capabilities
 *
 * @param onTaskClick Callback when a task is clicked
 * @param onAddClick Callback when add button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Get ViewModel instance
    // val viewModel: TaskViewModel = viewModel()
    // val tasks by viewModel.tasks.collectAsState()
    // val searchQuery by viewModel.searchQuery.collectAsState()
    // val isLoading by viewModel.isLoading.collectAsState()

    // Temporary state for UI preview
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    val tasks = remember { emptyList<Task>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Flow") },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter tasks"
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
            // Search bar
            SearchBarWithSuggestions(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* viewModel.updateSearchQuery(it) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Task list
            if (tasks.isEmpty()) {
                EmptyState(
                    message = "No tasks yet",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
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
                            onCheckedChange = { /* viewModel.toggleTaskCompletion(task.id, it) */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
