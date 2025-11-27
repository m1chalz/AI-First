package com.intive.aifirst.petspot.features.petdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus

/**
 * Status badge per Figma design: pill-shaped badge with status text.
 */
@Composable
fun StatusBadge(
    status: AnimalStatus,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = Color(android.graphics.Color.parseColor(status.badgeColor))

    Text(
        text = status.displayName,
        modifier =
            modifier
                .background(
                    color = backgroundColor,
                    // Pill shape per Figma
                    shape = RoundedCornerShape(50),
                )
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .testTag("petDetails.statusBadge"),
        color = Color.White,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    )
}

private class StatusPreviewProvider : PreviewParameterProvider<AnimalStatus> {
    override val values = sequenceOf(AnimalStatus.MISSING, AnimalStatus.FOUND)
}

@Preview(name = "Status Badge")
@Composable
private fun StatusBadgePreview(
    @PreviewParameter(StatusPreviewProvider::class) status: AnimalStatus,
) {
    StatusBadge(status = status)
}
