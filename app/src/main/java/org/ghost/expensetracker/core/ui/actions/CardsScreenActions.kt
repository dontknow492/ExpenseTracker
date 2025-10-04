package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.ui.navigation.AppRoute

data class CardsScreenActions(
    val onNavigationItemClick: (AppRoute) -> Unit,
    val onAddCardClick: (Long) -> Unit,
    val onCardClick: (Card) -> Unit,
)