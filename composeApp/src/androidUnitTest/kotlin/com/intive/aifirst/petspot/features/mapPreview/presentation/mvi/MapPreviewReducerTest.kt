package com.intive.aifirst.petspot.features.mapPreview.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for MapPreviewReducer.
 * Tests pure state transitions without side effects.
 */
class MapPreviewReducerTest {
    @Test
    fun `loading should set isLoading to true and clear error`() {
        // Given
        val state =
            MapPreviewUiState.Initial.copy(
                error = MapPreviewError.NetworkError,
            )

        // When
        val result = MapPreviewReducer.loading(state)

        // Then
        assertTrue(result.isLoading)
        assertNull(result.error)
    }

    @Test
    fun `success should update location and animals and clear loading`() {
        // Given
        val state = MapPreviewUiState.Initial.copy(isLoading = true)
        val location = LocationCoordinates(52.2297, 21.0122)
        val animals = MockAnimalData.generateMockAnimals(5)

        // When
        val result = MapPreviewReducer.success(state, location, animals)

        // Then
        assertFalse(result.isLoading)
        assertEquals(location, result.userLocation)
        assertEquals(5, result.animals.size)
        assertNull(result.error)
    }

    @Test
    fun `error should set error and clear loading`() {
        // Given
        val state = MapPreviewUiState.Initial.copy(isLoading = true)
        val error = MapPreviewError.LocationNotAvailable

        // When
        val result = MapPreviewReducer.error(state, error)

        // Then
        assertFalse(result.isLoading)
        assertEquals(error, result.error)
    }

    @Test
    fun `permissionGranted should update permission status to granted`() {
        // Given
        val state = MapPreviewUiState.Initial

        // When
        val result = MapPreviewReducer.permissionGranted(state)

        // Then
        assertTrue(result.permissionStatus is PermissionStatus.Granted)
    }

    @Test
    fun `permissionDenied should update permission status to denied`() {
        // Given
        val state = MapPreviewUiState.Initial

        // When
        val result = MapPreviewReducer.permissionDenied(state, shouldShowRationale = true)

        // Then
        assertTrue(result.permissionStatus is PermissionStatus.Denied)
        assertTrue((result.permissionStatus as PermissionStatus.Denied).shouldShowRationale)
    }

    @Test
    fun `permissionRequesting should update permission status to requesting`() {
        // Given
        val state = MapPreviewUiState.Initial

        // When
        val result = MapPreviewReducer.permissionRequesting(state)

        // Then
        assertEquals(PermissionStatus.Requesting, result.permissionStatus)
    }

    @Test
    fun `success should preserve permission status`() {
        // Given
        val state =
            MapPreviewUiState.Initial.copy(
                isLoading = true,
                permissionStatus = PermissionStatus.Granted(fineLocation = true, coarseLocation = true),
            )
        val location = LocationCoordinates(52.2297, 21.0122)
        val animals = MockAnimalData.generateMockAnimals(3)

        // When
        val result = MapPreviewReducer.success(state, location, animals)

        // Then
        assertTrue(result.permissionStatus is PermissionStatus.Granted)
    }

    @Test
    fun `animalsWithLocation should filter animals without coordinates`() {
        // Given
        val animals = MockAnimalData.generateMockAnimals(5)
        val location = LocationCoordinates(52.2297, 21.0122)
        val state =
            MapPreviewReducer.success(
                MapPreviewUiState.Initial,
                location,
                animals,
            )

        // When
        val filtered = state.animalsWithLocation

        // Then - all mock animals have coordinates
        assertEquals(animals.size, filtered.size)
    }

    @Test
    fun `canShowMap should be true when permission granted and location available`() {
        // Given
        val location = LocationCoordinates(52.2297, 21.0122)
        val animals = MockAnimalData.generateMockAnimals(3)
        var state = MapPreviewUiState.Initial

        // When - grant permission and add location
        state = MapPreviewReducer.permissionGranted(state)
        state = MapPreviewReducer.success(state, location, animals)

        // Then
        assertTrue(state.canShowMap)
    }

    @Test
    fun `canShowMap should be false when loading`() {
        // Given
        var state = MapPreviewUiState.Initial
        state = MapPreviewReducer.permissionGranted(state)
        state = state.copy(userLocation = LocationCoordinates(52.2297, 21.0122))

        // When
        state = MapPreviewReducer.loading(state)

        // Then
        assertFalse(state.canShowMap)
    }

    @Test
    fun `canShowMap should be false when error exists`() {
        // Given
        var state = MapPreviewUiState.Initial
        state = MapPreviewReducer.permissionGranted(state)
        state = state.copy(userLocation = LocationCoordinates(52.2297, 21.0122))

        // When
        state = MapPreviewReducer.error(state, MapPreviewError.NetworkError)

        // Then
        assertFalse(state.canShowMap)
    }
}
