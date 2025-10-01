package org.ghost.expensetracker.data.database.entity.relation

import androidx.room.Embedded
import org.ghost.expensetracker.data.database.entity.CategoryEntity

data class CategoryEntityWithExpenseCount(
    @Embedded
    val categoryEntity: CategoryEntity,

    val expenseCount: Int
)