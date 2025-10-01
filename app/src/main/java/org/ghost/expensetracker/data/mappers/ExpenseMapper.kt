package org.ghost.expensetracker.data.mappers

import org.ghost.expensetracker.data.database.entity.ExpenseEntity
import org.ghost.expensetracker.data.models.Expense

fun ExpenseEntity.toDomainModel(): Expense {
    return Expense(
        id = id,
        amount = amount,
        title = title,
        description = description,
        date = date,
        iconName = iconName,
        imageUri = imageUri,
        sourceDueId = sourceDueId,
        isSend = this.isSend,
        accountId = accountId,
        cardId = cardId,
        profileOwnerId = profileOwnerId,
        categoryId = categoryId,
        currency = currency
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = this.id,
        profileOwnerId = profileOwnerId,
        accountId = accountId,
        cardId = cardId,
        categoryId = categoryId,
        sourceDueId = this.sourceDueId,
        amount = this.amount,
        title = this.title,
        description = this.description,
        date = this.date,
        iconName = this.iconName,
        imageUri = this.imageUri,
        isSend = this.isSend,
        currency = this.currency
    )
}