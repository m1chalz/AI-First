package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Pure reducer for Missing Pet Report flow state transitions.
 * Takes current state and intent, produces new state.
 * No side effects - testable in isolation.
 */
object ReportMissingReducer {
    /**
     * Reduces state based on user intent.
     * Returns new state after applying the intent.
     *
     * @param currentState Current UI state
     * @param intent User intent to process
     * @return New UI state after reduction
     */
    fun reduce(
        currentState: ReportMissingUiState,
        intent: ReportMissingIntent,
    ): ReportMissingUiState {
        return when (intent) {
            is ReportMissingIntent.UpdateChipNumber ->
                currentState.copy(chipNumber = intent.value)

            is ReportMissingIntent.UpdatePhotoUri ->
                currentState.copy(
                    photoAttachment =
                        currentState.photoAttachment.copy(
                            uri = intent.uri,
                            status = if (intent.uri != null) PhotoStatus.CONFIRMED else PhotoStatus.EMPTY,
                        ),
                )

            // Photo intents
            is ReportMissingIntent.PhotoSelected ->
                currentState.copy(
                    photoAttachment =
                        currentState.photoAttachment.copy(
                            uri = intent.uri,
                            status = PhotoStatus.LOADING,
                        ),
                )

            is ReportMissingIntent.PhotoMetadataLoaded ->
                currentState.copy(
                    photoAttachment =
                        PhotoAttachmentState(
                            uri = intent.uri,
                            filename = intent.filename,
                            sizeBytes = intent.sizeBytes,
                            status = PhotoStatus.CONFIRMED,
                        ),
                )

            is ReportMissingIntent.PhotoLoadFailed ->
                currentState.copy(photoAttachment = PhotoAttachmentState.Empty)

            is ReportMissingIntent.RemovePhoto ->
                currentState.copy(photoAttachment = PhotoAttachmentState.Empty)

            is ReportMissingIntent.PhotoPickerCancelled ->
                currentState // Keep current state

            is ReportMissingIntent.UpdateDescription ->
                currentState.copy(description = intent.value)

            is ReportMissingIntent.UpdateContactEmail ->
                currentState.copy(contactEmail = intent.value)

            is ReportMissingIntent.UpdateContactPhone ->
                currentState.copy(contactPhone = intent.value)

            // Navigation and side-effect intents don't modify state
            is ReportMissingIntent.NavigateNext,
            is ReportMissingIntent.NavigateBack,
            is ReportMissingIntent.Submit,
            is ReportMissingIntent.OpenPhotoPicker,
            -> currentState
        }
    }

    /**
     * Returns state with updated current step.
     */
    fun updateStep(
        currentState: ReportMissingUiState,
        step: FlowStep,
    ): ReportMissingUiState = currentState.copy(currentStep = step)

    /**
     * Returns loading state.
     */
    fun loading(currentState: ReportMissingUiState): ReportMissingUiState = currentState.copy(isLoading = true)

    /**
     * Returns idle state (not loading).
     */
    fun idle(currentState: ReportMissingUiState): ReportMissingUiState = currentState.copy(isLoading = false)

    /**
     * Determines the next step in the flow.
     * Returns null if at the last step (Summary).
     */
    fun nextStep(currentStep: FlowStep): FlowStep? {
        return when (currentStep) {
            FlowStep.CHIP_NUMBER -> FlowStep.PHOTO
            FlowStep.PHOTO -> FlowStep.DESCRIPTION
            FlowStep.DESCRIPTION -> FlowStep.CONTACT_DETAILS
            FlowStep.CONTACT_DETAILS -> FlowStep.SUMMARY
            FlowStep.SUMMARY -> null
        }
    }

    /**
     * Determines the previous step in the flow.
     * Returns null if at the first step (ChipNumber).
     */
    fun previousStep(currentStep: FlowStep): FlowStep? {
        return when (currentStep) {
            FlowStep.CHIP_NUMBER -> null
            FlowStep.PHOTO -> FlowStep.CHIP_NUMBER
            FlowStep.DESCRIPTION -> FlowStep.PHOTO
            FlowStep.CONTACT_DETAILS -> FlowStep.DESCRIPTION
            FlowStep.SUMMARY -> FlowStep.CONTACT_DETAILS
        }
    }
}
