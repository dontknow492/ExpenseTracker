package org.ghost.expensetracker.data.mappers

import org.ghost.expensetracker.data.database.entity.relation.CategoryEntityWithExpenseCount
import org.ghost.expensetracker.data.models.CategoryWithExpenseCount

fun CategoryEntityWithExpenseCount.toDomainModel(): CategoryWithExpenseCount {
    return CategoryWithExpenseCount(
        category = this.categoryEntity.toDomainModel(),
        expenseCount = this.expenseCount
    )
}

fun CategoryWithExpenseCount.toEntity(): CategoryEntityWithExpenseCount {
    return CategoryEntityWithExpenseCount(
        categoryEntity = this.category.toEntity(),
        expenseCount = this.expenseCount
    )
}


