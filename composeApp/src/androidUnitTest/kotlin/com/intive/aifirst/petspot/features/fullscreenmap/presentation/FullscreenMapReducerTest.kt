package com.intive.aifirst.petspot.features.fullscreenmap.presentation

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapError
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapReducer
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapUiState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FullscreenMapReducerTest {
    private val testAnimal =
        Animal(
            id = "1",
            name = "Max",
            photoUrl = "https://example.com/max.jpg",
            location = Location(latitude = 52.2297, longitude = 21.0122),
            species = "Dog",
            breed = "Labrador",
            gender = AnimalGender.MALE,
            status = AnimalStatus.MISSING,
            lastSeenDate = "2026-01-08",
            description = "Friendly golden labrador",
            email = "owner@example.com",
            phone = "+48123456789",
            microchipNumber = null,
            rewardAmount = null,
            age = 3,
        )

    private val testLocation =
        LocationCoordinates(
            latitude = 52.2297,
            longitude = 21.0122,
        )

    // =====================================================
    // reduceAnimalsLoading tests
    // =====================================================

    @Test
    fun `reduceAnimalsLoading should set isLoadingAnimals to true`() {
        // Given
        val initialState = FullscreenMapUiState.Initial

        // When
        val newState = FullscreenMapReducer.reduceAnimalsLoading(initialState)

        // Then
        assertTrue(newState.isLoadingAnimals)
    }

    @Test
    fun `reduceAnimalsLoading should clear previous error`() {
        // Given
        val stateWithError = FullscreenMapUiState(error = FullscreenMapError.NetworkError)

        // When
        val newState = FullscreenMapReducer.reduceAnimalsLoading(stateWithError)

        // Then
        assertNull(newState.error)
    }

    @Test
    fun `reduceAnimalsLoading should preserve existing animals`() {
        // Given
        val stateWithAnimals = FullscreenMapUiState(animals = listOf(testAnimal))

        // When
        val newState = FullscreenMapReducer.reduceAnimalsLoading(stateWithAnimals)

        // Then
        assertEquals(listOf(testAnimal), newState.animals)
    }

    // =====================================================
    // reduceAnimalsSuccess tests
    // =====================================================

    @Test
    fun `reduceAnimalsSuccess should set animals and clear loading`() {
        // Given
        val loadingState = FullscreenMapUiState(isLoadingAnimals = true)
        val animals = listOf(testAnimal)

        // When
        val newState = FullscreenMapReducer.reduceAnimalsSuccess(loadingState, animals)

        // Then
        assertEquals(animals, newState.animals)
        assertFalse(newState.isLoadingAnimals)
        assertNull(newState.error)
    }

    @Test
    fun `reduceAnimalsSuccess should replace existing animals`() {
        // Given
        val oldAnimal = testAnimal.copy(id = "old")
        val stateWithOldAnimals = FullscreenMapUiState(animals = listOf(oldAnimal))
        val newAnimals = listOf(testAnimal)

        // When
        val newState = FullscreenMapReducer.reduceAnimalsSuccess(stateWithOldAnimals, newAnimals)

        // Then
        assertEquals(newAnimals, newState.animals)
    }

    // =====================================================
    // reduceAnimalsError tests
    // =====================================================

    @Test
    fun `reduceAnimalsError should set error type and clear loading`() {
        // Given
        val loadingState = FullscreenMapUiState(isLoadingAnimals = true)
        val errorType = FullscreenMapError.NetworkError

        // When
        val newState = FullscreenMapReducer.reduceAnimalsError(loadingState, errorType)

        // Then
        assertEquals(errorType, newState.error)
        assertFalse(newState.isLoadingAnimals)
    }

    @Test
    fun `reduceAnimalsError should preserve existing animals`() {
        // Given
        val stateWithAnimals =
            FullscreenMapUiState(
                animals = listOf(testAnimal),
                isLoadingAnimals = true,
            )

        // When
        val newState = FullscreenMapReducer.reduceAnimalsError(stateWithAnimals, FullscreenMapError.Unknown)

        // Then
        assertEquals(listOf(testAnimal), newState.animals)
    }

    // =====================================================
    // reduceLocationLoaded tests
    // =====================================================

    @Test
    fun `reduceLocationLoaded should set user location`() {
        // Given
        val initialState = FullscreenMapUiState.Initial

        // When
        val newState = FullscreenMapReducer.reduceLocationLoaded(initialState, testLocation)

        // Then
        assertEquals(testLocation, newState.userLocation)
    }

    // =====================================================
    // reduceAnimalSelected tests
    // =====================================================

    @Test
    fun `reduceAnimalSelected should set selectedAnimal`() {
        // Given
        val stateWithAnimals = FullscreenMapUiState(animals = listOf(testAnimal))

        // When
        val newState = FullscreenMapReducer.reduceAnimalSelected(stateWithAnimals, testAnimal)

        // Then
        assertEquals(testAnimal, newState.selectedAnimal)
        assertTrue(newState.isPopupVisible)
    }

    @Test
    fun `reduceAnimalSelected should replace previously selected animal`() {
        // Given
        val anotherAnimal = testAnimal.copy(id = "2", name = "Buddy")
        val stateWithSelection = FullscreenMapUiState(selectedAnimal = testAnimal)

        // When
        val newState = FullscreenMapReducer.reduceAnimalSelected(stateWithSelection, anotherAnimal)

        // Then
        assertEquals(anotherAnimal, newState.selectedAnimal)
    }

    // =====================================================
    // reducePopupDismissed tests
    // =====================================================

    @Test
    fun `reducePopupDismissed should clear selectedAnimal`() {
        // Given
        val stateWithPopup = FullscreenMapUiState(selectedAnimal = testAnimal)

        // When
        val newState = FullscreenMapReducer.reducePopupDismissed(stateWithPopup)

        // Then
        assertNull(newState.selectedAnimal)
        assertFalse(newState.isPopupVisible)
    }

    @Test
    fun `reducePopupDismissed should preserve animals list`() {
        // Given
        val stateWithPopup =
            FullscreenMapUiState(
                animals = listOf(testAnimal),
                selectedAnimal = testAnimal,
            )

        // When
        val newState = FullscreenMapReducer.reducePopupDismissed(stateWithPopup)

        // Then
        assertEquals(listOf(testAnimal), newState.animals)
    }

    // =====================================================
    // UiState computed properties tests
    // =====================================================

    @Test
    fun `animalsWithLocation should filter animals without coordinates`() {
        // Given
        val animalWithLocation = testAnimal
        val animalWithoutLocation =
            testAnimal.copy(
                id = "2",
                location = Location(latitude = null, longitude = null),
            )
        val state =
            FullscreenMapUiState(
                animals = listOf(animalWithLocation, animalWithoutLocation),
            )

        // When
        val result = state.animalsWithLocation

        // Then
        assertEquals(1, result.size)
        assertEquals(animalWithLocation, result.first())
    }

    @Test
    fun `hasError should return true when error is present`() {
        // Given
        val stateWithError = FullscreenMapUiState(error = FullscreenMapError.Unknown)

        // Then
        assertTrue(stateWithError.hasError)
    }

    @Test
    fun `hasError should return false when error is null`() {
        // Given
        val stateWithoutError = FullscreenMapUiState.Initial

        // Then
        assertFalse(stateWithoutError.hasError)
    }
}
