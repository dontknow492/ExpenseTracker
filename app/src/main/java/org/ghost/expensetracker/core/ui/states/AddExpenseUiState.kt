package org.ghost.expensetracker.core.ui.states

import android.net.Uri
import org.ghost.expensetracker.core.utils.getSafeDefaultCurrencyCode
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Category

data class AddExpenseUiState(
    //data
    val account: Account? = null, // selected account
    val category: Category? = null, // selected category
    val card: Card? = null, // selected card
    val amount: String = "",
    val currency: String = getSafeDefaultCurrencyCode(),
    val isSend: Boolean = true,
    val title: String = "",
    val description: String? = null,
    val sourceDueId: Long? = null,
    val iconId: Int? = null,
    val imageUri: Uri? = null,
    // validation
    val isAccountError: Boolean = false,
    val isCategoryError: Boolean = false,
    val isCardError: Boolean = false,
    val isAmountError: Boolean = false,
    val isCurrencyError: Boolean = false,
    val isTitleError: Boolean = false,
    val isSourceError: Boolean = false,

    //ui
    val isLoading: Boolean = false,
    val error: String? = null,
    val isExpenseSaved: Boolean = false
)