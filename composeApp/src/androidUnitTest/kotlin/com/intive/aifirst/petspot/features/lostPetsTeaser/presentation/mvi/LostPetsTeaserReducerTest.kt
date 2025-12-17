package com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for LostPetsTeaserReducer.
 * Follows Given-When-Then structure per project constitution.
 */
class LostPetsTeaserReducerTest {
    @Test
    fun `loading should return loading state with no error`() {
        // Given - initial state
        val currentState = LostPetsTeaserUiState.Initial

        // When - loading state requested
        val newState = LostPetsTeaserReducer.loading(currentState)

        // Then - loading true, error cleared
        assertTrue(newState.isLoading, "Loading should be true")
        assertNull(newState.error, "Error should be null")
    }

    @Test
    fun `loading should clear previous error`() {
        // Given - state with error
        val currentState = LostPetsTeaserUiState(error = "Previous error")

        // When - loading state requested
        val newState = LostPetsTeaserReducer.loading(currentState)

        // Then - error cleared
        assertTrue(newState.isLoading, "Loading should be true")
        assertNull(newState.error, "Error should be cleared")
    }

    @Test
    fun `success should return success state with animals`() {
        // Given - loading state and animals
        val currentState = LostPetsTeaserUiState(isLoading = true)
        val animals =
            MockAnimalData.generateMockAnimals(5)
                .filter { it.status == AnimalStatus.MISSING }
                .take(5)

        // When - success reducer called
        val newState = LostPetsTeaserReducer.success(currentState, animals)

        // Then - loading false, animals present, no error
        assertFalse(newState.isLoading, "Loading should be false")
        assertEquals(animals.size, newState.animals.size, "Animals should match")
        assertNull(newState.error, "Error should be null")
        assertFalse(newState.isEmpty, "isEmpty should be false when animals present")
    }

    @Test
    fun `success with empty list should result in isEmpty true`() {
        // Given - loading state
        val currentState = LostPetsTeaserUiState(isLoading = true)

        // When - success with empty list
        val newState = LostPetsTeaserReducer.success(currentState, emptyList())

        // Then - isEmpty computed property should be true
        assertFalse(newState.isLoading, "Loading should be false")
        assertTrue(newState.animals.isEmpty(), "Animals should be empty")
        assertNull(newState.error, "Error should be null")
        assertTrue(newState.isEmpty, "isEmpty should be true when no animals")
    }

    @Test
    fun `error should return error state with message`() {
        // Given - loading state
        val currentState = LostPetsTeaserUiState(isLoading = true)
        val errorMessage = "Network error"

        // When - error reducer called
        val newState = LostPetsTeaserReducer.error(currentState, errorMessage)

        // Then - loading false, error present
        assertFalse(newState.isLoading, "Loading should be false")
        assertEquals(errorMessage, newState.error, "Error message should match")
    }

    @Test
    fun `error should preserve previous animals`() {
        // Given - state with animals loaded
        val animals =
            MockAnimalData.generateMockAnimals(3)
                .filter { it.status == AnimalStatus.MISSING }
                .take(3)
        val currentState = LostPetsTeaserUiState(animals = animals, isLoading = true)

        // When - error occurs
        val newState = LostPetsTeaserReducer.error(currentState, "Error occurred")

        // Then - animals should be preserved
        assertEquals(animals.size, newState.animals.size, "Previous animals should be preserved")
    }

    @Test
    fun `isEmpty should be false during loading`() {
        // Given & When - loading state
        val state = LostPetsTeaserUiState(isLoading = true, animals = emptyList())

        // Then - isEmpty should be false (we're still loading)
        assertFalse(state.isEmpty, "isEmpty should be false during loading")
    }

    @Test
    fun `isEmpty should be false when error present`() {
        // Given & When - error state with empty animals
        val state = LostPetsTeaserUiState(error = "Error", animals = emptyList())

        // Then - isEmpty should be false (it's an error, not empty)
        assertFalse(state.isEmpty, "isEmpty should be false when error present")
    }
}
