package com.example.flowerworld.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/**
 * Explains that reminders may be killed by the phone's power saving and offers shortcuts to the
 * settings that prevent it. [onAutostartClick] is null when the device has no autostart screen.
 */
@Composable
fun ReminderHintDialog(
    onBatteryClick: () -> Unit,
    onAutostartClick: (() -> Unit)?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(stringResource(R.string.reminder_hint_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.reminder_hint_text),
                    style = MaterialTheme.typography.bodyMedium
                )
                AppTextButton(text = stringResource(R.string.reminder_hint_battery), onClick = onBatteryClick)
                if (onAutostartClick != null) {
                    AppTextButton(text = stringResource(R.string.reminder_hint_autostart), onClick = onAutostartClick)
                }
            }
        },
        confirmButton = {
            AppTextButton(text = stringResource(R.string.reminder_hint_dismiss), onClick = onDismiss)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ReminderHintDialogPreview() {
    FlowerWorldTheme {
        ReminderHintDialog(onBatteryClick = {}, onAutostartClick = {}, onDismiss = {})
    }
}
