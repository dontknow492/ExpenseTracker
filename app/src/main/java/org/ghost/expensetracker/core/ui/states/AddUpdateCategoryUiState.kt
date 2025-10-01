package org.ghost.expensetracker.core.ui.states

import androidx.compose.ui.graphics.Color

data class AddUpdateCategoryUiState(
    val name: String = "",
    val color: Color? = null,
    val iconId: Int? = null,

    val isNameError: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCategorySaved: Boolean = false
)