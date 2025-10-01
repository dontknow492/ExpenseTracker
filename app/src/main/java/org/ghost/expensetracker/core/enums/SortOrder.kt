package org.ghost.expensetracker.core.enums

enum class SortOrder(val value: String) {
    ASCENDING("Asc"),
    DESCENDING("Desc");

    fun opposite(): SortOrder {
        return when (this) {
            ASCENDING -> DESCENDING
            DESCENDING -> ASCENDING
        }
    }
}