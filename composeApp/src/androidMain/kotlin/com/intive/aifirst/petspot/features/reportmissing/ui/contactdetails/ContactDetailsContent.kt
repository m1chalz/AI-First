@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.features.reportmissing.ui.contactdetails

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingUiState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingUiStatePreviewProvider
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StepHeader

/**
 * Stateless content composable for Contact Details screen (Step 4/4).
 * Displays header with progress indicator, placeholder content, and continue button.
 *
 * @param state Current UI state
 * @param modifier Modifier for the component
 * @param onBackClick Callback when back button is clicked
 * @param onContinueClick Callback when continue button is clicked
 */
@Composable
fun ContactDetailsContent(
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
                .testTag("reportMissing.contactDetails.content"),
    ) {
        // Header with back button, title, and progress indicator
        StepHeader(
            title = "Owner's details",
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
                text = "Your contact info",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2D2D2D),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add your contact information's and potential reward.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF545F71),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder for contact details form (future implementation)
            Text(
                text = "Contact Details Screen",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.testTag("reportMissing.contactDetails.placeholder"),
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
                    .testTag("contactDetails.continueButton"),
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

@Preview(name = "Contact Details Content", showBackground = true)
@Composable
private fun ContactDetailsContentPreview(
    @PreviewParameter(ReportMissingUiStatePreviewProvider::class) state: ReportMissingUiState,
) {
    MaterialTheme {
        ContactDetailsContent(state = state)
    }
}
