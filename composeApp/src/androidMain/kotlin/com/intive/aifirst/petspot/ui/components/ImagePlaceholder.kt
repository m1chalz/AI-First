package com.intive.aifirst.petspot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable placeholder for unavailable images.
 * Shows gray background with "Image not available" text.
 *
 * @param modifier Modifier for the container
 * @param fontSize Text size (default 16sp for full-size, 14sp for smaller contexts)
 */
@Composable
fun ImagePlaceholder(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
) {
    Box(
        modifier = modifier.background(Color(0xFFE5E5E5)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Image not available",
            color = Color(0xFF6A7282),
            fontSize = fontSize,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagePlaceholderPreview() {
    MaterialTheme {
        ImagePlaceholder(modifier = Modifier.size(200.dp, 120.dp))
    }
}
