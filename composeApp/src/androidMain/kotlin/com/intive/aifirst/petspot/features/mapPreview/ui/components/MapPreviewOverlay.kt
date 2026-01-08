package com.intive.aifirst.petspot.features.mapPreview.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Overlay component showing "Tap to view interactive map" hint.
 * Centered in the map preview per Figma design.
 * Specs: white bg, pill shape, shadow, 14px text #101828
 */
@Composable
fun MapPreviewOverlay(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.testTag("mapPreview.overlay"),
        shape = RoundedCornerShape(percent = 50),
        color = Color.White,
        shadowElevation = 10.dp,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Tap to view interactive map",
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                color = MapPreviewColors.OverlayText,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MapPreviewOverlayPreview() {
    MaterialTheme {
        MapPreviewOverlay(
            modifier = Modifier.padding(16.dp),
        )
    }
}
