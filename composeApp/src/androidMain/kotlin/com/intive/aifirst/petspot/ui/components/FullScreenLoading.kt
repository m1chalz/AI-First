@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Full-screen loading indicator with centered circular progress.
 * Reusable across all screens requiring a loading state.
 *
 * @param modifier Optional modifier for customization
 * @param testTag Test identifier for UI testing (e.g., "petDetails.loading", "animalList.loading")
 */
@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier,
    testTag: String = "loading",
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(name = "Full Screen Loading", showBackground = true)
@Composable
private fun FullScreenLoadingPreview() {
    MaterialTheme {
        FullScreenLoading(testTag = "preview.loading")
    }
}
