package org.ghost.expensetracker.core.ui.states

import org.ghost.expensetracker.data.models.Account

data class AccountsUiState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)