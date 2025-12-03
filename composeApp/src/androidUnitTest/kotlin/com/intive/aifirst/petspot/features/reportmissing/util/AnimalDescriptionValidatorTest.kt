package com.intive.aifirst.petspot.features.reportmissing.util

import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUiState
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for AnimalDescriptionValidator.
 * Tests form validation logic for the Animal Description screen.
 * Follows Given-When-Then structure per project constitution.
 */
class AnimalDescriptionValidatorTest {
    // ========================================
    // Species Validation Tests
    // ========================================

    @Test
    fun `validate should return species error when species is empty`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid, "Should be invalid when species is empty")
        assertNotNull(result.speciesError, "Should have species error")
        assertEquals("This field cannot be empty", result.speciesError)
    }

    @Test
    fun `validate should return species error when species is blank`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "   ",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid, "Should be invalid when species is blank")
        assertNotNull(result.speciesError, "Should have species error")
    }

    @Test
    fun `validate should pass when species is selected`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.speciesError, "Should not have species error when species selected")
    }

    // ========================================
    // Race Validation Tests
    // ========================================

    @Test
    fun `validate should return race error when species selected but race is empty`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid, "Should be invalid when race is empty")
        assertNotNull(result.raceError, "Should have race error")
        assertEquals("This field cannot be empty", result.raceError)
    }

    @Test
    fun `validate should NOT return race error when species is empty`() {
        // Given - species not selected yet, race empty is OK
        val state = AnimalDescriptionUiState(
            animalSpecies = "",
            animalRace = "",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.raceError, "Should not have race error when species not selected")
    }

    @Test
    fun `validate should pass when race is provided`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Golden Retriever",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.raceError, "Should not have race error when race provided")
    }

    // ========================================
    // Gender Validation Tests
    // ========================================

    @Test
    fun `validate should return gender error when gender is null`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = null,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid, "Should be invalid when gender is null")
        assertNotNull(result.genderError, "Should have gender error")
        assertEquals("This field cannot be empty", result.genderError)
    }

    @Test
    fun `validate should pass when gender is FEMALE`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.FEMALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.genderError, "Should not have gender error when FEMALE selected")
    }

    @Test
    fun `validate should pass when gender is MALE`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.genderError, "Should not have gender error when MALE selected")
    }

    // ========================================
    // Complete Form Validation Tests
    // ========================================

    @Test
    fun `validate should return isValid true when all required fields are valid`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertTrue(result.isValid, "Should be valid when all required fields are filled")
        assertNull(result.speciesError)
        assertNull(result.raceError)
        assertNull(result.genderError)
    }

    @Test
    fun `validate should return multiple errors when multiple fields are invalid`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "",
            animalRace = "",
            animalGender = null,
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid, "Should be invalid with multiple errors")
        assertNotNull(result.speciesError, "Should have species error")
        assertNotNull(result.genderError, "Should have gender error")
        // Note: race error is null because species is not selected
        assertNull(result.raceError, "Race error should be null when species not selected")
    }

    // ========================================
    // Age Validation Tests (Optional Field)
    // ========================================

    @Test
    fun `validate should pass when age is empty`() {
        // Given - age is optional
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
            animalAge = "",
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.ageError, "Should not have age error when empty")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate should pass when age is valid number`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
            animalAge = "5",
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.ageError, "Should not have age error for valid age")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate should return age error when age is not a number`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
            animalAge = "abc",
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid)
        assertNotNull(result.ageError)
        assertEquals("Age must be a number", result.ageError)
    }

    @Test
    fun `validate should return age error when age is negative`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
            animalAge = "-1",
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid)
        assertNotNull(result.ageError)
        assertEquals("Age cannot be negative", result.ageError)
    }

    @Test
    fun `validate should return age error when age exceeds maximum`() {
        // Given
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
            animalAge = "41",
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertFalse(result.isValid)
        assertNotNull(result.ageError)
        assertEquals("Age must be 40 or less", result.ageError)
    }

    @Test
    fun `validate should pass when age is exactly 40`() {
        // Given - boundary test
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
            animalAge = "40",
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.ageError, "Should accept age of 40")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate should pass when age is 0`() {
        // Given - boundary test for newborn
        val state = AnimalDescriptionUiState(
            animalSpecies = "Dog",
            animalRace = "Labrador",
            animalGender = AnimalGender.MALE,
            animalAge = "0",
        )

        // When
        val result = AnimalDescriptionValidator.validate(state)

        // Then
        assertNull(result.ageError, "Should accept age of 0")
        assertTrue(result.isValid)
    }
}

