package org.ghost.expensetracker.data.database.models

data class ExpenseFilters(
    val isSend: Boolean? = null,
    val query: String? = null,
    val accountId: Long? = null,
    val cardId: Long? = null,
    val categoryId: Long? = null,
    val minDate: Long? = null,
    val maxDate: Long? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
)