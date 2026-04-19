package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskflow.ui.components.CategoryChip
import com.example.taskflow.ui.components.PriorityBadge
import com.example.taskflow.utils.DateUtils
import com.example.taskflow.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    onEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val taskWithDetails by remember(taskId) {
        viewModel.getTaskWithDetails(taskId)
    }.collectAsState()

    val task = taskWithDetails?.task
    val subtasks = taskWithDetails?.subtasks ?: emptyList()
    val tags = taskWithDetails?.tags ?: emptyList()

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && task != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(task)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task?.title ?: "Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (task != null) {
                        IconButton(
                            onClick = {
                                viewModel.toggleTaskCompletion(task.id, !task.isCompleted)
                            }
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Default.RadioButtonUnchecked
                                },
                                contentDescription = if (task.isCompleted) "Mark incomplete" else "Mark complete",
                                tint = if (task.isCompleted) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                        IconButton(onClick = { onEditTask(taskId) }) {
                            Icon(Icons.Default.Edit, "Edit task")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                "Delete task",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityBadge(priority = task.priority)
                task.category?.let { CategoryChip(category = it) }
                if (task.isCompleted) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (!task.description.isNullOrBlank()) {
                HorizontalDivider()
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider()
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            task.dueDate?.let { date ->
                DetailRow(
                    label = "Due Date",
                    value = buildString {
                        append(DateUtils.getRelativeTimeString(date))
                        task.dueTime?.let { append(" at $it") }
                    },
                    isOverdue = DateUtils.isPast(date) && !task.isCompleted
                )
            }
            DetailRow(label = "Created", value = DateUtils.formatDate(task.createdAt))
            task.completedAt?.let {
                DetailRow(label = "Completed", value = DateUtils.formatDateTime(it))
            }

            if (tags.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tags.forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

            if (subtasks.isNotEmpty()) {
                HorizontalDivider()
                val doneCount = subtasks.count { it.isDone }
                Text(
                    text = "Subtasks ($doneCount/${subtasks.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (subtasks.isNotEmpty()) {
                    LinearProgressIndicator(
                        progress = { doneCount.toFloat() / subtasks.size },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                subtasks.forEach { subtask ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = subtask.isDone,
                            onCheckedChange = { done ->
                                viewModel.toggleSubtaskCompletion(subtask.id, done)
                            }
                        )
                        Text(
                            text = subtask.title,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isOverdue: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isOverdue) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
