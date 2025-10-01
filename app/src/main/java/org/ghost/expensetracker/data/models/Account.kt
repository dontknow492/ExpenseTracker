package org.ghost.expensetracker.data.models

data class Account(
    val id: Long,
    val profileOwnerId: Long,
    val name: String,
    val description: String?,
    val currency: String,
    val balance: Double,
    val isDefault: Boolean,
    val displayOrder: Int,
)