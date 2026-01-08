package com.intive.aifirst.petspot.features.mapPreview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Legend dot size: 12px per design spec */
private val DOT_SIZE = 12.dp

/**
 * Legend component for map preview showing pin color meanings.
 * Displays red dot for "Missing" and blue dot for "Found".
 * Positioned above the map in header section per Figma design.
 */
@Composable
fun MapPreviewLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Missing - Red
        LegendItem(
            color = MapPreviewColors.MissingPin,
            label = "Missing",
        )

        // Found - Blue
        LegendItem(
            color = MapPreviewColors.FoundPin,
            label = "Found",
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(DOT_SIZE)
                    .background(color, CircleShape),
        )
        Text(
            text = label,
            style =
                MaterialTheme.typography.labelMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
            color = MapPreviewColors.LegendText,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MapPreviewLegendPreview() {
    MaterialTheme {
        MapPreviewLegend(
            modifier = Modifier.padding(16.dp),
        )
    }
}
