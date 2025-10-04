package org.ghost.expensetracker.core.utils

import org.ghost.expensetracker.core.enums.ExpenseGroupBy
import org.ghost.expensetracker.core.enums.TimeFilter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object DateTimeUtils {
    fun convertMillisToHumanReadable(millis: Long): String {
        val instant = Instant.ofEpochMilli(millis)
        val zoneId = ZoneId.systemDefault()
        val zdt = instant.atZone(zoneId)

        val now = ZonedDateTime.now(zoneId)
        val yesterday = now.minusDays(1)
        return when {
            zdt.toLocalDate().isEqual(now.toLocalDate()) -> {
                "Today" + ", ${zdt.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
            }

            zdt.toLocalDate().isEqual(yesterday.toLocalDate()) -> {
                "Yesterday" + ", ${zdt.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
            }

            else -> {
                "${zdt.format(DateTimeFormatter.ofPattern("dd MMMM, yyyy"))}"
            }
        }
    }

    fun convertToDateString(millis: Long): String {
        val instant = Instant.ofEpochMilli(millis)
        val zoneId = ZoneId.systemDefault()
        val zdt = instant.atZone(zoneId)

        return "${zdt.format(DateTimeFormatter.ofPattern("dd MMMM, yyyy"))}"
    }


    fun convertToTime(millis: Long): String {
        val instant = Instant.ofEpochMilli(millis)
        val zoneId = ZoneId.systemDefault()
        val zdt = instant.atZone(zoneId)

        val formatedString = zdt.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))

        if (formatedString.startsWith("00:")) {
            return formatedString.substring(3)
        }

        return formatedString

    }

    fun mapTimeFilterToDateRange(timeFilter: TimeFilter): Pair<Long, Long> {
        val currentDate = System.currentTimeMillis()
        return when (timeFilter) {
            TimeFilter.DAY -> Pair(currentDate - (24 * 60 * 60 * 1000L), currentDate)
            TimeFilter.WEEK -> Pair(currentDate - (7 * 24 * 60 * 60 * 1000L), currentDate)
            TimeFilter.MONTH -> Pair(currentDate - (30 * 24 * 60 * 60 * 1000L), currentDate)
            TimeFilter.YEAR -> Pair(currentDate - (365 * 24 * 60 * 60 * 1000L), currentDate)
        }
    }

    fun toMMYY(value: Long?): String {
        if (value == null) return ""
        val calendar = Calendar.getInstance().apply { timeInMillis = value }
        return SimpleDateFormat("MM/yy", Locale.getDefault()).format(calendar.time)
    }

    fun fromMMYY(value: String): Long? {
        if (value.length != 5) return null
        return try {
            val date = SimpleDateFormat("MM/yy", Locale.getDefault()).parse(value)
            // Set to the end of the month for logical expiry
            val calendar = Calendar.getInstance()
            date?.let {
                calendar.time = it
                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
            }
            calendar.timeInMillis
        } catch (e: Exception) {
            null
        }
    }


    fun mapTimeFilterToExpenseGroupBy(timeFilter: TimeFilter): ExpenseGroupBy {
        return when (timeFilter) {
            TimeFilter.DAY -> ExpenseGroupBy.DAY
            TimeFilter.WEEK -> ExpenseGroupBy.WEEK
            TimeFilter.MONTH -> ExpenseGroupBy.MONTH
            TimeFilter.YEAR -> ExpenseGroupBy.YEAR
        }
    }

}