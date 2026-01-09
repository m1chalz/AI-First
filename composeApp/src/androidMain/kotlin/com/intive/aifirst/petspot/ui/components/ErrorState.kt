package com.intive.aifirst.petspot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Reusable error state display with retry button.
 * Shows warning icon, title, error message, and retry action.
 *
 * @param error Error message to display (null shows default message)
 * @param onRetryClick Callback when retry button is clicked
 * @param modifier Modifier for the root container
 * @param testTagPrefix Prefix for test tags (e.g., "petDetails" produces "petDetails.error")
 * @param fillMaxSize Whether to fill max size (true for full-screen, false for inline)
 */
@Composable
fun ErrorState(
    error: String?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTagPrefix: String = "error",
    fillMaxSize: Boolean = true,
) {
    Column(
        modifier =
            modifier
                .then(if (fillMaxSize) Modifier.fillMaxSize() else Modifier)
                .padding(24.dp)
                .testTag("$testTagPrefix.error"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            // App theme error red
            tint = Color(0xFFFB2C36),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error ?: "Unknown error occurred",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetryClick,
            modifier = Modifier.testTag("$testTagPrefix.retryButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF155DFC),
                    contentColor = Color.White,
                ),
        ) {
            Text("Try Again")
        }
    }
}

@Preview(name = "Error State - Full Screen", showBackground = true)
@Composable
private fun ErrorStateFullScreenPreview() {
    MaterialTheme {
        ErrorState(
            error = "Failed to load data. Please check your connection.",
            onRetryClick = {},
            testTagPrefix = "preview",
        )
    }
}

@Preview(name = "Error State - Inline", showBackground = true)
@Composable
private fun ErrorStateInlinePreview() {
    MaterialTheme {
        ErrorState(
            error = "Network error",
            onRetryClick = {},
            testTagPrefix = "preview",
            fillMaxSize = false,
        )
    }
}
