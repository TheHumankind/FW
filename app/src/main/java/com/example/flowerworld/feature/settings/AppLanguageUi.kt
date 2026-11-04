package com.example.flowerworld.feature.settings

import androidx.annotation.StringRes
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.AppLanguage

/** The language's own name; it is the same in every app language, so the user can always find theirs. */
@get:StringRes
val AppLanguage.nameRes: Int
    get() = when (this) {
        AppLanguage.English -> R.string.language_english
        AppLanguage.Polish -> R.string.language_polish
        AppLanguage.Belarusian -> R.string.language_belarusian
        AppLanguage.Russian -> R.string.language_russian
        AppLanguage.Ukrainian -> R.string.language_ukrainian
    }

/** Emoji flag of the country the language is mostly associated with. */
val AppLanguage.flag: String
    get() = when (this) {
        AppLanguage.English -> "🇬🇧" // GB
        AppLanguage.Polish -> "🇵🇱" // PL
        AppLanguage.Belarusian -> "🇧🇾" // BY
        AppLanguage.Russian -> "🇷🇺" // RU
        AppLanguage.Ukrainian -> "🇺🇦" // UA
    }
