package com.example.flowerworld.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import com.example.flowerworld.ui.theme.Sage

/**
 * Photo area: shows the [photoUri] if there is one, otherwise a placeholder; tap to pick another.
 * It only displays and reports taps, the picking itself is done by [rememberPhotoPicker].
 */
@Composable
fun AppPhotoPicker(
    photoUri: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Sage.copy(alpha = 0.4f))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = stringResource(R.string.change_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = stringResource(R.string.choose_photo),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Remembers a system photo picker (no storage permission needed) and returns a function that opens it.
 * [onPicked] receives the URI of the chosen image; nothing is called if the user cancels.
 */
@Composable
fun rememberPhotoPicker(onPicked: (String) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onPicked(it.toString()) }
    }
    return remember(launcher) {
        { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPhotoPickerPreview() {
    FlowerWorldTheme {
        AppPhotoPicker(photoUri = null, onClick = {})
    }
}
