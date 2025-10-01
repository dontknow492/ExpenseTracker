package org.ghost.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun DropDownButton(
    modifier: Modifier = Modifier,
    filter: String,
    filters: List<String>,
    onFilterChange: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation = animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = ""
    )

    OutlinedCard(
        shape = CircleShape,
        modifier = modifier.clickable(onClick = { isExpanded = true })
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(text = filter)
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                modifier = Modifier.rotate(rotation.value),
                contentDescription = null
            )
        }
        DropdownMenu(
            isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            filters.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onFilterChange(it)
                        isExpanded = false
                    }
                )
            }
        }
    }

}