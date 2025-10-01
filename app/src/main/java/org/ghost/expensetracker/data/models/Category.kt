package org.ghost.expensetracker.data.models

data class Category(
    val id: Long,
    val profileOwnerId: Long,
    val name: String,
    val colorHex: String?,
    val iconName: String?,
    val displayOrder: Int
)