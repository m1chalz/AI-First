package com.intive.aifirst.petspot.features.reportmissing.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

/**
 * Reusable title + subtitle section for Report Missing Pet screens.
 * Styled to match Figma design with consistent typography and colors.
 *
 * @param title Main title text (displayed as headlineSmall)
 * @param subtitle Secondary description text (displayed as bodyMedium)
 * @param modifier Modifier for the section
 */
@Composable
fun ScreenTitleSection(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = ReportMissingColors.TitleColor,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = ReportMissingColors.SubtitleColor,
        )
    }
}

// ========================================
// Preview
// ========================================

private data class ScreenTitleSectionState(
    val title: String,
    val subtitle: String,
)

private class ScreenTitleSectionProvider : PreviewParameterProvider<ScreenTitleSectionState> {
    override val values =
        sequenceOf(
            ScreenTitleSectionState(
                title = "Identification by Microchip",
                subtitle = "Microchip identification is the most efficient way to reunite with your pet.",
            ),
            ScreenTitleSectionState(
                title = "Your pet's photo",
                subtitle = "Please upload a photo of the missing animal.",
            ),
            ScreenTitleSectionState(
                title = "Your pet's details",
                subtitle = "Fill out the details about the missing animal.",
            ),
        )
}

@Preview(name = "Screen Title Section", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ScreenTitleSectionPreview(
    @PreviewParameter(ScreenTitleSectionProvider::class) state: ScreenTitleSectionState,
) {
    MaterialTheme {
        ScreenTitleSection(
            title = state.title,
            subtitle = state.subtitle,
            modifier = Modifier.padding(16.dp),
        )
    }
}
