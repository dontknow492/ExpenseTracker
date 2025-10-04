package org.ghost.expensetracker.core.ui.states

import org.ghost.expensetracker.core.enums.CardSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.models.Card

data class CardsUiState(
    val query: String = "",
    val sortBy: CardSortBy = CardSortBy.DISPLAY_ORDER,
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    val cards: List<Card> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)