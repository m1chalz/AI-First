package com.intive.aifirst.petspot.features.reportmissing.ui.photo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.R

/**
 * Empty state for photo selection - displays before user selects a photo.
 * Shows placeholder icon, title, helper text, and Browse button.
 */
@Composable
fun PhotoEmptyState(
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Placeholder icon
        Icon(
            painter = painterResource(R.drawable.ic_report_missing_animal),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        )

        // Title
        Text(
            text = stringResource(R.string.photo_screen_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        // Helper text
        Text(
            text = stringResource(R.string.photo_helper_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Browse button
        Button(
            onClick = onBrowseClick,
            modifier = Modifier.testTag("animalPhoto.browseButton"),
        ) {
            Text(text = stringResource(R.string.photo_browse_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoEmptyStatePreview() {
    MaterialTheme {
        PhotoEmptyState(onBrowseClick = {})
    }
}
