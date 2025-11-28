package com.intive.aifirst.petspot.features.animallist.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.domain.models.PermissionStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for AnimalListReducer.
 * Tests all reducer branches: success, failure, empty, loading, and permission states.
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

    // ========================================
    // Permission State Transition Tests (US2)
    // ========================================

    @Test
    fun `requestingPermission should transition to Requesting status`() {
        // Given - initial state with NotRequested permission
        val currentState = AnimalListUiState.Initial
        assertTrue(
            currentState.permissionStatus is PermissionStatus.NotRequested,
            "Initial permission should be NotRequested",
        )

        // When - requesting permission
        val newState = AnimalListReducer.requestingPermission(currentState)

        // Then - permission status should be Requesting
        assertTrue(
            newState.permissionStatus is PermissionStatus.Requesting,
            "Permission status should be Requesting",
        )
    }

    @Test
    fun `permissionGranted should transition to Granted status with correct flags`() {
        // Given - state with Requesting permission
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Requesting,
            )

        // When - permission is granted with both fine and coarse
        val newState =
            AnimalListReducer.permissionGranted(
                currentState,
                fineLocation = true,
                coarseLocation = true,
            )

        // Then - permission status should be Granted with correct flags
        assertTrue(
            newState.permissionStatus is PermissionStatus.Granted,
            "Permission status should be Granted",
        )
        val granted = newState.permissionStatus as PermissionStatus.Granted
        assertTrue(granted.fineLocation, "Fine location should be true")
        assertTrue(granted.coarseLocation, "Coarse location should be true")
        assertTrue(newState.isLocationLoading, "Should start loading location")
    }

    @Test
    fun `permissionGranted should work with coarse only (Android 12+)`() {
        // Given - state with Requesting permission
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Requesting,
            )

        // When - only coarse permission is granted
        val newState =
            AnimalListReducer.permissionGranted(
                currentState,
                fineLocation = false,
                coarseLocation = true,
            )

        // Then - permission status should be Granted with coarse only
        val granted = newState.permissionStatus as PermissionStatus.Granted
        assertFalse(granted.fineLocation, "Fine location should be false")
        assertTrue(granted.coarseLocation, "Coarse location should be true")
    }

    @Test
    fun `permissionDenied should transition to Denied status with shouldShowRationale true`() {
        // Given - state with Requesting permission
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Requesting,
            )

        // When - permission is denied but rationale should be shown
        val newState =
            AnimalListReducer.permissionDenied(
                currentState,
                shouldShowRationale = true,
            )

        // Then - permission status should be Denied with shouldShowRationale = true
        assertTrue(
            newState.permissionStatus is PermissionStatus.Denied,
            "Permission status should be Denied",
        )
        val denied = newState.permissionStatus as PermissionStatus.Denied
        assertTrue(denied.shouldShowRationale, "shouldShowRationale should be true")
        assertFalse(newState.isLocationLoading, "Should not be loading location")
    }

    @Test
    fun `permissionDenied should transition to Denied status with shouldShowRationale false`() {
        // Given - state with Requesting permission
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Requesting,
            )

        // When - permission is denied with "Don't ask again"
        val newState =
            AnimalListReducer.permissionDenied(
                currentState,
                shouldShowRationale = false,
            )

        // Then - permission status should be Denied with shouldShowRationale = false
        val denied = newState.permissionStatus as PermissionStatus.Denied
        assertFalse(denied.shouldShowRationale, "shouldShowRationale should be false")
    }

    @Test
    fun `permission state transitions should preserve animal list`() {
        // Given - state with animals loaded
        val animals = MockAnimalData.generateMockAnimals(5)
        val currentState =
            AnimalListUiState(
                animals = animals,
                permissionStatus = PermissionStatus.NotRequested,
            )

        // When - permission granted
        val newState =
            AnimalListReducer.permissionGranted(
                currentState,
                fineLocation = true,
                coarseLocation = true,
            )

        // Then - animals should be preserved
        assertEquals(5, newState.animals.size, "Animals should be preserved during permission change")
    }

    // ========================================
    // User Story 3: Denied State and Rationale (US3)
    // ========================================

    @Test
    fun `rationaleShown should transition to rationale shown state`() {
        // Given - state with denied permission and rationale not yet shown
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Denied(shouldShowRationale = false),
                rationaleShownThisSession = false,
            )

        // When - rationale is shown
        val newState = AnimalListReducer.rationaleShown(currentState)

        // Then - rationaleShownThisSession should be true
        assertTrue(
            newState.rationaleShownThisSession,
            "rationaleShownThisSession should be true after showing rationale",
        )
    }

    @Test
    fun `rationaleShown should preserve permission status`() {
        // Given - denied permission state
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Denied(shouldShowRationale = true),
            )

        // When - rationale is shown
        val newState = AnimalListReducer.rationaleShown(currentState)

        // Then - permission status should be unchanged
        assertTrue(
            newState.permissionStatus is PermissionStatus.Denied,
            "Permission status should remain Denied",
        )
    }

    @Test
    fun `permissionDenied should not change rationaleShownThisSession flag`() {
        // Given - state with rationale already shown
        val currentState =
            AnimalListUiState.Initial.copy(
                rationaleShownThisSession = true,
            )

        // When - permission is denied
        val newState =
            AnimalListReducer.permissionDenied(
                currentState,
                shouldShowRationale = false,
            )

        // Then - rationaleShownThisSession should remain true
        assertTrue(
            newState.rationaleShownThisSession,
            "rationaleShownThisSession should not be reset on denial",
        )
    }

    // ========================================
    // User Story 4: Educational Rationale (US4)
    // ========================================

    @Test
    fun `permissionDenied with shouldShowRationale true indicates educational rationale needed`() {
        // Given - state after first denial (user can be asked again)
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Requesting,
            )

        // When - permission denied but rationale can be shown
        val newState =
            AnimalListReducer.permissionDenied(
                currentState,
                shouldShowRationale = true,
            )

        // Then - state indicates educational rationale is appropriate
        val denied = newState.permissionStatus as PermissionStatus.Denied
        assertTrue(
            denied.shouldShowRationale,
            "shouldShowRationale should be true for educational rationale flow",
        )
    }

    @Test
    fun `permissionDenied with shouldShowRationale false indicates settings needed`() {
        // Given - state after user selected "Don't ask again"
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Requesting,
            )

        // When - permission denied with "Don't ask again"
        val newState =
            AnimalListReducer.permissionDenied(
                currentState,
                shouldShowRationale = false,
            )

        // Then - state indicates Settings navigation is needed
        val denied = newState.permissionStatus as PermissionStatus.Denied
        assertFalse(
            denied.shouldShowRationale,
            "shouldShowRationale should be false when user selected Don't ask again",
        )
    }

    // ========================================
    // User Story 5: Dynamic Permission Changes (US5)
    // ========================================

    @Test
    fun `permissionGranted from Denied state should trigger location loading`() {
        // Given - previously denied permission state
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Denied(shouldShowRationale = false),
                location = null,
            )

        // When - permission is granted (user enabled via Settings)
        val newState =
            AnimalListReducer.permissionGranted(
                currentState,
                fineLocation = true,
                coarseLocation = true,
            )

        // Then - location loading should start
        assertTrue(
            newState.isLocationLoading,
            "Location loading should start when permission granted after denial",
        )
        assertTrue(
            newState.permissionStatus is PermissionStatus.Granted,
            "Permission status should transition to Granted",
        )
    }

    @Test
    fun `permissionDenied from Granted state should clear location`() {
        // Given - previously granted permission with location
        val currentState =
            AnimalListUiState.Initial.copy(
                permissionStatus = PermissionStatus.Granted(fineLocation = true, coarseLocation = true),
                location = com.intive.aifirst.petspot.domain.models.LocationCoordinates(52.0, 21.0),
            )

        // When - permission is revoked (user disabled via Settings)
        val newState =
            AnimalListReducer.permissionDenied(
                currentState,
                shouldShowRationale = false,
            )

        // Then - location should be cleared
        assertNull(newState.location, "Location should be cleared when permission revoked")
        assertTrue(
            newState.permissionStatus is PermissionStatus.Denied,
            "Permission status should transition to Denied",
        )
    }
}
