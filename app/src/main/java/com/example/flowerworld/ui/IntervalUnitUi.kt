package com.example.flowerworld.ui

import androidx.annotation.StringRes
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.IntervalUnit

/** Plural name of the unit ("Days", "Hours"), for selectors. */
@get:StringRes
val IntervalUnit.nameRes: Int
    get() = when (this) {
        IntervalUnit.Days -> R.string.unit_days
        IntervalUnit.Hours -> R.string.unit_hours
    }
