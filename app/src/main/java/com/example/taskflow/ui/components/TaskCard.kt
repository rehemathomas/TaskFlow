package com.example.taskflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Task
import com.example.taskflow.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card component displaying task information
 * Material 3 design with checkbox, title, metadata, and actions
 *
 * @param task Task data to display
 * @param onClick Callback when card is clicked
 * @param onCheckedChange Callback when checkbox is toggled
 * @param modifier Modifier for the card
 */
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                // Description
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Metadata row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority badge
                    PriorityBadge(priority = task.priority)

                    // Category chip
                    task.category?.let { category ->
                        CategoryChip(category = category)
                    }

                    // Due date
                    task.dueDate?.let { date ->
                        Text(
                            text = formatDate(date),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (date.before(Date()) && !task.isCompleted) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            // More options button
            IconButton(onClick = { /* Show menu */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }
    }
}

/**
 * Format date for display
 */
private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(date)
}
