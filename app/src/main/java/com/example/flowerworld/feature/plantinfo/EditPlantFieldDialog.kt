package com.example.flowerworld.feature.plantinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.components.AppDropdown
import com.example.flowerworld.ui.components.AppTextButton
import com.example.flowerworld.ui.components.AppTextField
import com.example.flowerworld.ui.message
import com.example.flowerworld.ui.nameRes
import com.example.flowerworld.ui.theme.FlowerWorldTheme

private const val HoursInDay = 24
private val UnitFieldWidth = 116.dp

/**
 * Dialog that edits one detail of [plant] (see [PlantEdit.field]). The text lives here while the user types;
 * [onSave] gets it (and the unit of a watering interval) and the view model decides if it is valid.
 */
@Composable
fun EditPlantFieldDialog(
    plant: Plant,
    edit: PlantEdit,
    onSave: (text: String, unit: IntervalUnit) -> Unit,
    onInput: () -> Unit,
    onDismiss: () -> Unit
) {
    val field = edit.field
    // an interval of whole days is edited in days, any other in hours
    val inDays = plant.wateringIntervalHours % HoursInDay == 0
    var text by rememberSaveable(field) {
        mutableStateOf(
            when (field) {
                PlantField.Name -> plant.name
                PlantField.Title -> plant.title
                PlantField.Notes -> plant.notes
                PlantField.WateringInterval ->
                    (if (inDays) plant.wateringIntervalHours / HoursInDay else plant.wateringIntervalHours).toString()
            }
        )
    }
    var unit by rememberSaveable(field) { mutableStateOf(if (inDays) IntervalUnit.Days else IntervalUnit.Hours) }
    val label = stringResource(
        when (field) {
            PlantField.Name -> R.string.field_name
            PlantField.Title -> R.string.field_title
            PlantField.Notes -> R.string.field_notes
            PlantField.WateringInterval -> R.string.field_watering_interval
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            if (field == PlantField.WateringInterval) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            onInput()
                        },
                        label = label,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        errorText = edit.error?.message(),
                        modifier = Modifier.weight(1f)
                    )
                    AppDropdown(
                        items = IntervalUnit.entries,
                        selected = unit,
                        onSelect = {
                            unit = it
                            onInput()
                        },
                        label = null,
                        itemLabel = { stringResource(it.nameRes) },
                        // the text field starts half a label lower than a field without one
                        modifier = Modifier.padding(top = 8.dp).width(UnitFieldWidth)
                    )
                }
            } else {
                val isNotes = field == PlantField.Notes
                AppTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onInput()
                    },
                    label = label,
                    singleLine = !isNotes,
                    minLines = if (isNotes) 3 else 1,
                    imeAction = if (isNotes) ImeAction.Default else ImeAction.Done,
                    errorText = edit.error?.message(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { AppTextButton(text = stringResource(R.string.save), onClick = { onSave(text, unit) }) },
        dismissButton = { AppTextButton(text = stringResource(R.string.cancel), onClick = onDismiss) }
    )
}

@Preview(showBackground = true)
@Composable
private fun EditPlantFieldDialogPreview() {
    FlowerWorldTheme {
        EditPlantFieldDialog(
            plant = TestPlants.first(),
            edit = PlantEdit(PlantField.WateringInterval),
            onSave = { _, _ -> },
            onInput = {},
            onDismiss = {}
        )
    }
}
