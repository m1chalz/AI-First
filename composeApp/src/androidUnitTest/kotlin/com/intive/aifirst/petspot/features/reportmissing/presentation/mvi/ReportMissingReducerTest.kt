package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for ReportMissingReducer state transitions.
 * Follows Given-When-Then structure per project constitution.
 */
class ReportMissingReducerTest {
    // ========================================
    // reduce() Tests - State Modifying Intents
    // ========================================

    @Test
    fun `reduce UpdateChipNumber should update chip number in state`() {
        // Given
        val initialState = ReportMissingUiState.Initial
        val intent = ReportMissingIntent.UpdateChipNumber("123456789")

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals("123456789", newState.chipNumber, "Chip number should be updated")
    }

    @Test
    fun `reduce UpdatePhotoUri should update photo URI in state`() {
        // Given
        val initialState = ReportMissingUiState.Initial
        val intent = ReportMissingIntent.UpdatePhotoUri("content://photo/1")

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals("content://photo/1", newState.photoAttachment.uri, "Photo URI should be updated")
        assertEquals(PhotoStatus.CONFIRMED, newState.photoAttachment.status, "Status should be CONFIRMED")
    }

    @Test
    fun `reduce UpdatePhotoUri with null should clear photo URI`() {
        // Given
        val initialState =
            ReportMissingUiState(
                photoAttachment =
                    PhotoAttachmentState(
                        uri = "content://photo/1",
                        status = PhotoStatus.CONFIRMED,
                    ),
            )
        val intent = ReportMissingIntent.UpdatePhotoUri(null)

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertNull(newState.photoAttachment.uri, "Photo URI should be cleared")
        assertEquals(PhotoStatus.EMPTY, newState.photoAttachment.status, "Status should be EMPTY")
    }

    @Test
    fun `reduce UpdateDescription should update description in state`() {
        // Given
        val initialState = ReportMissingUiState.Initial
        val intent = ReportMissingIntent.UpdateDescription("Small brown dog")

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals("Small brown dog", newState.description, "Description should be updated")
    }

    @Test
    fun `reduce UpdateContactEmail should update contact email in state`() {
        // Given
        val initialState = ReportMissingUiState.Initial
        val intent = ReportMissingIntent.UpdateContactEmail("owner@example.com")

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals("owner@example.com", newState.contactEmail, "Contact email should be updated")
    }

    @Test
    fun `reduce UpdateContactPhone should update contact phone in state`() {
        // Given
        val initialState = ReportMissingUiState.Initial
        val intent = ReportMissingIntent.UpdateContactPhone("+1234567890")

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals("+1234567890", newState.contactPhone, "Contact phone should be updated")
    }

    // ========================================
    // reduce() Tests - Navigation Intents (No State Change)
    // ========================================

    @Test
    fun `reduce NavigateNext should not modify state`() {
        // Given
        val initialState =
            ReportMissingUiState(
                chipNumber = "123456",
                currentStep = FlowStep.CHIP_NUMBER,
            )
        val intent = ReportMissingIntent.NavigateNext

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals(initialState, newState, "State should not change for navigation intents")
    }

    @Test
    fun `reduce NavigateBack should not modify state`() {
        // Given
        val initialState =
            ReportMissingUiState(
                currentStep = FlowStep.PHOTO,
            )
        val intent = ReportMissingIntent.NavigateBack

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals(initialState, newState, "State should not change for navigation intents")
    }

    @Test
    fun `reduce Submit should not modify state`() {
        // Given
        val initialState = ReportMissingUiState(currentStep = FlowStep.SUMMARY)
        val intent = ReportMissingIntent.Submit

        // When
        val newState = ReportMissingReducer.reduce(initialState, intent)

        // Then
        assertEquals(initialState, newState, "State should not change for submit intent")
    }

    // ========================================
    // updateStep() Tests
    // ========================================

    @Test
    fun `updateStep should change current step`() {
        // Given
        val initialState = ReportMissingUiState(currentStep = FlowStep.CHIP_NUMBER)

        // When
        val newState = ReportMissingReducer.updateStep(initialState, FlowStep.PHOTO)

        // Then
        assertEquals(FlowStep.PHOTO, newState.currentStep, "Current step should be updated")
    }

    @Test
    fun `updateStep should preserve other state fields`() {
        // Given
        val initialState =
            ReportMissingUiState(
                currentStep = FlowStep.CHIP_NUMBER,
                chipNumber = "123456",
                description = "Test",
            )

        // When
        val newState = ReportMissingReducer.updateStep(initialState, FlowStep.DESCRIPTION)

        // Then
        assertEquals("123456", newState.chipNumber, "Chip number should be preserved")
        assertEquals("Test", newState.description, "Description should be preserved")
    }

    // ========================================
    // loading() and idle() Tests
    // ========================================

    @Test
    fun `loading should set isLoading to true`() {
        // Given
        val initialState = ReportMissingUiState(isLoading = false)

        // When
        val newState = ReportMissingReducer.loading(initialState)

        // Then
        assertTrue(newState.isLoading, "isLoading should be true")
    }

    @Test
    fun `idle should set isLoading to false`() {
        // Given
        val initialState = ReportMissingUiState(isLoading = true)

        // When
        val newState = ReportMissingReducer.idle(initialState)

        // Then
        assertFalse(newState.isLoading, "isLoading should be false")
    }

    // ========================================
    // nextStep() Tests
    // ========================================

    @Test
    fun `nextStep from CHIP_NUMBER should return PHOTO`() {
        // Given / When
        val result = ReportMissingReducer.nextStep(FlowStep.CHIP_NUMBER)

        // Then
        assertEquals(FlowStep.PHOTO, result, "Next step from CHIP_NUMBER should be PHOTO")
    }

    @Test
    fun `nextStep from PHOTO should return DESCRIPTION`() {
        // Given / When
        val result = ReportMissingReducer.nextStep(FlowStep.PHOTO)

        // Then
        assertEquals(FlowStep.DESCRIPTION, result, "Next step from PHOTO should be DESCRIPTION")
    }

    @Test
    fun `nextStep from DESCRIPTION should return CONTACT_DETAILS`() {
        // Given / When
        val result = ReportMissingReducer.nextStep(FlowStep.DESCRIPTION)

        // Then
        assertEquals(
            FlowStep.CONTACT_DETAILS,
            result,
            "Next step from DESCRIPTION should be CONTACT_DETAILS",
        )
    }

    @Test
    fun `nextStep from CONTACT_DETAILS should return SUMMARY`() {
        // Given / When
        val result = ReportMissingReducer.nextStep(FlowStep.CONTACT_DETAILS)

        // Then
        assertEquals(FlowStep.SUMMARY, result, "Next step from CONTACT_DETAILS should be SUMMARY")
    }

    @Test
    fun `nextStep from SUMMARY should return null`() {
        // Given / When
        val result = ReportMissingReducer.nextStep(FlowStep.SUMMARY)

        // Then
        assertNull(result, "Next step from SUMMARY should be null (no next step)")
    }

    // ========================================
    // previousStep() Tests
    // ========================================

    @Test
    fun `previousStep from CHIP_NUMBER should return null`() {
        // Given / When
        val result = ReportMissingReducer.previousStep(FlowStep.CHIP_NUMBER)

        // Then
        assertNull(result, "Previous step from CHIP_NUMBER should be null (first step)")
    }

    @Test
    fun `previousStep from PHOTO should return CHIP_NUMBER`() {
        // Given / When
        val result = ReportMissingReducer.previousStep(FlowStep.PHOTO)

        // Then
        assertEquals(FlowStep.CHIP_NUMBER, result, "Previous step from PHOTO should be CHIP_NUMBER")
    }

    @Test
    fun `previousStep from DESCRIPTION should return PHOTO`() {
        // Given / When
        val result = ReportMissingReducer.previousStep(FlowStep.DESCRIPTION)

        // Then
        assertEquals(FlowStep.PHOTO, result, "Previous step from DESCRIPTION should be PHOTO")
    }

    @Test
    fun `previousStep from CONTACT_DETAILS should return DESCRIPTION`() {
        // Given / When
        val result = ReportMissingReducer.previousStep(FlowStep.CONTACT_DETAILS)

        // Then
        assertEquals(
            FlowStep.DESCRIPTION,
            result,
            "Previous step from CONTACT_DETAILS should be DESCRIPTION",
        )
    }

    @Test
    fun `previousStep from SUMMARY should return CONTACT_DETAILS`() {
        // Given / When
        val result = ReportMissingReducer.previousStep(FlowStep.SUMMARY)

        // Then
        assertEquals(
            FlowStep.CONTACT_DETAILS,
            result,
            "Previous step from SUMMARY should be CONTACT_DETAILS",
        )
    }
}
