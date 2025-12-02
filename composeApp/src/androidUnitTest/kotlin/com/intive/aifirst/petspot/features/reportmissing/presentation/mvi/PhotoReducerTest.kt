package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for photo-specific reducers in ReportMissingReducer.
 * Follows Given-When-Then structure per project constitution.
 */
class PhotoReducerTest {
    @Test
    fun `reduce PhotoSelected should set status to LOADING`() {
        // Given
        val initialState =
            ReportMissingUiState(
                photoAttachment = PhotoAttachmentState.Empty,
            )
        val intent = ReportMissingIntent.PhotoSelected("content://photo/1")

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals("content://photo/1", newState.photoAttachment.uri)
        assertEquals(PhotoStatus.LOADING, newState.photoAttachment.status)
    }

    @Test
    fun `reduce PhotoMetadataLoaded should set status to CONFIRMED with metadata`() {
        // Given
        val initialState =
            ReportMissingUiState(
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://photo/1",
                        status = PhotoStatus.LOADING,
                    ),
            )
        val intent =
            ReportMissingIntent.PhotoMetadataLoaded(
                uri = "content://photo/1",
                filename = "dog_photo.jpg",
                sizeBytes = 1_234_567,
            )

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals("content://photo/1", newState.photoAttachment.uri)
        assertEquals("dog_photo.jpg", newState.photoAttachment.filename)
        assertEquals(1_234_567, newState.photoAttachment.sizeBytes)
        assertEquals(PhotoStatus.CONFIRMED, newState.photoAttachment.status)
    }

    @Test
    fun `photoAttachment should survive navigation to DESCRIPTION and back to PHOTO`() {
        // Given
        val photoState =
            PhotoAttachmentState(
                uri = "content://photo/1",
                filename = "dog.jpg",
                sizeBytes = 1024,
                status = PhotoStatus.CONFIRMED,
            )
        val stateOnPhotoStep =
            ReportMissingUiState(
                currentStep = FlowStep.PHOTO,
                photoAttachment = photoState,
            )

        // When - navigate forward to Description
        val stateOnDescriptionStep = ReportMissingReducer.updateStep(stateOnPhotoStep, FlowStep.DESCRIPTION)

        // Then - photo data preserved
        assertEquals(photoState, stateOnDescriptionStep.photoAttachment)

        // When - navigate back to Photo
        val stateBackToPhoto = ReportMissingReducer.updateStep(stateOnDescriptionStep, FlowStep.PHOTO)

        // Then - photo data still preserved
        assertEquals(photoState, stateBackToPhoto.photoAttachment)
    }

    @Test
    fun `reduce PhotoLoadFailed should reset to EMPTY status`() {
        // Given
        val initialState =
            ReportMissingUiState(
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://photo/1",
                        status = PhotoStatus.LOADING,
                    ),
            )
        val intent = ReportMissingIntent.PhotoLoadFailed

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals(PhotoAttachmentState.Empty, newState.photoAttachment)
    }

    @Test
    fun `reduce RemovePhoto should reset to EMPTY status`() {
        // Given
        val initialState =
            ReportMissingUiState(
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://photo/1",
                        filename = "dog.jpg",
                        sizeBytes = 1024,
                        status = PhotoStatus.CONFIRMED,
                    ),
            )
        val intent = ReportMissingIntent.RemovePhoto

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals(PhotoAttachmentState.Empty, newState.photoAttachment)
    }

    @Test
    fun `reduce PhotoPickerCancelled should keep EMPTY status and preserve other state`() {
        // Given
        val initialState =
            ReportMissingUiState(
                chipNumber = "123456789012345",
                photoAttachment = PhotoAttachmentState.Empty,
            )
        val intent = ReportMissingIntent.PhotoPickerCancelled

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals(initialState, newState)
        assertEquals("123456789012345", newState.chipNumber)
        assertEquals(PhotoStatus.EMPTY, newState.photoAttachment.status)
    }
}
