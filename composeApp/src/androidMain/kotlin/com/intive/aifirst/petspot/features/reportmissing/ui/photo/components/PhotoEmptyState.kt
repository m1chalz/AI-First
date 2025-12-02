package com.intive.aifirst.petspot.features.reportmissing.ui.photo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intive.aifirst.petspot.R

/**
 * Empty state for photo selection - displays before user selects a photo.
 * Compact inline card with icon, title, subtitle, and Browse button.
 * Matches Figma design: CompactInline (node 297:8076)
 */
@Composable
fun PhotoEmptyState(
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Design tokens from Figma
    val cardBackground = Color.White
    val borderColor = Color(0xFFE5E7EB) // gray-200
    val iconBackground = Color(0xFFE0E7FF) // indigo-100
    val iconTint = Color(0xFF4F39F6) // indigo/purple
    val titleColor = Color(0xFF101828)
    val subtitleColor = Color(0xFF6A7282)
    val buttonColor = Color(0xFF4F39F6)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(cardBackground)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Icon container
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_upload),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint,
                )
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(R.string.photo_empty_title),
                    fontSize = 14.sp,
                    color = titleColor,
                )
                Text(
                    text = stringResource(R.string.photo_empty_subtitle),
                    fontSize = 12.sp,
                    color = subtitleColor,
                    lineHeight = 18.sp,
                )
            }

            // Browse button
            Button(
                onClick = onBrowseClick,
                modifier = Modifier.testTag("animalPhoto.browseButton"),
                shape = RoundedCornerShape(10.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.photo_browse_button),
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoEmptyStatePreview() {
    MaterialTheme {
        PhotoEmptyState(
            onBrowseClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
