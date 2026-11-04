package com.example.flowerworld.domain.model

/** A unit a duration is counted in (watering interval, time left on a plant card); [hours] is its length. */
enum class IntervalUnit(val hours: Int) { Days(24), Hours(1) }

/**
 * [text] (a count of [unit]s) as whole hours, or null if it is not a positive whole number
 * or is too big to be stored in hours.
 */
fun parseIntervalHours(text: String, unit: IntervalUnit): Int? =
    text.trim().toLongOrNull()
        ?.takeIf { it > 0 }
        ?.times(unit.hours)
        ?.takeIf { it <= Int.MAX_VALUE }
        ?.toInt()
