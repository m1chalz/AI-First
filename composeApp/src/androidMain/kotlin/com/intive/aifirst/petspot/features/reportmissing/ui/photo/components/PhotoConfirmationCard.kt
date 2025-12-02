package com.intive.aifirst.petspot.features.reportmissing.ui.photo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.intive.aifirst.petspot.R

/**
 * Confirmation card showing selected photo details.
 * Displays thumbnail, filename, file size, and remove button.
 */
@Composable
fun PhotoConfirmationCard(
    photoUri: String,
    filename: String,
    fileSize: String,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Photo thumbnail
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(photoUri)
                        .crossfade(true)
                        .build(),
                placeholder = painterResource(R.drawable.ic_pet_photo_default),
                contentDescription = "Selected photo thumbnail",
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD0FAE5))
                        .testTag("animalPhoto.thumbnail"),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Filename and size
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = filename,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("animalPhoto.filename"),
                )
                Text(
                    text = fileSize,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("animalPhoto.fileSize"),
                )
            }

            // Remove button
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.testTag("animalPhoto.removeButton"),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove photo",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoConfirmationCardPreview() {
    MaterialTheme {
        PhotoConfirmationCard(
            photoUri = "content://media/picker/0/photo/1",
            filename = "dog_photo.jpg",
            fileSize = "1.2 MB",
            onRemoveClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoConfirmationCardLongFilenamePreview() {
    MaterialTheme {
        PhotoConfirmationCard(
            photoUri = "content://media/picker/0/photo/2",
            filename = "my_missing_pet_pho...",
            fileSize = "5.7 MB",
            onRemoveClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
