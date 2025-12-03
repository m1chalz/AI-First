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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.intive.aifirst.petspot.R
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoAttachmentState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoStatus

/**
 * Confirmation card showing selected photo details.
 * Displays thumbnail (or loader), filename, file size, and remove button.
 * Handles both LOADING and CONFIRMED states.
 * Matches Figma design: "Uploaded img" (node 297:8090)
 */
@Composable
fun PhotoConfirmationCard(
    photoAttachment: PhotoAttachmentState,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Design tokens from Figma
    val cardBackground = Color.White
    val borderColor = Color(0x1A000000) // rgba(0,0,0,0.1)
    val iconBackground = Color(0xFFD0FAE5) // mint green
    val loaderColor = Color(0xFF10B981) // green
    val filenameColor = Color(0xFF0A0A0A) // neutral-950
    val fileSizeColor = Color(0xFF717182)

    val isLoading = photoAttachment.status == PhotoStatus.LOADING

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
                .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Left side: Thumbnail/Loader + text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Thumbnail area - shows loader when loading, photo when confirmed
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(iconBackground)
                            .testTag("animalPhoto.thumbnail"),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = loaderColor,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        AsyncImage(
                            model =
                                ImageRequest.Builder(LocalContext.current)
                                    .data(photoAttachment.uri)
                                    .crossfade(true)
                                    .build(),
                            contentDescription = "Selected photo thumbnail",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ic_image),
                            error = painterResource(R.drawable.ic_image),
                        )
                    }
                }

                // Filename and size
                Column {
                    Text(
                        text = if (isLoading) "Loading..." else photoAttachment.displayFilename,
                        fontSize = 14.sp,
                        color = filenameColor,
                        modifier = Modifier.testTag("animalPhoto.filename"),
                    )
                    Text(
                        text = if (isLoading) "Processing photo" else photoAttachment.formattedSize,
                        fontSize = 12.sp,
                        color = fileSizeColor,
                        modifier = Modifier.testTag("animalPhoto.fileSize"),
                    )
                }
            }

            // Remove button (disabled during loading)
            IconButton(
                onClick = onRemoveClick,
                enabled = !isLoading,
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("animalPhoto.removeButton"),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove photo",
                    modifier = Modifier.size(24.dp),
                    tint = if (isLoading) Color(0xFFD1D5DB) else Color(0xFF6B7280),
                )
            }
        }
    }
}

/**
 * Preview parameter provider for PhotoConfirmationCard.
 * Provides sample states for loading, confirmed, and various filename scenarios.
 */
private class PhotoConfirmationCardStateProvider : PreviewParameterProvider<PhotoAttachmentState> {
    override val values =
        sequenceOf(
            // Loading state
            PhotoAttachmentState(
                uri = "content://media/picker/0/photo/1",
                status = PhotoStatus.LOADING,
            ),
            // Normal filename - confirmed
            PhotoAttachmentState(
                uri = "content://media/picker/0/photo/1",
                filename = "Max.img",
                sizeBytes = 1_258_291,
                status = PhotoStatus.CONFIRMED,
            ),
            // Long filename (will be truncated)
            PhotoAttachmentState(
                uri = "content://media/picker/0/photo/2",
                filename = "my_missing_pet_photo_with_very_long_filename.jpg",
                sizeBytes = 5_976_883,
                status = PhotoStatus.CONFIRMED,
            ),
            // Small file
            PhotoAttachmentState(
                uri = "content://media/picker/0/photo/3",
                filename = "cat.png",
                sizeBytes = 256_000,
                status = PhotoStatus.CONFIRMED,
            ),
        )
}

@Preview(showBackground = true)
@Composable
private fun PhotoConfirmationCardPreview(
    @PreviewParameter(PhotoConfirmationCardStateProvider::class) photoAttachment: PhotoAttachmentState,
) {
    MaterialTheme {
        PhotoConfirmationCard(
            photoAttachment = photoAttachment,
            onRemoveClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
