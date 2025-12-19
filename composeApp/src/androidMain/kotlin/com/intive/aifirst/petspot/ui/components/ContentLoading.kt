package com.intive.aifirst.petspot.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Flexible loading indicator for inline/section loading states.
 * Unlike [FullScreenLoading], this does NOT fill the entire screen.
 *
 * Parent composable controls the size via modifier.
 *
 * @param modifier Modifier for the container Box (set height/width here)
 * @param indicatorSize Size of the CircularProgressIndicator
 * @param color Color of the indicator
 * @param testTag Test identifier for UI testing
 */
@Composable
fun ContentLoading(
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 32.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    testTag: String = "loading",
) {
    Box(
        modifier = modifier.testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            color = color,
        )
    }
}

@Preview(name = "Content Loading - Default", showBackground = true)
@Composable
private fun ContentLoadingPreview() {
    MaterialTheme {
        ContentLoading(
            modifier = Modifier.size(200.dp),
        )
    }
}

@Preview(name = "Content Loading - Custom Color", showBackground = true)
@Composable
private fun ContentLoadingCustomColorPreview() {
    MaterialTheme {
        ContentLoading(
            modifier = Modifier.size(150.dp),
            indicatorSize = 24.dp,
            color = Color(0xFF2196F3),
        )
    }
}
