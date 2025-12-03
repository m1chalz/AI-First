package com.intive.aifirst.petspot.features.reportmissing.ui.chipnumber

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiState
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ContinueButton
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ReportMissingColors
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ScreenTitleSection
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StepHeader
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StyledOutlinedTextField
import com.intive.aifirst.petspot.features.reportmissing.util.MicrochipVisualTransformation
import com.intive.aifirst.petspot.ui.preview.PreviewScreenSizes

/**
 * Stateless content composable for Chip Number screen (Step 1/4).
 * Displays header with progress indicator, microchip input field, and continue button.
 *
 * @param state Current UI state with chip number
 * @param modifier Modifier for the component
 * @param onChipNumberChange Callback when chip number input changes
 * @param onBackClick Callback when back button is clicked
 * @param onContinueClick Callback when continue button is clicked
 */
@Composable
fun ChipNumberContent(
    state: ChipNumberUiState,
    modifier: Modifier = Modifier,
    onChipNumberChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .testTag("reportMissing.chipNumber.content"),
    ) {
        // Header with back button, title, and progress indicator
        StepHeader(
            title = "Microchip number",
            currentStep = 1,
            onBackClick = onBackClick,
        )

        // Main content
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ScreenTitleSection(
                title = "Identification by Microchip",
                subtitle =
                    "Microchip identification is the most efficient way to reunite with your pet. " +
                        "If your pet has been microchipped and you know the microchip number, please enter it here.",
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Label above field (matching Figma - static, not floating)
            Text(
                text = "Microchip number (optional)",
                style = MaterialTheme.typography.bodyMedium,
                color = ReportMissingColors.LabelColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Microchip number input field (FR-006 to FR-011)
            StyledOutlinedTextField(
                value = state.chipNumber,
                onValueChange = onChipNumberChange,
                placeholder = "00000-00000-00000",
                keyboardType = KeyboardType.Number,
                visualTransformation = MicrochipVisualTransformation(),
                modifier = Modifier.testTag("missingPet.microchip.input"),
            )
        }

        // Continue button (FR-012 to FR-015)
        ContinueButton(
            onClick = onContinueClick,
            modifier = Modifier.testTag("missingPet.microchip.continueButton"),
        )
    }
}

/**
 * Preview parameter provider for ChipNumberContent.
 * Provides sample states for empty, partial, and complete chip number entry.
 */
class ChipNumberUiStateProvider : PreviewParameterProvider<ChipNumberUiState> {
    override val values =
        sequenceOf(
            // Empty state
            ChipNumberUiState.Initial,
            // Partial entry (5 digits)
            ChipNumberUiState(chipNumber = "12345"),
            // Partial entry (10 digits)
            ChipNumberUiState(chipNumber = "1234567890"),
            // Complete entry (15 digits)
            ChipNumberUiState(chipNumber = "123456789012345"),
        )
}

@Preview(name = "Chip Number Content", showBackground = true)
@PreviewScreenSizes
@Composable
private fun ChipNumberContentPreview(
    @PreviewParameter(ChipNumberUiStateProvider::class) state: ChipNumberUiState,
) {
    MaterialTheme {
        ChipNumberContent(state = state)
    }
}
