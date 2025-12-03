package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
 */
@Composable
fun ContinueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Continue",
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ReportMissingColors.PrimaryBlue,
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

// ========================================
// Preview
// ========================================

private class ContinueButtonTextProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Continue",
        "Submit",
        "Next Step",
    )
}

@Preview(name = "Continue Button", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ContinueButtonPreview(
    @PreviewParameter(ContinueButtonTextProvider::class) text: String,
) {
    MaterialTheme {
        ContinueButton(
            onClick = {},
            text = text,
        )
    }
}
