package org.ghost.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import org.ghost.expensetracker.core.enums.SortOrder


@Composable
inline fun <reified T : Enum<T>> SortListScreen(
    modifier: Modifier = Modifier,
    sortBy: T,
    sortOrder: SortOrder,
    crossinline onValueChange: (T, SortOrder) -> Unit,
) {
    val entries = enumValues<T>()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        entries.forEach { entry ->
            SortItem(
                modifier = Modifier.fillMaxWidth(),
                text = entry.name.replace('_', ' ').lowercase()
                    .replaceFirstChar { it.titlecase() },
                isAscending = sortOrder == SortOrder.ASCENDING,
                isSelected = entry == sortBy,
                onClick = {
                    if (entry == sortBy) {
                        // If it's already selected, just toggle the order
                        onValueChange(entry, sortOrder.opposite())
                    } else {
                        // If a new option is selected, set it with a default order
                        onValueChange(entry, sortOrder)
                    }
                }
            )

        }
    }
}


@Composable
fun SortItem(
    modifier: Modifier = Modifier,
    text: String,
    isAscending: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isAscending) 90f else 270f,
        label = ""
    )
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.rotate(rotation),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
        )
    }
}