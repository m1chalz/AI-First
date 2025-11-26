package com.intive.aifirst.petspot.features.petdetails.presentation.mvi

import com.intive.aifirst.petspot.domain.fixtures.MockAnimalData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for PetDetailsReducer.
 * Follows Given-When-Then pattern with pure function testing.
 */
class PetDetailsReducerTest {
    
    @Test
    fun `loading should set isLoading to true and clear error`() {
        // Given
        val initialState = PetDetailsUiState(
            isLoading = false,
            error = "Previous error"
        )
        
        // When
        val newState = PetDetailsReducer.loading(initialState)
        
        // Then
        assertTrue(newState.isLoading)
        assertNull(newState.error)
    }
    
    @Test
    fun `loading should preserve pet data`() {
        // Given
        val pet = MockAnimalData.generateMockAnimals().first()
        val initialState = PetDetailsUiState(
            pet = pet,
            isLoading = false,
            error = null
        )
        
        // When
        val newState = PetDetailsReducer.loading(initialState)
        
        // Then
        assertEquals(pet, newState.pet)
    }
    
    @Test
    fun `reduce should update state with pet on success`() {
        // Given
        val initialState = PetDetailsUiState(isLoading = true)
        val pet = MockAnimalData.generateMockAnimals().first()
        val result = Result.success(pet)
        
        // When
        val newState = PetDetailsReducer.reduce(initialState, result)
        
        // Then
        assertEquals(pet, newState.pet)
        assertFalse(newState.isLoading)
        assertNull(newState.error)
    }
    
    @Test
    fun `reduce should set error and clear pet on failure`() {
        // Given
        val pet = MockAnimalData.generateMockAnimals().first()
        val initialState = PetDetailsUiState(
            pet = pet,
            isLoading = true
        )
        val errorMessage = "Network error"
        val result = Result.failure<com.intive.aifirst.petspot.domain.models.Animal>(
            RuntimeException(errorMessage)
        )
        
        // When
        val newState = PetDetailsReducer.reduce(initialState, result)
        
        // Then
        assertNull(newState.pet)
        assertFalse(newState.isLoading)
        assertEquals(errorMessage, newState.error)
    }
    
    @Test
    fun `reduce should handle exception without message`() {
        // Given
        val initialState = PetDetailsUiState(isLoading = true)
        val result = Result.failure<com.intive.aifirst.petspot.domain.models.Animal>(
            RuntimeException()
        )
        
        // When
        val newState = PetDetailsReducer.reduce(initialState, result)
        
        // Then
        assertNull(newState.pet)
        assertFalse(newState.isLoading)
        assertEquals("Unknown error occurred", newState.error)
    }
}

