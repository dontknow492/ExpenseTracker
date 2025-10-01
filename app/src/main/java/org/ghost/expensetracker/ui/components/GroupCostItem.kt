package org.ghost.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CostItem(
    modifier: Modifier = Modifier,
    title: String,
    currency: String,
    amount: Double,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        icon()
        Text(
            text = title,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "$amount $currency",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun CircularIndicator(modifier: Modifier = Modifier, backgroundColor: Color) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
    )
}

@Preview
@Composable
fun CostItemPreview() {
    CostItem(
        title = "Groceries",
        currency = "USD",
        amount = 50.0,
        onClick = {}
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "icon",
        )
    }
}