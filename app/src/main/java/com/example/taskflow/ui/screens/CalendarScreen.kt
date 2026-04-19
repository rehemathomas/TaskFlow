package com.example.taskflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Priority
import com.example.taskflow.ui.components.EmptyState
import com.example.taskflow.ui.components.TaskCard
import com.example.taskflow.ui.theme.PriorityHigh
import com.example.taskflow.ui.theme.PriorityLow
import com.example.taskflow.ui.theme.PriorityMedium
import com.example.taskflow.utils.DateUtils
import com.example.taskflow.viewmodel.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayedMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    val allTasks by viewModel.tasks.collectAsState()

    val tasksOnSelectedDay by remember(selectedDate, allTasks) {
        val start = DateUtils.getStartOfDay(selectedDate.time).time
        val end = DateUtils.getEndOfDay(selectedDate.time).time
        derivedStateOf {
            allTasks.filter { task ->
                val due = task.dueDate?.time ?: return@filter false
                due in start..end
            }
        }
    }

    val tasksByDay by remember(displayedMonth, allTasks) {
        derivedStateOf {
            val monthStart = displayedMonth.clone() as Calendar
            monthStart.set(Calendar.DAY_OF_MONTH, 1)
            monthStart.set(Calendar.HOUR_OF_DAY, 0)
            monthStart.set(Calendar.MINUTE, 0)

            val monthEnd = displayedMonth.clone() as Calendar
            monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))
            monthEnd.set(Calendar.HOUR_OF_DAY, 23)
            monthEnd.set(Calendar.MINUTE, 59)

            allTasks
                .filter { task ->
                    val due = task.dueDate?.time ?: return@filter false
                    due in monthStart.timeInMillis..monthEnd.timeInMillis
                }
                .groupBy { task ->
                    val cal = Calendar.getInstance().apply { time = task.dueDate!! }
                    cal.get(Calendar.DAY_OF_MONTH)
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calendar") })
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Kichwa cha mwezi na vitufe vya mbele/nyuma
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    displayedMonth = (displayedMonth.clone() as Calendar).also {
                        it.add(Calendar.MONTH, -1)
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous month")
                }

                Text(
                    text = buildString {
                        val monthName = displayedMonth.getDisplayName(
                            Calendar.MONTH, Calendar.LONG, Locale.getDefault()
                        )
                        append(monthName)
                        append(" ")
                        append(displayedMonth.get(Calendar.YEAR))
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    displayedMonth = (displayedMonth.clone() as Calendar).also {
                        it.add(Calendar.MONTH, 1)
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next month")
                }
            }

            // Vichwa vya siku za wiki
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Grid ya siku za mwezi
            val firstDayOfMonth = (displayedMonth.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }.get(Calendar.DAY_OF_WEEK) - 1

            val daysInMonth = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            val totalCells = firstDayOfMonth + daysInMonth
            val rows = (totalCells + 6) / 7

            val today = Calendar.getInstance()

            Column(modifier = Modifier.fillMaxWidth()) {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDayOfMonth + 1

                            if (day < 1 || day > daysInMonth) {
                                Box(modifier = Modifier.weight(1f).height(52.dp))
                            } else {
                                val isToday = today.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR) &&
                                        today.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH) &&
                                        today.get(Calendar.DAY_OF_MONTH) == day

                                val isSelected = selectedDate.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR) &&
                                        selectedDate.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH) &&
                                        selectedDate.get(Calendar.DAY_OF_MONTH) == day

                                val dayTasks = tasksByDay[day] ?: emptyList()
                                val topPriority = dayTasks.minByOrNull { it.priority.ordinal }?.priority

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                        .clickable {
                                            selectedDate = (displayedMonth.clone() as Calendar).apply {
                                                set(Calendar.DAY_OF_MONTH, day)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = day.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                                isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                        if (dayTasks.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when (topPriority) {
                                                            Priority.HIGH -> PriorityHigh
                                                            Priority.MEDIUM -> PriorityMedium
                                                            Priority.LOW -> PriorityLow
                                                            null -> MaterialTheme.colorScheme.primary
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Orodha ya tasks za siku iliyochaguliwa
            val dateLabel = DateUtils.getRelativeTimeString(selectedDate.time)
            Text(
                text = "Tasks — $dateLabel",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (tasksOnSelectedDay.isEmpty()) {
                EmptyState(
                    message = "No tasks on $dateLabel",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = tasksOnSelectedDay,
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
