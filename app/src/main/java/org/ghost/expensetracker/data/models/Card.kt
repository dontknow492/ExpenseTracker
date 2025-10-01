package org.ghost.expensetracker.data.models

data class Card(
    val id: Long,
    val profileOwnerId: Long,
    val balance: Double,
    val currency: String,
    val holderName: String,
    val type: String,
    val cardCompany: String,
    val cardLastFourDigits: Int,
    val expirationDate: Long?,
    val hexColor: String?,
    val isDefault: Boolean,
    val displayOrder: Int,
)