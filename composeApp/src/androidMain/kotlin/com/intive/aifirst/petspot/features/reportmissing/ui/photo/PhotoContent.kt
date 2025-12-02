
package com.intive.aifirst.petspot.features.reportmissing.ui.photo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.FlowStep
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingUiState
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StepHeader
import com.intive.aifirst.petspot.ui.preview.PreviewScreenSizes

/**
 * Stateless content composable for Photo screen (Step 2/4).
 * Displays header with progress indicator, placeholder content, and continue button.
 *
 * @param state Current UI state
 * @param modifier Modifier for the component
 * @param onBackClick Callback when back button is clicked
 * @param onContinueClick Callback when continue button is clicked
 */
@Composable
fun PhotoContent(
    state: ReportMissingUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .testTag("reportMissing.photo.content"),
    ) {
        // Header with back button, title, and progress indicator
        StepHeader(
            title = "Animal photo",
            currentStep = state.progressStepNumber,
            onBackClick = onBackClick,
        )

        // Placeholder content
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your pet's photo",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2D2D2D),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please upload a photo of the missing animal.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF545F71),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder for photo upload (future implementation)
            Text(
                text = "Photo upload area placeholder",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.testTag("animalPhoto.browse"),
            )
        }

        // Continue button
        Button(
            onClick = onContinueClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .testTag("animalPhoto.continue"),
            shape = RoundedCornerShape(10.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF155DFC),
                ),
        ) {
            Text(
                text = "Continue",
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Preview(name = "Photo Content", showBackground = true)
@PreviewScreenSizes
@Composable
private fun PhotoContentPreview() {
    MaterialTheme {
        PhotoContent(state = ReportMissingUiState(currentStep = FlowStep.PHOTO))
    }
}
