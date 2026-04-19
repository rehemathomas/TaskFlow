package com.example.taskflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskflow.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            HorizontalDivider()

            SortOption.entries.forEach { option ->
                SortOptionItem(
                    label = option.toDisplayString(),
                    isSelected = currentSort == option,
                    onClick = {
                        onSortSelected(option)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun SortOptionItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun SortOption.toDisplayString(): String {
    return when (this) {
        SortOption.DATE_ASC -> "Date (Oldest first)"
        SortOption.DATE_DESC -> "Date (Newest first)"
        SortOption.PRIORITY_HIGH_FIRST -> "Priority (High first)"
        SortOption.PRIORITY_LOW_FIRST -> "Priority (Low first)"
        SortOption.TITLE_A_TO_Z -> "Title (A to Z)"
        SortOption.TITLE_Z_TO_A -> "Title (Z to A)"
    }
}
