package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for ReportMissingUiState computed properties.
 * Follows Given-When-Then structure per project constitution.
 */
class ReportMissingUiStateTest {
    // ========================================
    // showProgressIndicator Tests
    // ========================================

    @Test
    fun `showProgressIndicator should return true for CHIP_NUMBER step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.CHIP_NUMBER)

        // When
        val result = state.showProgressIndicator

        // Then
        assertTrue(result, "Progress indicator should be visible on chip number screen")
    }

    @Test
    fun `showProgressIndicator should return true for PHOTO step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.PHOTO)

        // When
        val result = state.showProgressIndicator

        // Then
        assertTrue(result, "Progress indicator should be visible on photo screen")
    }

    @Test
    fun `showProgressIndicator should return true for DESCRIPTION step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.DESCRIPTION)

        // When
        val result = state.showProgressIndicator

        // Then
        assertTrue(result, "Progress indicator should be visible on description screen")
    }

    @Test
    fun `showProgressIndicator should return true for CONTACT_DETAILS step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.CONTACT_DETAILS)

        // When
        val result = state.showProgressIndicator

        // Then
        assertTrue(result, "Progress indicator should be visible on contact details screen")
    }

    @Test
    fun `showProgressIndicator should return false for SUMMARY step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.SUMMARY)

        // When
        val result = state.showProgressIndicator

        // Then
        assertFalse(result, "Progress indicator should be hidden on summary screen")
    }

    // ========================================
    // progressStepNumber Tests
    // ========================================

    @Test
    fun `progressStepNumber should return 1 for CHIP_NUMBER step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.CHIP_NUMBER)

        // When
        val result = state.progressStepNumber

        // Then
        assertEquals(1, result, "Step number should be 1 for chip number screen")
    }

    @Test
    fun `progressStepNumber should return 2 for PHOTO step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.PHOTO)

        // When
        val result = state.progressStepNumber

        // Then
        assertEquals(2, result, "Step number should be 2 for photo screen")
    }

    @Test
    fun `progressStepNumber should return 3 for DESCRIPTION step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.DESCRIPTION)

        // When
        val result = state.progressStepNumber

        // Then
        assertEquals(3, result, "Step number should be 3 for description screen")
    }

    @Test
    fun `progressStepNumber should return 4 for CONTACT_DETAILS step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.CONTACT_DETAILS)

        // When
        val result = state.progressStepNumber

        // Then
        assertEquals(4, result, "Step number should be 4 for contact details screen")
    }

    @Test
    fun `progressStepNumber should return 0 for SUMMARY step`() {
        // Given
        val state = ReportMissingUiState(currentStep = FlowStep.SUMMARY)

        // When
        val result = state.progressStepNumber

        // Then
        assertEquals(0, result, "Step number should be 0 for summary screen (not displayed)")
    }

    // ========================================
    // Initial State Tests
    // ========================================

    @Test
    fun `Initial state should have CHIP_NUMBER as current step`() {
        // Given / When
        val state = ReportMissingUiState.Initial

        // Then
        assertEquals(FlowStep.CHIP_NUMBER, state.currentStep, "Initial step should be CHIP_NUMBER")
    }

    @Test
    fun `Initial state should have empty form fields`() {
        // Given / When
        val state = ReportMissingUiState.Initial

        // Then
        assertEquals("", state.chipNumber, "Chip number should be empty initially")
        assertEquals(null, state.photoAttachment.uri, "Photo URI should be null initially")
        assertEquals("", state.description, "Description should be empty initially")
        assertEquals("", state.contactEmail, "Contact email should be empty initially")
        assertEquals("", state.contactPhone, "Contact phone should be empty initially")
    }

    @Test
    fun `Initial state should not be loading`() {
        // Given / When
        val state = ReportMissingUiState.Initial

        // Then
        assertFalse(state.isLoading, "Initial state should not be loading")
    }

    @Test
    fun `progressTotalSteps should always be 4`() {
        // Given
        val state = ReportMissingUiState.Initial

        // When
        val result = state.progressTotalSteps

        // Then
        assertEquals(4, result, "Total steps should always be 4")
    }
}
