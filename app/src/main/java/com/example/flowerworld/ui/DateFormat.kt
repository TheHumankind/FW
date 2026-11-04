package com.example.flowerworld.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val DateOnly = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
private val DateAndTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

/** [epochMillis] as a date in the user's locale and time zone, e.g. "Nov 4, 2026". */
fun formatDate(epochMillis: Long): String = format(DateOnly, epochMillis)

/** [epochMillis] as a date and time in the user's locale and time zone, e.g. "Nov 4, 2026, 8:49 PM". */
fun formatDateTime(epochMillis: Long): String = format(DateAndTime, epochMillis)

private fun format(formatter: DateTimeFormatter, epochMillis: Long): String =
    formatter.withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(epochMillis))
