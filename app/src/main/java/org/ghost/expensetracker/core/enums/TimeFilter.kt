package org.ghost.expensetracker.core.enums

enum class TimeFilter {
    DAY,
    WEEK,
    MONTH,
    YEAR;

    override fun toString(): String {
        return when (this) {
            DAY -> "Day"
            WEEK -> "Week"
            MONTH -> "Month"
            YEAR -> "Year"
        }
    }

    companion object {
        fun fromString(value: String): TimeFilter {
            return when (value) {
                "Day" -> DAY
                "Week" -> WEEK
                "Month" -> MONTH
                "Year" -> YEAR
                else -> throw IllegalArgumentException("Invalid TimeFilter value: $value")
            }
        }

        fun fromStringOrDefault(value: String, default: TimeFilter): TimeFilter {
            return when (value) {
                "Day" -> DAY
                "Week" -> WEEK
                "Month" -> MONTH
                "Year" -> YEAR
                else -> default
            }
        }
    }


}