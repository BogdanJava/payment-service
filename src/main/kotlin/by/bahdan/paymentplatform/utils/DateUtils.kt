package by.bahdan.paymentplatform.utils

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date

fun parseISODate(date: String): Date {
    return Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(date)))
}

fun Date.toISODateString(): String {
    return DateTimeFormatter.ISO_INSTANT.format(this.toInstant())
}
