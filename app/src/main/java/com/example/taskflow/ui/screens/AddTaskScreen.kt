package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Priority
import com.example.taskflow.data.entity.Task
import com.example.taskflow.ui.components.TaskFlowDatePickerDialog
import com.example.taskflow.ui.components.TaskFlowTimePickerDialog
import com.example.taskflow.utils.DateUtils
import com.example.taskflow.utils.ValidationUtils
import com.example.taskflow.viewmodel.TaskViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    onTaskSaved: () -> Unit,
    editTaskId: Long? = null,
    modifier: Modifier = Modifier
) {
    val isEditMode = editTaskId != null
    val existingTask by remember(editTaskId) {
        if (editTaskId != null) viewModel.getTaskById(editTaskId)
        else kotlinx.coroutines.flow.MutableStateFlow(null)
    }.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var dueTime by remember { mutableStateOf<String?>(null) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(existingTask) {
        existingTask?.let { task ->
            title = task.title
            description = task.description ?: ""
            category = task.category ?: ""
            selectedPriority = task.priority
            dueDate = task.dueDate
            dueTime = task.dueTime
        }
    }

    if (showDatePicker) {
        TaskFlowDatePickerDialog(
            onDateSelected = { date ->
                dueDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = dueDate
        )
    }

    if (showTimePicker) {
        val initHour = dueTime?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 12
        val initMinute = dueTime?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
        TaskFlowTimePickerDialog(
            onTimeSelected = { hour, minute ->
                dueTime = "%02d:%02d".format(hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
            initialHour = initHour,
            initialMinute = initMinute
        )
    }

    fun validate(): Boolean {
        titleError = ValidationUtils.validateTitle(title)
        descriptionError = ValidationUtils.validateDescription(description)
        categoryError = ValidationUtils.validateCategory(category)
        return titleError == null && descriptionError == null && categoryError == null
    }

    fun handleSave() {
        if (!validate()) return

        val cleanTitle = ValidationUtils.sanitizeInput(title)
        val cleanDesc = description.trim().ifBlank { null }
        val cleanCat = category.trim().ifBlank { null }

        if (isEditMode && existingTask != null) {
            val updated = existingTask!!.copy(
                title = cleanTitle,
                description = cleanDesc,
                category = cleanCat,
                priority = selectedPriority,
                dueDate = dueDate,
                dueTime = dueTime,
                updatedAt = Date()
            )
            viewModel.updateTask(updated) { onTaskSaved() }
        } else {
            val newTask = Task(
                title = cleanTitle,
                description = cleanDesc,
                category = cleanCat,
                priority = selectedPriority,
                dueDate = dueDate,
                dueTime = dueTime
            )
            viewModel.insertTask(newTask) { onTaskSaved() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Task" else "Add Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null
                },
                label = { Text("Task Title *") },
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = null
                },
                label = { Text("Description") },
                isError = descriptionError != null,
                supportingText = descriptionError?.let { { Text(it) } },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                    categoryError = null
                },
                label = { Text("Category") },
                isError = categoryError != null,
                supportingText = categoryError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = "Due Date & Time",
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dueDate?.let { DateUtils.formatDate(it) } ?: "Set date"
                    )
                }

                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = dueTime ?: "Set time")
                }
            }

            if (dueDate != null || dueTime != null) {
                TextButton(
                    onClick = {
                        dueDate = null
                        dueTime = null
                    }
                ) {
                    Text("Clear date & time")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { handleSave() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Update Task" else "Save Task")
            }
        }
    }
}
