package org.ghost.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.utils.CurrencyUtils
import org.ghost.expensetracker.data.models.Account

@Composable
fun WideAccountItem(
    modifier: Modifier = Modifier,
    account: Account,
    onClick: (Account) -> Unit
) {
    val balanceText by remember(account.currency, account.balance) {
        derivedStateOf {
            CurrencyUtils.formattedAmount(account.balance, account.currency)
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onClick(account) },
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box {
            AsyncImage(
                model = R.drawable.person_placeholder,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .aspectRatio(1f)
                    .clip(
                        CircleShape
                    )
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = account.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = balanceText,
                style = MaterialTheme.typography.titleMedium,
            )
        }

    }

}

@Preview
@Composable
fun DraggableAccountItemPreview() {
    val account = Account(
        id = 1L,
        profileOwnerId = 1L,
        name = "Savings Account",
        description = "My primary savings account",
        currency = "USD",
        balance = 10000.0,
        isDefault = true,
        displayOrder = 1
    )
    DraggableAccountItem(
        card = account,
        onClick = {},
        onEditClick = {},
        onDeleteClick = {},
        dragHandler = {}
    )
}

@Composable
fun DraggableAccountItem(
    modifier: Modifier = Modifier,
    card: Account,
    onClick: (Account) -> Unit,
    onEditClick: (Account) -> Unit,
    onDeleteClick: (Account) -> Unit,
    dragHandler: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        WideAccountItem(
            modifier = Modifier.weight(1f),
            account = card,
            onClick = onClick,
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