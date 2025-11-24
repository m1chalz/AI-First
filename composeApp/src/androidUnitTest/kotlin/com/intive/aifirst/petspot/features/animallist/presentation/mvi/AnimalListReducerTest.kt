package com.intive.aifirst.petspot.features.animallist.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for AnimalListReducer.
 * Tests all reducer branches: success, failure, empty, loading.
 * Follows Given-When-Then structure per project constitution.
 */
class AnimalListReducerTest {
    
    @Test
    fun `reduce should return success state when result is success with animals`() {
        // Given - initial state and successful result with animals
        val currentState = AnimalListUiState.Initial
        val mockAnimals = MockAnimalData.generateMockAnimals(5)
        val result = Result.success(mockAnimals)
        
        // When - reducer processes the result
        val newState = AnimalListReducer.reduce(currentState, result)
        
        // Then - state contains animals, not loading, no error
        assertEquals(5, newState.animals.size, "State should contain 5 animals")
        assertFalse(newState.isLoading, "Loading should be false")
        assertNull(newState.error, "Error should be null")
        assertFalse(newState.isEmpty, "isEmpty should be false when animals present")
    }
    
    @Test
    fun `reduce should return error state when result is failure`() {
        // Given - state with previous animals and failure result
        val previousAnimals = MockAnimalData.generateMockAnimals(3)
        val currentState = AnimalListUiState(animals = previousAnimals, isLoading = true)
        val exception = Exception("Network error")
        val result = Result.failure<List<com.intive.aifirst.petspot.composeapp.domain.models.Animal>>(exception)
        
        // When - reducer processes the failure
        val newState = AnimalListReducer.reduce(currentState, result)
        
        // Then - state preserves previous animals, shows error, not loading
        assertEquals(3, newState.animals.size, "Should preserve previous animals")
        assertFalse(newState.isLoading, "Loading should be false")
        assertEquals("Network error", newState.error, "Error message should match exception")
        assertFalse(newState.isEmpty, "isEmpty should be false when animals present despite error")
    }
    
    @Test
    fun `reduce should return empty state when result is success with empty list`() {
        // Given - initial state and successful result with empty list
        val currentState = AnimalListUiState.Initial
        val result = Result.success(emptyList<com.intive.aifirst.petspot.composeapp.domain.models.Animal>())
        
        // When - reducer processes the empty result
        val newState = AnimalListReducer.reduce(currentState, result)
        
        // Then - state has empty list, not loading, no error, isEmpty true
        assertTrue(newState.animals.isEmpty(), "Animals list should be empty")
        assertFalse(newState.isLoading, "Loading should be false")
        assertNull(newState.error, "Error should be null")
        assertTrue(newState.isEmpty, "isEmpty should be true when no animals")
    }
    
    @Test
    fun `loading should return loading state`() {
        // Given - initial state
        val currentState = AnimalListUiState.Initial
        
        // When - loading state is requested
        val newState = AnimalListReducer.loading(currentState)
        
        // Then - state is loading with no error
        assertTrue(newState.isLoading, "Loading should be true")
        assertNull(newState.error, "Error should be null")
    }
}

