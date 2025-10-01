package org.ghost.expensetracker.data.mappers

import org.ghost.expensetracker.data.database.entity.DueEntity
import org.ghost.expensetracker.data.models.Due

fun DueEntity.toDomainModel(): Due {
    return Due(
        id = id,
        name = name,
        description = description,
        amount = amount,
        currency = currency,
        isRecurring = isRecurring,
        recurrenceInterval = recurrenceInterval,
        recurrenceUnit = recurrenceUnit,
        lastPaymentDate = lastPaymentDate,
        profileOwnerId = profileOwnerId,
        categoryId = categoryId,
        accountId = accountId,
        cardId = cardId,
    )
}

fun Due.toEntity(): DueEntity {
    return DueEntity(
        id = id,
        profileOwnerId = profileOwnerId,
        categoryId = categoryId,
        accountId = accountId,
        cardId = cardId,
        name = name,
        description = description,
        amount = amount,
        currency = currency,
        creationTimestamp = System.currentTimeMillis(),
        isRecurring = isRecurring,
        recurrenceInterval = recurrenceInterval,
        recurrenceUnit = recurrenceUnit,
        lastPaymentDate = lastPaymentDate,
    )
}