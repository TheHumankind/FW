package com.example.flowerworld.domain.model

import java.time.Month

/** Seasons of the northern hemisphere, which the care data is written for. */
enum class Season {
    Spring, Summer, Autumn, Winter;

    companion object {
        fun of(month: Month): Season = when (month) {
            Month.MARCH, Month.APRIL, Month.MAY -> Spring
            Month.JUNE, Month.JULY, Month.AUGUST -> Summer
            Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Autumn
            else -> Winter
        }
    }
}
