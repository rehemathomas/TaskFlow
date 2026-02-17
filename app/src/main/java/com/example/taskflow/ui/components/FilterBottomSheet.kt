package com.example.taskflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Priority

/**
 * Bottom sheet for advanced filtering options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedPriority: Priority?,
    selectedCategory: String?,
    categories: List<String>,
    showCompleted: Boolean,
    onPrioritySelected: (Priority?) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onShowCompletedChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Divider()
            }

            item {
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                PriorityFilterRow(
                    selectedPriority = selectedPriority,
                    onPrioritySelected = onPrioritySelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Divider()
            }

            item {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                CategoryFilterRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Divider()
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Show completed",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Switch(
                        checked = showCompleted,
                        onCheckedChange = onShowCompletedChanged
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        onPrioritySelected(null)
                        onCategorySelected(null)
                        onShowCompletedChanged(true)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear all filters")
                }
            }
        }
    }
}
