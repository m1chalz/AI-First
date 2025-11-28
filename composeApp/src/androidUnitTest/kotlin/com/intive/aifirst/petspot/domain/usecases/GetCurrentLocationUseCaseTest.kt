package com.intive.aifirst.petspot.domain.usecases

import com.intive.aifirst.petspot.fakes.FakeLocationRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for GetCurrentLocationUseCase.
 * Tests two-stage location fetch: (1) cached, (2) fresh with timeout.
 * Follows Given-When-Then structure per project constitution.
 */
class GetCurrentLocationUseCaseTest {
    @Test
    fun `invoke should return cached location when available (Stage 1 hit)`() =
        runTest {
            // Given - repository with cached location available
            val cachedLocation = FakeLocationRepository.SAMPLE_WARSAW
            val repository = FakeLocationRepository(cachedLocation = cachedLocation)
            val useCase = GetCurrentLocationUseCase(repository)

            // When - use case is invoked
            val result = useCase()

            // Then - should return cached location without requesting fresh
            assertTrue(result.isSuccess, "Result should be success")
            assertEquals(cachedLocation, result.getOrNull(), "Should return cached location")
            assertEquals(1, repository.getLastKnownLocationCallCount, "Should call getLastKnownLocation once")
            assertEquals(0, repository.requestFreshLocationCallCount, "Should NOT call requestFreshLocation")
        }

    @Test
    fun `invoke should request fresh location when cached is null (Stage 1 miss, Stage 2 success)`() =
        runTest {
            // Given - repository with no cached location but fresh location available
            val freshLocation = FakeLocationRepository.SAMPLE_KRAKOW
            val repository =
                FakeLocationRepository(
                    cachedLocation = null,
                    freshLocation = freshLocation,
                )
            val useCase = GetCurrentLocationUseCase(repository)

            // When - use case is invoked
            val result = useCase()

            // Then - should try cached first, then request fresh
            assertTrue(result.isSuccess, "Result should be success")
            assertEquals(freshLocation, result.getOrNull(), "Should return fresh location")
            assertEquals(1, repository.getLastKnownLocationCallCount, "Should call getLastKnownLocation once")
            assertEquals(1, repository.requestFreshLocationCallCount, "Should call requestFreshLocation once")
        }

    @Test
    fun `invoke should return null when both stages fail (Stage 1 miss, Stage 2 timeout)`() =
        runTest {
            // Given - repository with no cached and no fresh location (timeout simulation)
            val repository =
                FakeLocationRepository(
                    cachedLocation = null,
                    freshLocation = null,
                )
            val useCase = GetCurrentLocationUseCase(repository)

            // When - use case is invoked
            val result = useCase()

            // Then - should return success with null (fallback mode, not error)
            assertTrue(result.isSuccess, "Result should be success (fallback to no-location mode)")
            assertNull(result.getOrNull(), "Location should be null when both stages fail")
            assertEquals(1, repository.getLastKnownLocationCallCount, "Should call getLastKnownLocation")
            assertEquals(1, repository.requestFreshLocationCallCount, "Should call requestFreshLocation")
        }

    @Test
    fun `invoke should return failure when permission not granted`() =
        runTest {
            // Given - repository configured to throw SecurityException
            val repository =
                FakeLocationRepository(
                    shouldFailWithSecurityException = true,
                )
            val useCase = GetCurrentLocationUseCase(repository)

            // When - use case is invoked
            val result = useCase()

            // Then - should return failure with SecurityException
            assertTrue(result.isFailure, "Result should be failure")
            assertTrue(
                result.exceptionOrNull() is SecurityException,
                "Exception should be SecurityException",
            )
        }

    @Test
    fun `invoke should pass correct timeout to repository`() =
        runTest {
            // Given - repository with no cached location
            val repository =
                FakeLocationRepository(
                    cachedLocation = null,
                    freshLocation = FakeLocationRepository.SAMPLE_WARSAW,
                )
            val useCase = GetCurrentLocationUseCase(repository)

            // When - use case is invoked
            useCase()

            // Then - should pass default 10 second timeout
            assertNotNull(repository.lastRequestedTimeout, "Timeout should be passed to repository")
            assertEquals(10_000L, repository.lastRequestedTimeout, "Default timeout should be 10 seconds")
        }

    @Test
    fun `invoke should use custom timeout when specified`() =
        runTest {
            // Given - repository with no cached location
            val repository =
                FakeLocationRepository(
                    cachedLocation = null,
                    freshLocation = FakeLocationRepository.SAMPLE_WARSAW,
                )
            val customTimeout = 5_000L
            val useCase = GetCurrentLocationUseCase(repository, defaultTimeoutMs = customTimeout)

            // When - use case is invoked
            useCase()

            // Then - should pass custom timeout
            assertEquals(customTimeout, repository.lastRequestedTimeout, "Custom timeout should be used")
        }
}
