package com.example.flowerworld.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.flowerworld.R

/** Why a form field is not valid; the screen turns it into a message with [message]. */
enum class FieldError { Required, InvalidNumber }

@Composable
fun FieldError.message(): String = when (this) {
    FieldError.Required -> stringResource(R.string.error_required)
    FieldError.InvalidNumber -> stringResource(R.string.error_invalid_number)
}
