package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Priority
import com.example.taskflow.ui.theme.PriorityHigh
import com.example.taskflow.ui.theme.PriorityLow
import com.example.taskflow.ui.theme.PriorityMedium
import com.example.taskflow.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val totalCount by viewModel.totalTaskCount.collectAsState()
    val completedCount by viewModel.completedTaskCount.collectAsState()
    val pendingCount by viewModel.pendingTaskCount.collectAsState()
    val overdueCount by viewModel.overdueTaskCount.collectAsState()
    val allTasks by viewModel.tasks.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val completionRate = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val highCount = allTasks.count { it.priority == Priority.HIGH }
    val mediumCount = allTasks.count { it.priority == Priority.MEDIUM }
    val lowCount = allTasks.count { it.priority == Priority.LOW }

    val topCategories = categories
        .map { cat -> cat to allTasks.count { it.category == cat } }
        .sortedByDescending { it.second }
        .take(3)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Statistics") })
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stat cards — jumla, completed, pending, overdue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatNumberCard(
                    label = "Total",
                    count = totalCount,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
                StatNumberCard(
                    label = "Done",
                    count = completedCount,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatNumberCard(
                    label = "Pending",
                    count = pendingCount,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
                StatNumberCard(
                    label = "Overdue",
                    count = overdueCount,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            }

            HorizontalDivider()

            // Completion rate
            Text(
                text = "Completion Rate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { completionRate },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(completionRate * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider()

            // Mgawanyo wa priority
            Text(
                text = "By Priority",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            PriorityBar(
                label = "High",
                count = highCount,
                total = totalCount,
                color = PriorityHigh
            )
            PriorityBar(
                label = "Medium",
                count = mediumCount,
                total = totalCount,
                color = PriorityMedium
            )
            PriorityBar(
                label = "Low",
                count = lowCount,
                total = totalCount,
                color = PriorityLow
            )

            if (topCategories.isNotEmpty()) {
                HorizontalDivider()

                Text(
                    text = "Top Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                topCategories.forEach { (category, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Text(
                            text = "$count task${if (count != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatNumberCard(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun PriorityBar(
    label: String,
    count: Int,
    total: Int,
    color: androidx.compose.ui.graphics.Color
) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(52.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(24.dp),
            fontWeight = FontWeight.Bold
        )
    }
}
