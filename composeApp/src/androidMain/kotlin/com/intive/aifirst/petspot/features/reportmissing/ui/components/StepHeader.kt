@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * Header bar for data collection screens in Missing Pet Report flow.
 * Displays back button, centered title, and progress indicator.
 * Used on Steps 1-4 (NOT on Summary screen).
 *
 * @param title Screen title text
 * @param currentStep Current step number (1-4)
 * @param modifier Modifier for the component
 * @param totalSteps Total number of steps (default 4)
 * @param onBackClick Callback when back button is clicked
 */
@Composable
fun StepHeader(
    title: String,
    currentStep: Int,
    modifier: Modifier = Modifier,
    totalSteps: Int = 4,
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier =
                Modifier
                    .size(40.dp)
                    .border(0.667.dp, Color(0xFFE5E9EC), CircleShape)
                    .testTag("reportMissing.header.backButton"),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF2D2D2D),
            )
        }

        // Title (centered)
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .testTag("reportMissing.header.title"),
        )

        // Progress Indicator
        StepProgressIndicator(
            currentStep = currentStep,
            totalSteps = totalSteps,
        )
    }
}

@Preview(name = "Step Header - Step 1", showBackground = true)
@Composable
private fun StepHeaderPreview1() {
    MaterialTheme {
        StepHeader(
            title = "Microchip number",
            currentStep = 1,
        )
    }
}

@Preview(name = "Step Header - Step 3", showBackground = true)
@Composable
private fun StepHeaderPreview3() {
    MaterialTheme {
        StepHeader(
            title = "Animal description",
            currentStep = 3,
        )
    }
}
