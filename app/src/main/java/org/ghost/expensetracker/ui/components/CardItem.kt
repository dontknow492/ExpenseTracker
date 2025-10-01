package org.ghost.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil3.compose.AsyncImage
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.utils.CurrencyUtils
import org.ghost.expensetracker.data.models.Card

@Composable
fun CardItem(modifier: Modifier = Modifier, card: Card) {
    val cardDefaultColor = CardDefaults.cardColors().containerColor
    val cardColor: Color = remember(card.hexColor) {
        val hex = card.hexColor
        if (hex.isNullOrBlank()) {
            // If hex is null or empty, return the default color.
            cardDefaultColor
        } else {
            try {
                // Add '#' if missing and parse.
                Color(("#" + hex.removePrefix("#")).toColorInt())
            } catch (e: Exception) {
                // If parsing fails (e.g., invalid hex string),
                // fall back to the default color.
                cardDefaultColor
            }
        }
    }
    Card(
        modifier = modifier,
        colors = CardDefaults
            .cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "${card.cardCompany} Card",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                card.type.uppercase(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "**** **** **** ${card.cardLastFourDigits}",
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                Text(
                    "Card Holder: ${card.holderName}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Exp: ${card.expirationDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SimpleCardItem(modifier: Modifier = Modifier, card: Card, onClick: (Card) -> Unit) {
    val cardDefaultColor = CardDefaults.cardColors().containerColor
    val cardColor: Color = remember(card.hexColor) {
        val hex = card.hexColor
        if (hex.isNullOrBlank()) {
            // If hex is null or empty, return the default color.
            cardDefaultColor
        } else {
            try {
                // Add '#' if missing and parse.
                Color(("#" + hex.removePrefix("#")).toColorInt())
            } catch (e: Exception) {
                // If parsing fails (e.g., invalid hex string),
                // fall back to the default color.
                cardDefaultColor
            }
        }
    }
    Card(
        modifier = modifier.clickable { onClick(card) },
        colors = CardDefaults
            .cardColors(containerColor = cardColor)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "**** ${card.cardLastFourDigits}",
            modifier = Modifier
                .align(alignment = Alignment.End)
                .padding(end = 8.dp, bottom = 8.dp),
        )
    }

}

@Preview
@Composable
fun CardItemPreview() {
    val card = Card(
        id = 1,
        profileOwnerId = 1,
        balance = 1000.0,
        currency = "USD",
        holderName = "John Doe",
        type = "Debit",
        cardCompany = "Visa",
        cardLastFourDigits = 1234,
        expirationDate = 1672531199000L, // Example: Dec 31, 2022
        hexColor = "#FF5733",
        isDefault = true,
        displayOrder = 0,
    )
    CardItem(
        modifier = Modifier.size(300.dp, 180.dp),
        card = card
    )
}

@Preview
@Composable
fun SimpleCardItemPreview() {
    val card = Card(
        id = 1,
        profileOwnerId = 1,
        balance = 1000.0,
        currency = "USD",
        holderName = "John Doe",
        type = "Debit",
        cardCompany = "Visa",
        cardLastFourDigits = 1234,
        expirationDate = 1672531199000L, // Example: Dec 31, 2022
        hexColor = "#FF5733",
        isDefault = true,
        displayOrder = 0,
    )
    DraggableCardItem(
        modifier = Modifier,
        card = card,
        onClick = {},
        onEditClick = {},
        onDeleteClick = {},
        dragHandler = { Text("=") }
    )
}


@Composable
fun WideCardItem(
    modifier: Modifier = Modifier,
    card: Card,
    onClick: (Card) -> Unit
) {
    val balanceText by remember(card.currency, card.balance) {
        derivedStateOf {
            CurrencyUtils.formattedAmount(card.balance, card.currency)
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onClick(card) }
    ) {
        Box {
            AsyncImage(
                model = R.drawable.master_card,
                contentDescription = null,
                modifier = Modifier
                    .size(height = 70.dp, width = 80.dp)
                    .padding(horizontal = 8.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row {
                Text(
                    text = card.cardCompany,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,

                    )
                Text(
                    text = "**** ${card.cardLastFourDigits}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )

            }
            Text(
                text = balanceText,
                style = MaterialTheme.typography.titleMedium,
            )
        }

    }
}

@Composable
fun DraggableCardItem(
    modifier: Modifier = Modifier,
    card: Card,
    onClick: (Card) -> Unit,
    onEditClick: (Card) -> Unit,
    onDeleteClick: (Card) -> Unit,
    dragHandler: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        WideCardItem(
            modifier = Modifier.weight(1f),
            card = card,
            onClick = onClick
        )
        IconButton(
            onClick = { onDeleteClick(card) },
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete)
            )
        }
        IconButton(
            onClick = { onEditClick(card) },
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit)
            )
        }
        dragHandler()


    }

}