package com.intive.aifirst.petspot.features.reportmissing.ui.photo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoAttachmentState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.PhotoStatus
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ContinueButton
import com.intive.aifirst.petspot.features.reportmissing.ui.components.ScreenTitleSection
import com.intive.aifirst.petspot.features.reportmissing.ui.components.StepHeader
import com.intive.aifirst.petspot.features.reportmissing.ui.photo.components.PhotoConfirmationCard
import com.intive.aifirst.petspot.features.reportmissing.ui.photo.components.PhotoEmptyState
import com.intive.aifirst.petspot.ui.preview.PreviewScreenSizes

/**
 * Stateless content composable for Photo screen (Step 2/4).
 * Displays header with progress indicator, photo picker UI, and continue button.
 *
 * @param photoAttachment Current photo attachment state
 * @param modifier Modifier for the component
 * @param onBrowseClick Callback when browse/upload button is clicked
 * @param onRemovePhotoClick Callback when remove photo button is clicked
 * @param onBackClick Callback when back button is clicked
 * @param onContinueClick Callback when continue button is clicked
 */
@Composable
fun PhotoContent(
    photoAttachment: PhotoAttachmentState,
    modifier: Modifier = Modifier,
    onBrowseClick: () -> Unit = {},
    onRemovePhotoClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("reportMissing.photo.content"),
    ) {
        // Header with back button, title, and progress indicator
        StepHeader(
            title = "Animal photo",
            currentStep = 2,
            onBackClick = onBackClick,
        )

        // Main content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ScreenTitleSection(
                title = "Your pet's photo",
                subtitle = "Please upload a photo of the missing animal.",
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Upload card - always visible (acts as trigger to add/change photo)
            PhotoEmptyState(onBrowseClick = onBrowseClick)

            // Photo details card - shown when photo is loading or confirmed
            if (photoAttachment.status == PhotoStatus.LOADING ||
                photoAttachment.status == PhotoStatus.CONFIRMED
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                PhotoConfirmationCard(
                    photoAttachment = photoAttachment,
                    onRemoveClick = onRemovePhotoClick,
                )
            }
        }

        // Continue button
        ContinueButton(
            onClick = onContinueClick,
            modifier = Modifier.testTag("animalPhoto.continue"),
        )
    }
}

/**
 * Preview parameter provider for PhotoContent.
 * Provides sample states for empty, loading, confirmed, and error scenarios.
 */
class PhotoAttachmentStateProvider : PreviewParameterProvider<PhotoAttachmentState> {
    override val values = sequenceOf(
        // Empty state
        PhotoAttachmentState.Empty,
        // Loading state
        PhotoAttachmentState(
            uri = "content://photo/1",
            status = PhotoStatus.LOADING,
        ),
        // Confirmed state with photo
        PhotoAttachmentState(
            uri = "content://photo/1",
            filename = "missing_dog.jpg",
            sizeBytes = 1_534_000,
            status = PhotoStatus.CONFIRMED,
        ),
        // Confirmed state with long filename
        PhotoAttachmentState(
            uri = "content://photo/2",
            filename = "my_very_long_filename_photo_dog.jpg",
            sizeBytes = 512_000,
            status = PhotoStatus.CONFIRMED,
        ),
        // Error state
        PhotoAttachmentState(
            uri = "content://photo/error",
            status = PhotoStatus.ERROR,
        ),
    )
}

@Preview(name = "Photo Content", showBackground = true)
@PreviewScreenSizes
@Composable
private fun PhotoContentPreview(
    @PreviewParameter(PhotoAttachmentStateProvider::class) photoAttachment: PhotoAttachmentState,
) {
    MaterialTheme {
        PhotoContent(photoAttachment = photoAttachment)
    }
}
