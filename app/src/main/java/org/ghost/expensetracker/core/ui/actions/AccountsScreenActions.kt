package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.data.models.Account

data class AccountsScreenActions(
    val onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    val onDelete: (account: Account) -> Unit,
    val onEdit: (account: Account) -> Unit,
    val onAdd: (Account) -> Unit,
)