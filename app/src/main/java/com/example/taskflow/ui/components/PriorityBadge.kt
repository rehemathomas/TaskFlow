package com.example.taskflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.entity.Priority
import com.example.taskflow.ui.theme.*

/**
 * Badge component displaying task priority with color coding
 *
 * @param priority Task priority level
 * @param modifier Modifier for the badge
 */
@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (priority) {
        Priority.HIGH -> Triple(
            PriorityHighContainer,
            OnPriorityHighContainer,
            "HIGH"
        )
        Priority.MEDIUM -> Triple(
            PriorityMediumContainer,
            OnPriorityMediumContainer,
            "MEDIUM"
        )
        Priority.LOW -> Triple(
            PriorityLowContainer,
            OnPriorityLowContainer,
            "LOW"
        )
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
