package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

/**
 * Standardized Continue button for the Report Missing Pet flow.
 * Styled to match Figma design with primary blue background and rounded corners.
 *
 * Includes navigation bar padding for edge-to-edge support.
 *
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 * @param text Button text (defaults to "Continue")
 * @param enabled Whether the button is enabled (defaults to true)
 * @param isLoading Whether to show loading indicator instead of text (defaults to false)
 * @param testTag Optional test tag for the button (for automation)
 */
@Composable
fun ContinueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Continue",
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ReportMissingColors.PrimaryBlue,
            disabledContainerColor = ReportMissingColors.PrimaryBlue.copy(alpha = 0.5f),
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text(text = text)
            }
        }
    }
}

// ========================================
// Preview
// ========================================

private data class ContinueButtonState(
    val text: String,
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
)

private class ContinueButtonStateProvider : PreviewParameterProvider<ContinueButtonState> {
    override val values = sequenceOf(
        ContinueButtonState(text = "Continue"),
        ContinueButtonState(text = "Submit"),
        ContinueButtonState(text = "Continue", enabled = false),
        ContinueButtonState(text = "Continue", isLoading = true),
    )
}

@Preview(name = "Continue Button", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ContinueButtonPreview(
    @PreviewParameter(ContinueButtonStateProvider::class) state: ContinueButtonState,
) {
    MaterialTheme {
        ContinueButton(
            onClick = {},
            text = state.text,
            enabled = state.enabled,
            isLoading = state.isLoading,
        )
    }
}
