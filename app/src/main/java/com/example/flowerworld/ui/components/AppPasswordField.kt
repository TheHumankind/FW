package com.example.flowerworld.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import com.example.flowerworld.ui.theme.HunterGreen

/** Поле пароля: скрывает символы и позволяет временно показать их. */
@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        imeAction = imeAction,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingContent = {
            Text(
                text = stringResource(if (visible) R.string.hide_password else R.string.show_password),
                style = MaterialTheme.typography.labelMedium,
                color = HunterGreen,
                modifier = Modifier
                    .clickable(role = Role.Button) { visible = !visible }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppPasswordFieldPreview() {
    FlowerWorldTheme {
        AppPasswordField(value = "secret", onValueChange = {}, label = "Password")
    }
}
