package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.data.models.CategoryWithExpenseCount

data class CategoryScreenActions(
    val onNavigateBackClick: () -> Unit,
    val onAddCategoryClick: () -> Unit,
    val onCategoryClick: (CategoryWithExpenseCount) -> Unit,
    val onEditCategoryClick: (CategoryWithExpenseCount) -> Unit,
    val onDeleteCategoryClick: (CategoryWithExpenseCount) -> Unit,
    val onMoveCategory: (Int, Int) -> Unit,
)