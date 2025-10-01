package org.ghost.expensetracker.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Long.convertMillisToHumanReadable(): String {
    val millis = this
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