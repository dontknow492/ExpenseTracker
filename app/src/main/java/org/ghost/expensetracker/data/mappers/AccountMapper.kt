package org.ghost.expensetracker.data.mappers

import org.ghost.expensetracker.data.database.entity.AccountEntity
import org.ghost.expensetracker.data.models.Account

fun AccountEntity.toDomainModel(): Account {
    return Account(
        id = id,
        description = description,
        currency = currency,
        balance = balance,
        isDefault = isDefault,
        profileOwnerId = profileOwnerId,
        name = name,
        displayOrder = displayOrder
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        description = description,
        currency = currency,
        balance = balance,
        isDefault = isDefault,
        creationTimestamp = System.currentTimeMillis(),
        profileOwnerId = profileOwnerId,
        name = name,
        displayOrder = displayOrder

    )
}