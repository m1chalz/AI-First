
package com.intive.aifirst.petspot.features.reportmissing.ui.summary

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.FlowStep
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingUiState
import com.intive.aifirst.petspot.ui.preview.PreviewScreenSizes

/**
 * Stateless content composable for Summary screen (No progress indicator).
 * Displays confirmation message and close button.
 * NOTE: This screen does NOT use StepHeader since it has no progress indicator.
 *
 * @param state Current UI state
 * @param modifier Modifier for the component
 * @param onCloseClick Callback when close button is clicked
 */
@Composable
fun SummaryContent(
    state: ReportMissingUiState,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .testTag("reportMissing.summary.content"),
    ) {
        // Content area with horizontal padding (matching other screens)
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Title
            Text(
                text = "Report created",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text =
                    "Your missing pet report has been created successfully. " +
                        "We will notify you if someone finds your pet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF545F71),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Placeholder for removal code (future implementation)
            Text(
                text = "Summary Screen",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.testTag("reportMissing.summary.placeholder"),
            )

            // Placeholder removal code display
            Text(
                text = "5216577",
                style =
                    MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        letterSpacing = 4.sp,
                    ),
                color = Color(0xFF155DFC),
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .padding(vertical = 24.dp)
                        .testTag("reportMissing.summary.removalCode"),
            )

            Text(
                text = "Save this code to remove the report later",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
            )
        }

        // Close button
        Button(
            onClick = onCloseClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .testTag("summary.submitButton"),
            shape = RoundedCornerShape(10.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF155DFC),
                ),
        ) {
            Text(
                text = "Close",
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Preview(name = "Summary Content", showBackground = true)
@PreviewScreenSizes
@Composable
private fun SummaryContentPreview() {
    MaterialTheme {
        SummaryContent(state = ReportMissingUiState(currentStep = FlowStep.SUMMARY))
    }
}
