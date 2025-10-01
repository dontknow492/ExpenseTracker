package org.ghost.expensetracker.data.models


data class Expense(
    val id: Long,
    val accountId: Long?,
    val profileOwnerId: Long,
    val categoryId: Long,
    val cardId: Long?,
    val amount: Double,
    val currency: String,
    val isSend: Boolean,
    val title: String,
    val description: String?,
    val date: Long,
    val sourceDueId: Long?,
    val iconName: String?,
    val imageUri: String?,
)
