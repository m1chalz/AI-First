package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Enum representing the 5 screens in the Missing Pet Report flow.
 * Determines which screen content to display and progress indicator state.
 */
enum class FlowStep {
    CHIP_NUMBER, // Step 1/4
    PHOTO, // Step 2/4
    DESCRIPTION, // Step 3/4
    CONTACT_DETAILS, // Step 4/4
    SUMMARY, // No progress indicator
}

/**
 * Immutable UI state for Missing Pet Report flow.
 * Single source of truth for all 5 screens in the wizard.
 * Managed by ReportMissingViewModel, shared across nav graph.
 */
data class ReportMissingUiState(
    // Current step tracking
    val currentStep: FlowStep = FlowStep.CHIP_NUMBER,
    // Step 1: Chip Number
    val chipNumber: String = "",
    // Step 2: Photo
    val photoAttachment: PhotoAttachmentState = PhotoAttachmentState.Empty,
    // Step 3: Description
    val description: String = "",
    // Step 4: Contact Details
    val contactEmail: String = "",
    val contactPhone: String = "",
    // UI state flags
    val isLoading: Boolean = false,
) {
    companion object {
        val Initial = ReportMissingUiState()
    }

    /**
     * Progress indicator visibility logic.
     * Shows progress on data collection screens (1-4), hidden on summary.
     */
    val showProgressIndicator: Boolean
        get() = currentStep != FlowStep.SUMMARY

    /**
     * Current step number for progress indicator (1-4).
     * Returns 0 for summary screen (not displayed).
     */
    val progressStepNumber: Int
        get() =
            when (currentStep) {
                FlowStep.CHIP_NUMBER -> 1
                FlowStep.PHOTO -> 2
                FlowStep.DESCRIPTION -> 3
                FlowStep.CONTACT_DETAILS -> 4
                FlowStep.SUMMARY -> 0
            }

    /**
     * Total steps for progress indicator (always 4 data collection steps).
     */
    val progressTotalSteps: Int = 4
}

/**
 * Preview parameter provider for Compose previews.
 * Provides sample states for each step in the flow.
 */
class ReportMissingUiStatePreviewProvider : PreviewParameterProvider<ReportMissingUiState> {
    override val values =
        sequenceOf(
            // Step 1: Empty chip number
            ReportMissingUiState.Initial,
            // Step 1: With chip number entered
            ReportMissingUiState(
                currentStep = FlowStep.CHIP_NUMBER,
                chipNumber = "123456789012345",
            ),
            // Step 2: Photo screen empty
            ReportMissingUiState(
                currentStep = FlowStep.PHOTO,
                chipNumber = "123456789012345",
            ),
            // Step 2: Photo screen loading
            ReportMissingUiState(
                currentStep = FlowStep.PHOTO,
                chipNumber = "123456789012345",
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://media/picker/0/photo/1",
                        status = PhotoStatus.LOADING,
                    ),
            ),
            // Step 2: Photo screen confirmed
            ReportMissingUiState(
                currentStep = FlowStep.PHOTO,
                chipNumber = "123456789012345",
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://media/picker/0/photo/1",
                        filename = "dog_photo.jpg",
                        sizeBytes = 1_234_567,
                        status = PhotoStatus.CONFIRMED,
                    ),
            ),
            // Step 2: Photo screen with long filename
            ReportMissingUiState(
                currentStep = FlowStep.PHOTO,
                chipNumber = "123456789012345",
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://media/picker/0/photo/2",
                        filename = "my_missing_pet_photo_2024.jpg",
                        sizeBytes = 5_678_901,
                        status = PhotoStatus.CONFIRMED,
                    ),
            ),
            // Step 3: Description with photo
            ReportMissingUiState(
                currentStep = FlowStep.DESCRIPTION,
                chipNumber = "123456789012345",
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://media/picker/0/photo/1",
                        filename = "dog_photo.jpg",
                        sizeBytes = 1_234_567,
                        status = PhotoStatus.CONFIRMED,
                    ),
            ),
            // Step 4: Contact details
            ReportMissingUiState(
                currentStep = FlowStep.CONTACT_DETAILS,
                chipNumber = "123456789012345",
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://media/picker/0/photo/1",
                        filename = "dog_photo.jpg",
                        sizeBytes = 1_234_567,
                        status = PhotoStatus.CONFIRMED,
                    ),
                description = "Small brown dog, friendly",
            ),
            // Step 5: Summary with all data
            ReportMissingUiState(
                currentStep = FlowStep.SUMMARY,
                chipNumber = "123456789012345",
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://media/picker/0/photo/1",
                        filename = "dog_photo.jpg",
                        sizeBytes = 1_234_567,
                        status = PhotoStatus.CONFIRMED,
                    ),
                description = "Small brown dog, friendly",
                contactEmail = "owner@example.com",
                contactPhone = "+1234567890",
            ),
        )
}
