package org.ghost.expensetracker.data.mappers

import org.ghost.expensetracker.data.database.entity.CategoryEntity
import org.ghost.expensetracker.data.models.Category

fun CategoryEntity.toDomainModel(): Category {
    return Category(
        id = id,
        name = name,
        colorHex = colorHex,
        iconName = iconName,
        profileOwnerId = profileOwnerId,
        displayOrder = displayOrder,

        )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        profileOwnerId = profileOwnerId,
        name = name,
        colorHex = colorHex,
        iconName = iconName,
        displayOrder = displayOrder
    )
}