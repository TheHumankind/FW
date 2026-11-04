package com.example.flowerworld.domain.model

/** A unit a duration is counted in (watering interval, time left on a plant card); [hours] is its length. */
enum class IntervalUnit(val hours: Int) { Days(24), Hours(1) }
