package com.intive.aifirst.petspot.features.mapPreview.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus
import com.intive.aifirst.petspot.domain.repositories.LocationRepository
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.features.mapPreview.domain.usecases.GetNearbyAnimalsForMapUseCase
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewError
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for MapPreviewViewModel.
 * Follows Given-When-Then pattern with Turbine for Flow testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MapPreviewViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeLocationRepository: FakeLocationRepository
    private lateinit var fakeAnimalRepository: FakeAnimalRepository
    private lateinit var getCurrentLocationUseCase: GetCurrentLocationUseCase
    private lateinit var getNearbyAnimalsForMapUseCase: GetNearbyAnimalsForMapUseCase
    private lateinit var viewModel: MapPreviewViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeLocationRepository = FakeLocationRepository()
        fakeAnimalRepository = FakeAnimalRepository()
        getCurrentLocationUseCase = GetCurrentLocationUseCase(fakeLocationRepository)
        getNearbyAnimalsForMapUseCase = GetNearbyAnimalsForMapUseCase(fakeAnimalRepository)
        viewModel = MapPreviewViewModel(getCurrentLocationUseCase, getNearbyAnimalsForMapUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit loading then success state when LoadMap dispatched`() =
        runTest {
            // Given
            val location = LocationCoordinates(52.2297, 21.0122)
            val animals = MockAnimalData.generateMockAnimals(5)
            fakeLocationRepository.setLocation(location)
            fakeAnimalRepository.setAnimals(animals)

            viewModel.state.test {
                // Initial state
                val initial = awaitItem()
                assertFalse(initial.isLoading)
                assertTrue(initial.animals.isEmpty())

                // When
                viewModel.dispatchIntent(MapPreviewIntent.LoadMap)

                // Then - loading state
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Then - success state
                testDispatcher.scheduler.advanceUntilIdle()
                val success = awaitItem()
                assertFalse(success.isLoading)
                assertEquals(location, success.userLocation)
                assertEquals(5, success.animals.size)
                assertNull(success.error)
            }
        }

    @Test
    fun `should emit error state when location unavailable`() =
        runTest {
            // Given
            fakeLocationRepository.setLocation(null)

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(MapPreviewIntent.LoadMap)

                // Then - loading state
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Then - error state
                testDispatcher.scheduler.advanceUntilIdle()
                val error = awaitItem()
                assertFalse(error.isLoading)
                assertEquals(MapPreviewError.LocationNotAvailable, error.error)
            }
        }

    @Test
    fun `should emit network error when animals fetch fails`() =
        runTest {
            // Given
            val location = LocationCoordinates(52.2297, 21.0122)
            fakeLocationRepository.setLocation(location)
            fakeAnimalRepository.shouldThrowException = true
            fakeAnimalRepository.exceptionToThrow = java.io.IOException("Network error")

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(MapPreviewIntent.LoadMap)

                // Skip loading
                awaitItem()

                // Then - error state
                testDispatcher.scheduler.advanceUntilIdle()
                val error = awaitItem()
                assertEquals(MapPreviewError.NetworkError, error.error)
            }
        }

    @Test
    fun `should emit location error when location fetch throws SecurityException`() =
        runTest {
            // Given
            fakeLocationRepository.shouldThrowSecurityException = true

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(MapPreviewIntent.LoadMap)

                // Skip loading
                awaitItem()

                // Then - error state
                testDispatcher.scheduler.advanceUntilIdle()
                val error = awaitItem()
                assertEquals(MapPreviewError.LocationNotAvailable, error.error)
            }
        }

    @Test
    fun `should update permission status when PermissionGranted dispatched`() =
        runTest {
            // Given
            val location = LocationCoordinates(52.2297, 21.0122)
            val animals = MockAnimalData.generateMockAnimals(3)
            fakeLocationRepository.setLocation(location)
            fakeAnimalRepository.setAnimals(animals)

            viewModel.state.test {
                // Initial state
                val initial = awaitItem()
                assertEquals(PermissionStatus.NotRequested, initial.permissionStatus)

                // When
                viewModel.dispatchIntent(MapPreviewIntent.PermissionGranted)

                // Then - permission granted, then loading triggered
                val permissionGranted = awaitItem()
                assertTrue(permissionGranted.permissionStatus is PermissionStatus.Granted)

                // Then - loading
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Then - success
                testDispatcher.scheduler.advanceUntilIdle()
                val success = awaitItem()
                assertTrue(success.canShowMap)
            }
        }

    @Test
    fun `should update permission status when PermissionDenied dispatched`() =
        runTest {
            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(MapPreviewIntent.PermissionDenied)

                // Then
                val denied = awaitItem()
                assertTrue(denied.permissionStatus is PermissionStatus.Denied)
            }
        }

    @Test
    fun `should update permission status when RequestPermission dispatched`() =
        runTest {
            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(MapPreviewIntent.RequestPermission)

                // Then
                val requesting = awaitItem()
                assertEquals(PermissionStatus.Requesting, requesting.permissionStatus)
            }
        }

    @Test
    fun `Retry should reload data after error`() =
        runTest {
            // Given - initial failure, then fix
            fakeLocationRepository.setLocation(null)

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When - first attempt fails
                viewModel.dispatchIntent(MapPreviewIntent.LoadMap)
                awaitItem() // loading
                testDispatcher.scheduler.advanceUntilIdle()
                val errorState = awaitItem()
                assertEquals(MapPreviewError.LocationNotAvailable, errorState.error)

                // Fix the repository
                val location = LocationCoordinates(52.2297, 21.0122)
                val animals = MockAnimalData.generateMockAnimals(3)
                fakeLocationRepository.setLocation(location)
                fakeAnimalRepository.setAnimals(animals)

                // When - retry
                viewModel.dispatchIntent(MapPreviewIntent.Retry)

                // Then - loading
                val retryLoading = awaitItem()
                assertTrue(retryLoading.isLoading)

                // Then - success
                testDispatcher.scheduler.advanceUntilIdle()
                val success = awaitItem()
                assertNull(success.error)
                assertEquals(location, success.userLocation)
            }
        }

    /**
     * Fake location repository for testing.
     */
    private class FakeLocationRepository : LocationRepository {
        private var location: LocationCoordinates? = null
        var shouldThrowSecurityException = false

        fun setLocation(location: LocationCoordinates?) {
            this.location = location
        }

        override suspend fun getLastKnownLocation(): LocationCoordinates? {
            if (shouldThrowSecurityException) {
                throw SecurityException("Permission denied")
            }
            return location
        }

        override suspend fun requestFreshLocation(timeoutMs: Long): LocationCoordinates? {
            if (shouldThrowSecurityException) {
                throw SecurityException("Permission denied")
            }
            return location
        }
    }

    /**
     * Fake animal repository for testing.
     */
    private class FakeAnimalRepository : AnimalRepository {
        private var animals: List<Animal> = emptyList()
        var shouldThrowException = false
        var exceptionToThrow: Throwable = RuntimeException("Test exception")

        fun setAnimals(animals: List<Animal>) {
            this.animals = animals
        }

        override suspend fun getAnimals(
            lat: Double?,
            lng: Double?,
            range: Int?,
        ): List<Animal> {
            if (shouldThrowException) throw exceptionToThrow
            return animals
        }

        override suspend fun getAnimalById(id: String): Animal {
            if (shouldThrowException) throw exceptionToThrow
            return animals.find { it.id == id }
                ?: throw NoSuchElementException("Animal not found: $id")
        }
    }
}
