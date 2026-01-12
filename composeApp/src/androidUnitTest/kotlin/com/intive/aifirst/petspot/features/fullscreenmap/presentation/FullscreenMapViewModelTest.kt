package com.intive.aifirst.petspot.features.fullscreenmap.presentation

import app.cash.turbine.test
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.repositories.LocationRepository
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapEffect
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapError
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.viewmodels.FullscreenMapViewModel
import com.intive.aifirst.petspot.features.mapPreview.domain.usecases.GetNearbyAnimalsForMapUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

@OptIn(ExperimentalCoroutinesApi::class)
class FullscreenMapViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val testLocation = LocationCoordinates(52.2297, 21.0122)

    private val testBounds =
        LatLngBounds(
            LatLng(52.0, 21.0),
            LatLng(52.5, 21.5),
        )

    private lateinit var fakeLocationRepository: FakeLocationRepository
    private lateinit var fakeAnimalRepository: FakeAnimalRepository
    private lateinit var getCurrentLocationUseCase: GetCurrentLocationUseCase
    private lateinit var getNearbyAnimalsForMapUseCase: GetNearbyAnimalsForMapUseCase

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeLocationRepository = FakeLocationRepository()
        fakeAnimalRepository = FakeAnimalRepository()
        getCurrentLocationUseCase = GetCurrentLocationUseCase(fakeLocationRepository)
        getNearbyAnimalsForMapUseCase = GetNearbyAnimalsForMapUseCase(fakeAnimalRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        FullscreenMapViewModel(
            getCurrentLocationUseCase = getCurrentLocationUseCase,
            getNearbyAnimalsForMapUseCase = getNearbyAnimalsForMapUseCase,
        )

    // =====================================================
    // US1: OnBackPressed tests
    // =====================================================

    @Test
    fun `should emit NavigateBack effect when OnBackPressed is dispatched`() =
        runTest {
            // Given
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.setAnimals(emptyList())
            val viewModel = createViewModel()

            // When
            viewModel.effects.test {
                viewModel.dispatchIntent(FullscreenMapIntent.OnBackPressed)
                advanceUntilIdle()

                // Then
                assertEquals(FullscreenMapEffect.NavigateBack, awaitItem())
            }
        }

    // =====================================================
    // US1: Initialize tests
    // =====================================================

    @Test
    fun `should load user location and animals on Initialize success`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(3)
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.setAnimals(animals)
            val viewModel = createViewModel()

            // When
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Then - verify final state
            val state = viewModel.state.value
            assertEquals(testLocation, state.userLocation)
            assertEquals(3, state.animals.size)
            assertFalse(state.isLoadingAnimals)
            assertNull(state.error)
        }

    @Test
    fun `should set error state when location is null`() =
        runTest {
            // Given
            fakeLocationRepository.setLocation(null)
            val viewModel = createViewModel()

            // When
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertTrue(state.hasError)
            assertEquals(FullscreenMapError.PermissionDenied, state.error)
        }

    @Test
    fun `should set error state when location fails with SecurityException`() =
        runTest {
            // Given
            fakeLocationRepository.shouldThrowSecurityException = true
            val viewModel = createViewModel()

            // When
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertTrue(state.hasError)
            assertEquals(FullscreenMapError.PermissionDenied, state.error)
        }

    // =====================================================
    // US2: OnViewportChanged tests
    // =====================================================

    @Test
    fun `should load animals when viewport changes`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(5)
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.setAnimals(animals)
            val viewModel = createViewModel()

            // Initialize first
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // When - change viewport
            viewModel.dispatchIntent(FullscreenMapIntent.OnViewportChanged(testBounds))
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(5, state.animals.size)
            assertFalse(state.isLoadingAnimals)
        }

    // =====================================================
    // US3: Loading/Success/Error state tests
    // =====================================================

    @Test
    fun `should set error state when fetching animals fails`() =
        runTest {
            // Given
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.shouldThrowException = true
            fakeAnimalRepository.exceptionToThrow = java.io.IOException("Network error")
            val viewModel = createViewModel()

            // When
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertTrue(state.hasError)
            assertEquals(FullscreenMapError.NetworkError, state.error)
        }

    @Test
    fun `should reload animals when OnRetryTapped is dispatched`() =
        runTest {
            // Given - initial failure
            fakeLocationRepository.setLocation(null)
            val viewModel = createViewModel()

            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Verify error state
            assertTrue(viewModel.state.value.hasError)

            // Fix the repository
            val animals = MockAnimalData.generateMockAnimals(3)
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.setAnimals(animals)

            // When - retry
            viewModel.dispatchIntent(FullscreenMapIntent.OnRetryTapped)
            advanceUntilIdle()

            // Then - success
            val state = viewModel.state.value
            assertNull(state.error)
            assertEquals(testLocation, state.userLocation)
            assertEquals(3, state.animals.size)
        }

    // =====================================================
    // US4: OnAnimalTapped tests
    // =====================================================

    @Test
    fun `should set selectedAnimal when OnAnimalTapped is dispatched`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(3)
            val targetAnimal = animals.first()
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.setAnimals(animals)
            val viewModel = createViewModel()

            // Initialize to load animals
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Verify animals are loaded
            assertEquals(3, viewModel.state.value.animals.size)

            // When
            viewModel.dispatchIntent(FullscreenMapIntent.OnAnimalTapped(targetAnimal.id))

            // Then
            val state = viewModel.state.value
            assertEquals(targetAnimal, state.selectedAnimal)
            assertTrue(state.isPopupVisible)
        }

    @Test
    fun `should clear selectedAnimal when OnPopupDismissed is dispatched`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(3)
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.setAnimals(animals)
            val viewModel = createViewModel()

            // Initialize to load animals
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Tap an animal
            viewModel.dispatchIntent(FullscreenMapIntent.OnAnimalTapped(animals.first().id))
            assertTrue(viewModel.state.value.isPopupVisible)

            // When
            viewModel.dispatchIntent(FullscreenMapIntent.OnPopupDismissed)

            // Then
            val state = viewModel.state.value
            assertNull(state.selectedAnimal)
            assertFalse(state.isPopupVisible)
        }

    @Test
    fun `should update selectedAnimal when different pin is tapped`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(3)
            val firstAnimal = animals[0]
            val secondAnimal = animals[1]
            fakeLocationRepository.setLocation(testLocation)
            fakeAnimalRepository.setAnimals(animals)
            val viewModel = createViewModel()

            // Initialize to load animals
            viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
            advanceUntilIdle()

            // Tap first animal
            viewModel.dispatchIntent(FullscreenMapIntent.OnAnimalTapped(firstAnimal.id))
            assertEquals(firstAnimal, viewModel.state.value.selectedAnimal)

            // When - tap second animal
            viewModel.dispatchIntent(FullscreenMapIntent.OnAnimalTapped(secondAnimal.id))

            // Then
            assertEquals(secondAnimal, viewModel.state.value.selectedAnimal)
        }

    // =====================================================
    // Fake Repositories
    // =====================================================

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
