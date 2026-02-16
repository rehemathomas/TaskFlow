package com.example.taskflow.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Priority

/**
 * Row of priority filter chips
 */
@Composable
fun PriorityFilterRow(
    selectedPriority: Priority?,
    onPrioritySelected: (Priority?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedPriority == null,
            onClick = { onPrioritySelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedPriority == null) {
                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else null
        )

        Priority.entries.forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPrioritySelected(priority) },
                label = { Text(priority.name) },
                leadingIcon = if (selectedPriority == priority) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

/**
 * Row of category filter chips
 */
@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedCategory == null) {
                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else null
        )

        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                leadingIcon = if (selectedCategory == category) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}
