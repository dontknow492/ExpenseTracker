package org.ghost.expensetracker.data.database.models

import org.ghost.expensetracker.core.enums.RecurringUnit

data class DueFilters(
    val query: String? = null,
    val categoryId: Long? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val currency: String? = null,
    val minDate: Long? = null,
    val maxDate: Long? = null,
    val minLastPaymentDate: Long? = null,
    val maxLastPaymentDate: Long? = null,
    val isRecurring: Boolean,
    val recurrenceInterval: Int? = null,
    val recurrenceUnit: RecurringUnit? = null,
)