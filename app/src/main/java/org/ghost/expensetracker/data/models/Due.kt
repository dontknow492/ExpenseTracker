package org.ghost.expensetracker.data.models

data class Due(
    val id: Long,
    val profileOwnerId: Long,
    val categoryId: Long,
    val accountId: Long?,
    val cardId: Long?,
    val name: String,
    val description: String?,
    val amount: Double,
    val currency: String,
    val isRecurring: Boolean,
    val recurrenceInterval: Int?,
    val recurrenceUnit: String?,
    val lastPaymentDate: Long?,
)