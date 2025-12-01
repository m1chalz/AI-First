@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Circular progress indicator for Missing Pet Report flow.
 * Shows partial arc based on progress + step text (e.g., "1/4").
 *
 * @param currentStep Current step number (1-4)
 * @param modifier Modifier for the component
 * @param totalSteps Total number of steps (default 4)
 */
@Composable
fun StepProgressIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier,
    totalSteps: Int = 4,
) {
    val progress = currentStep.toFloat() / totalSteps.toFloat()

    Box(
        modifier =
            modifier
                .size(40.dp)
                .testTag("reportMissing.progressIndicator"),
        contentAlignment = Alignment.Center,
    ) {
        // Background circle (gray track)
        Canvas(modifier = Modifier.size(40.dp)) {
            drawArc(
                color = Color(0xFFE5E9EC),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        // Progress arc (blue)
        Canvas(modifier = Modifier.size(40.dp)) {
            drawArc(
                color = Color(0xFF155DFC),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        // Step text
        Text(
            text = "$currentStep/$totalSteps",
            style =
                TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                ),
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("reportMissing.progressIndicator.text"),
        )
    }
}

private class StepProgressPreviewProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(1, 2, 3, 4)
}

@Preview(name = "Step Progress Indicator", showBackground = true)
@Composable
private fun StepProgressIndicatorPreview(
    @PreviewParameter(StepProgressPreviewProvider::class) step: Int,
) {
    StepProgressIndicator(currentStep = step)
}
