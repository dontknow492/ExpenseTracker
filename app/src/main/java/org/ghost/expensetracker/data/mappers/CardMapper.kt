package org.ghost.expensetracker.data.mappers

import org.ghost.expensetracker.data.database.entity.CardEntity
import org.ghost.expensetracker.data.models.Card

fun CardEntity.toDomainModel(): Card {
    return Card(
        id = id,
        holderName = holderName,
        type = type,
        cardCompany = cardCompany,
        cardLastFourDigits = cardLastFourDigits,
        expirationDate = expirationDate,
        hexColor = hexColor,
        isDefault = isDefault,
        balance = balance,
        currency = currency,
        profileOwnerId = profileOwnerId,
        displayOrder = displayOrder
    )
}


fun Card.toEntity(): CardEntity {
    return CardEntity(
        id = id,
        holderName = holderName,
        type = type,
        cardCompany = cardCompany,
        cardLastFourDigits = cardLastFourDigits,
        expirationDate = expirationDate,
        hexColor = hexColor,
        isDefault = isDefault,
        addedAt = System.currentTimeMillis(),
        profileOwnerId = profileOwnerId,
        balance = balance,
        currency = currency,
        displayOrder = displayOrder
    )
}