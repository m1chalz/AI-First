package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

/**
 * Standardized screen layout for the Report Missing Pet flow.
 * Provides consistent structure with:
 * - Status bar padding (edge-to-edge support)
 * - StepHeader with back button and progress
 * - Title + subtitle section
 * - Scrollable content area
 * - Continue button at bottom
 *
 * @param headerTitle Title displayed in StepHeader
 * @param currentStep Current step number (1-4) for progress indicator
 * @param title Main title text
 * @param subtitle Secondary description text
 * @param onBackClick Callback when back button is clicked
 * @param onContinueClick Callback when continue button is clicked
 * @param modifier Modifier for the layout
 * @param continueButtonText Text for continue button (defaults to "Continue")
 * @param scrollable Whether the content area is scrollable (defaults to true)
 * @param content Screen-specific content slot
 */
@Composable
fun ReportMissingScreenLayout(
    headerTitle: String,
    currentStep: Int,
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    continueButtonText: String = "Continue",
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding(),
    ) {
        // Header with back button, title, and progress indicator
        StepHeader(
            title = headerTitle,
            currentStep = currentStep,
            onBackClick = onBackClick,
        )

        // Main content area
        val contentModifier =
            if (scrollable) {
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            } else {
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            }

        Column(modifier = contentModifier) {
            Spacer(modifier = Modifier.height(24.dp))

            ScreenTitleSection(
                title = title,
                subtitle = subtitle,
            )

            Spacer(modifier = Modifier.height(24.dp))

            content()
        }

        // Continue button at bottom
        ContinueButton(
            onClick = onContinueClick,
            text = continueButtonText,
        )
    }
}

// ========================================
// Preview
// ========================================

private data class ReportMissingScreenLayoutState(
    val headerTitle: String,
    val currentStep: Int,
    val title: String,
    val subtitle: String,
)

private class ReportMissingScreenLayoutProvider : PreviewParameterProvider<ReportMissingScreenLayoutState> {
    override val values =
        sequenceOf(
            ReportMissingScreenLayoutState(
                headerTitle = "Microchip number",
                currentStep = 1,
                title = "Identification by Microchip",
                subtitle = "Microchip identification is the most efficient way to reunite with your pet.",
            ),
            ReportMissingScreenLayoutState(
                headerTitle = "Animal photo",
                currentStep = 2,
                title = "Your pet's photo",
                subtitle = "Please upload a photo of the missing animal.",
            ),
            ReportMissingScreenLayoutState(
                headerTitle = "Animal description",
                currentStep = 3,
                title = "Your pet's details",
                subtitle = "Fill out the details about the missing animal.",
            ),
        )
}

@Preview(name = "Report Missing Screen Layout", showBackground = true)
@Composable
private fun ReportMissingScreenLayoutPreview(
    @PreviewParameter(ReportMissingScreenLayoutProvider::class) state: ReportMissingScreenLayoutState,
) {
    MaterialTheme {
        ReportMissingScreenLayout(
            headerTitle = state.headerTitle,
            currentStep = state.currentStep,
            title = state.title,
            subtitle = state.subtitle,
            onBackClick = {},
            onContinueClick = {},
        ) {
            // Sample content
            Text(
                text = "Screen-specific content goes here",
                style = MaterialTheme.typography.bodyMedium,
                color = ReportMissingColors.SubtitleColor,
            )
        }
    }
}
