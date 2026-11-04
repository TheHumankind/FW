package com.example.flowerworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/**
 * Swipeable photos, one per page, with dots at the bottom when there are several.
 * With no photos it shows one page with the [placeholder]. [pagerState] must have
 * [pageCount] pages, which is at least one.
 */
@Composable
fun PhotoPager(
    photoPaths: List<String>,
    pagerState: PagerState,
    contentDescription: String?,
    placeholder: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            PhotoBox(
                photoUri = photoPaths.getOrNull(page),
                contentDescription = contentDescription,
                placeholder = placeholder,
                modifier = Modifier.fillMaxSize()
            )
        }
        if (photoPaths.size > 1) {
            PageDots(
                count = photoPaths.size,
                selected = pagerState.currentPage,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp)
            )
        }
    }
}

/** Number of pages a [PhotoPager] needs for [photoCount] photos. */
fun photoPageCount(photoCount: Int): Int = photoCount.coerceAtLeast(1)

@Composable
private fun PageDots(count: Int, selected: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (index == selected) 1f else 0.5f)
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoPagerPreview() {
    FlowerWorldTheme {
        PhotoPager(
            photoPaths = emptyList(),
            pagerState = rememberPagerState { photoPageCount(0) },
            contentDescription = null,
            placeholder = Icons.Filled.Eco,
            modifier = Modifier.size(200.dp)
        )
    }
}
