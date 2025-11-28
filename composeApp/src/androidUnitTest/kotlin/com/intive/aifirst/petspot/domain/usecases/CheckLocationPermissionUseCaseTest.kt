package com.intive.aifirst.petspot.domain.usecases

import com.intive.aifirst.petspot.domain.models.PermissionStatus
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for CheckLocationPermissionUseCase.
 * Tests permission status checking logic.
 * Follows Given-When-Then structure per project constitution.
 *
 * Note: These tests use a FakePermissionChecker to simulate Android permission states.
 * In actual implementation, the use case depends on Android Context for permission checks.
 */
class CheckLocationPermissionUseCaseTest {
    @Test
    fun `invoke should return Granted when both permissions are granted`() =
        runTest {
            // Given - permission checker returns both permissions granted
            val permissionChecker =
                FakePermissionChecker(
                    fineLocationGranted = true,
                    coarseLocationGranted = true,
                    shouldShowRationale = false,
                )
            val useCase = CheckLocationPermissionUseCase(permissionChecker)

            // When - use case is invoked
            val result = useCase()

            // Then - should return Granted with both permissions true
            assertTrue(result is PermissionStatus.Granted, "Should return Granted status")
            val granted = result as PermissionStatus.Granted
            assertTrue(granted.fineLocation, "Fine location should be granted")
            assertTrue(granted.coarseLocation, "Coarse location should be granted")
        }

    @Test
    fun `invoke should return Granted when only coarse permission is granted`() =
        runTest {
            // Given - only coarse location granted (Android 12+ approximate location)
            val permissionChecker =
                FakePermissionChecker(
                    fineLocationGranted = false,
                    coarseLocationGranted = true,
                    shouldShowRationale = false,
                )
            val useCase = CheckLocationPermissionUseCase(permissionChecker)

            // When - use case is invoked
            val result = useCase()

            // Then - should return Granted with coarse only
            assertTrue(result is PermissionStatus.Granted, "Should return Granted status")
            val granted = result as PermissionStatus.Granted
            assertFalse(granted.fineLocation, "Fine location should NOT be granted")
            assertTrue(granted.coarseLocation, "Coarse location should be granted")
        }

    @Test
    fun `invoke should return Denied with shouldShowRationale true when rationale should be shown`() =
        runTest {
            // Given - permissions denied but should show rationale
            val permissionChecker =
                FakePermissionChecker(
                    fineLocationGranted = false,
                    coarseLocationGranted = false,
                    shouldShowRationale = true,
                )
            val useCase = CheckLocationPermissionUseCase(permissionChecker)

            // When - use case is invoked
            val result = useCase()

            // Then - should return Denied with shouldShowRationale = true
            assertTrue(result is PermissionStatus.Denied, "Should return Denied status")
            val denied = result as PermissionStatus.Denied
            assertTrue(denied.shouldShowRationale, "shouldShowRationale should be true")
        }

    @Test
    fun `invoke should return Denied with shouldShowRationale false when permanently denied`() =
        runTest {
            // Given - permissions denied with "Don't ask again" (no rationale)
            val permissionChecker =
                FakePermissionChecker(
                    fineLocationGranted = false,
                    coarseLocationGranted = false,
                    shouldShowRationale = false,
                )
            val useCase = CheckLocationPermissionUseCase(permissionChecker)

            // When - use case is invoked
            val result = useCase()

            // Then - should return Denied with shouldShowRationale = false
            assertTrue(result is PermissionStatus.Denied, "Should return Denied status")
            val denied = result as PermissionStatus.Denied
            assertFalse(denied.shouldShowRationale, "shouldShowRationale should be false")
        }

    @Test
    fun `invoke should return NotRequested when permission has never been requested`() =
        runTest {
            // Given - permission checker indicates never requested state
            val permissionChecker =
                FakePermissionChecker(
                    fineLocationGranted = false,
                    coarseLocationGranted = false,
                    shouldShowRationale = false,
                    neverRequested = true,
                )
            val useCase = CheckLocationPermissionUseCase(permissionChecker)

            // When - use case is invoked
            val result = useCase()

            // Then - should return NotRequested
            assertTrue(result is PermissionStatus.NotRequested, "Should return NotRequested status")
        }
}

/**
 * Fake permission checker for testing.
 * Simulates Android permission check behavior without requiring Android Context.
 */
class FakePermissionChecker(
    private val fineLocationGranted: Boolean = false,
    private val coarseLocationGranted: Boolean = false,
    private val shouldShowRationale: Boolean = false,
    private val neverRequested: Boolean = false,
) : PermissionChecker {
    override fun isPermissionGranted(permission: String): Boolean =
        when (permission) {
            android.Manifest.permission.ACCESS_FINE_LOCATION -> fineLocationGranted
            android.Manifest.permission.ACCESS_COARSE_LOCATION -> coarseLocationGranted
            else -> false
        }

    override fun shouldShowRationale(permission: String): Boolean = shouldShowRationale

    override fun hasNeverBeenRequested(permission: String): Boolean = neverRequested
}
