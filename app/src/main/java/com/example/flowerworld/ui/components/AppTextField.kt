package com.example.flowerworld.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import com.example.flowerworld.ui.theme.HunterGreen
import com.example.flowerworld.ui.theme.Sage

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    // Error text under the field; null means the field is valid
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        isError = errorText != null,
        supportingText = errorText?.let { { Text(it) } },
        shape = RoundedCornerShape(2.dp),
        visualTransformation = visualTransformation,
        trailingIcon = trailingContent,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HunterGreen,
            unfocusedBorderColor = Sage,
            focusedLabelColor = HunterGreen,
            cursorColor = HunterGreen
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    FlowerWorldTheme {
        AppTextField(value = "", onValueChange = {}, label = "Login")
    }
}
