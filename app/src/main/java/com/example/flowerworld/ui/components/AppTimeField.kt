package com.example.flowerworld.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** A read-only field that shows a time of day and opens a clock dialog to change it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimeField(
    time: LocalTime,
    onTimeSelect: (LocalTime) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    // a read-only text field does not report clicks; the end of a press is the click
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { if (it is PressInteraction.Release) showDialog = true }
    }

    OutlinedTextField(
        value = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text(label) },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(Dimens.ControlCornerRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    )

    if (showDialog) {
        val state = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = DateFormat.is24HourFormat(LocalContext.current)
        )
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = { TimePicker(state = state) },
            confirmButton = {
                AppTextButton(
                    text = stringResource(R.string.action_ok),
                    onClick = {
                        showDialog = false
                        onTimeSelect(LocalTime.of(state.hour, state.minute))
                    }
                )
            },
            dismissButton = {
                AppTextButton(text = stringResource(R.string.cancel), onClick = { showDialog = false })
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTimeFieldPreview() {
    FlowerWorldTheme {
        AppTimeField(time = LocalTime.of(9, 0), onTimeSelect = {}, label = "Reminder time")
    }
}
