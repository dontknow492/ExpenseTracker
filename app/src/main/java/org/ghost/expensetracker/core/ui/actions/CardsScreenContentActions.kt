package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.core.enums.CardSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.models.Card

data class CardsScreenContentActions(
    val onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    val onDelete: (card: Card) -> Unit,
    val onEdit: (card: Card) -> Unit,
    val onSortByChange: (sortBy: CardSortBy) -> Unit,
    val onSortOrderChange: (sortOrder: SortOrder) -> Unit,
    val onQueryChange: (query: String) -> Unit,
)