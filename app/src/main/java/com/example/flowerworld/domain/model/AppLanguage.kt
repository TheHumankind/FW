package com.example.flowerworld.domain.model

import java.util.Locale

/** Languages the app is translated into. [tag] is the BCP 47 language tag. */
enum class AppLanguage(val tag: String) {
    English("en"),
    Polish("pl"),
    Belarusian("be"),
    Russian("ru"),
    Ukrainian("uk");

    companion object {
        fun fromTag(tag: String?): AppLanguage? = entries.firstOrNull { it.tag == tag }

        /** The language the system would pick for the app: the device language if translated, else English. */
        fun systemDefault(): AppLanguage = fromTag(Locale.getDefault().language) ?: English
    }
}
