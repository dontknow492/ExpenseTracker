package org.ghost.expensetracker.core.ui.actions

import android.net.Uri
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Category

data class AddExpenseScreenActions(
    val onNavigateBackClick: () -> Unit,
    val onIsSendChange: (Boolean) -> Unit,
    val onCategoryChange: (Category) -> Unit,
    val onAccountChange: (Account) -> Unit,
    val onCardChange: (Card) -> Unit,
    val onAmountChange: (String) -> Unit,
    val onCurrencyChange: (String) -> Unit,
    val onTitleChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onSourceDueChange: (Long?) -> Unit,
    val onIconIdChange: (Int?) -> Unit,
    val onImageUriChange: (Uri?) -> Unit,
    val addExpense: () -> Unit,
    val onAddNewCategory: () -> Unit,
    val onAddNewAccount: () -> Unit,
    val onAddNewCard: () -> Unit,
)