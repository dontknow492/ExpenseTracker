package org.ghost.expensetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.utils.DateTimeUtils
import org.ghost.expensetracker.data.models.Expense

@Composable
fun ExpenseItem(
    modifier: Modifier = Modifier,
    expense: Expense,
    isSelected: Boolean = false,
    onLongClick: (Expense) -> Unit,
    onClick: (Expense) -> Unit,
) {
    // Animate the background color for a smooth transition
    val cardColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
        label = "cardColorAnimation"
    )

    Card(
        modifier = modifier
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { onClick(expense) },
                onLongClick = { onLongClick(expense) }
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Box(
            modifier = Modifier
                .border(
                    2.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    CardDefaults.shape
                )
                .padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            // Main content of the item
            ExpenseItemContent(
                expense = expense,
                modifier = Modifier.matchParentSize()
            )
            if (isSelected) {
                SelectionIndicator(
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }

    }
}

@Composable
private fun ExpenseItemContent(
    modifier: Modifier = Modifier,
    expense: Expense,
) {

    val amountString = remember(expense.amount) {
        String.format("%.2f", expense.amount) + " ${expense.currency}"
    }

    val avatarInitials = remember(expense.title) {
        // The calculation logic is the same, but now it directly returns a value.
        when {
            expense.title.isEmpty() -> "?"
            expense.title.split(" ").size == 1 -> {
                val singleWord = expense.title
                if (singleWord.length < 2) {
                    singleWord.getOrNull(0)?.uppercase() ?: "?"
                } else {
                    singleWord.substring(0, 2).uppercase()
                }
            }

            else -> {
                expense.title
                    .split(" ")
                    .joinToString("") { it.getOrNull(0)?.uppercase() ?: "" }
                    .take(2) // .take() is a simpler way to get the first 2 chars
            }
        }
    }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ImageItem(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(0.75f)),
            model = null,
            contentDescription = "image",
            errorComposable = {
                Text(
                    text = avatarInitials,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
            },
        )
        Column(
            modifier = Modifier.weight(1f),
            //            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = expense.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = DateTimeUtils.convertMillisToHumanReadable(expense.date),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            //            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (expense.isSend) {
                Text(
                    text = "-${amountString}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red.copy(alpha = 0.75f)
                )
                Text(
                    text = stringResource(R.string.transfer),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "+${amountString}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Green.copy(alpha = 0.75f)
                )
                Text(
                    text = stringResource(R.string.receive),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }

}


@Preview(showSystemUi = !true)
@Composable
private fun ExpenseItemPreview() {
    var isSelected by remember { mutableStateOf(!false) }
    val expense = Expense(
        id = 1L,
        accountId = 1L,
        profileOwnerId = 1L,
        categoryId = 1L,
        cardId = 1L,
        amount = 100.0,
        isSend = !true,
        title = "Lunch Dinner Minior Pad",
        description = "Lunch with friends",
        date = System.currentTimeMillis(),
        sourceDueId = 1L,
        iconName = "asdf",
        imageUri = null,
        currency = "USD"
    )
    ExpenseItem(
        modifier = Modifier.padding(top = 30.dp),
        expense = expense,
        onClick = {},
        isSelected = isSelected,
        onLongClick = { isSelected = !isSelected }
    )
}

@Composable
fun ImageItem(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String,
    errorComposable: @Composable () -> Unit,
) {
    var isError by remember { mutableStateOf(false) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = model,
            onError = {
                isError = true
            },
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize()
        )
        if (isError) {
            errorComposable()
        }
    }

}


@Composable
private fun SelectionIndicator(modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.size(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.selected), // For accessibility
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}