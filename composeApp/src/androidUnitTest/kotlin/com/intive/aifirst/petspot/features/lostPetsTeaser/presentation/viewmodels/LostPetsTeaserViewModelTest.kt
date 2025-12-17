package com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetRecentAnimalsUseCase
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserEffect
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent
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
 * Unit tests for LostPetsTeaserViewModel.
 * Follows Given-When-Then pattern with Turbine for Flow testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LostPetsTeaserViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAnimalRepository
    private lateinit var useCase: GetRecentAnimalsUseCase
    private lateinit var viewModel: LostPetsTeaserViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAnimalRepository()
        useCase = GetRecentAnimalsUseCase(fakeRepository)
        viewModel = LostPetsTeaserViewModel(useCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit loading then success state when LoadData dispatched`() =
        runTest {
            // Given - repository with MISSING animals
            val allAnimals = MockAnimalData.generateMockAnimals(10)
            fakeRepository.setAnimals(allAnimals)

            viewModel.state.test {
                // Initial state
                val initial = awaitItem()
                assertFalse(initial.isLoading)
                assertTrue(initial.animals.isEmpty())

                // When
                viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)

                // Then - loading state
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Then - success state
                testDispatcher.scheduler.advanceUntilIdle()
                val success = awaitItem()
                assertFalse(success.isLoading)
                assertTrue(success.animals.isNotEmpty())
                assertNull(success.error)
            }
        }

    @Test
    fun `should emit loading then error state when repository fails`() =
        runTest {
            // Given - repository that throws
            fakeRepository.shouldThrowException = true

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)

                // Then - loading state
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Then - error state
                testDispatcher.scheduler.advanceUntilIdle()
                val error = awaitItem()
                assertFalse(error.isLoading)
                assertTrue(error.animals.isEmpty())
                assertEquals("Test exception", error.error)
            }
        }

    @Test
    fun `should show empty state when no MISSING pets available`() =
        runTest {
            // Given - only FOUND animals
            val foundAnimals =
                MockAnimalData.generateMockAnimals(5)
                    .filter { it.status != AnimalStatus.MISSING }
            fakeRepository.setAnimals(foundAnimals)

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)

                // Then - loading
                awaitItem()

                // Then - empty state
                testDispatcher.scheduler.advanceUntilIdle()
                val emptyState = awaitItem()
                assertFalse(emptyState.isLoading)
                assertTrue(emptyState.animals.isEmpty())
                assertNull(emptyState.error)
                assertTrue(emptyState.isEmpty)
            }
        }

    @Test
    fun `should emit NavigateToPetDetails effect when PetClicked dispatched`() =
        runTest {
            // Given
            val petId = "pet-123"

            viewModel.effects.test {
                // When
                viewModel.dispatchIntent(LostPetsTeaserIntent.PetClicked(petId))
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is LostPetsTeaserEffect.NavigateToPetDetails)
                assertEquals(petId, (effect as LostPetsTeaserEffect.NavigateToPetDetails).petId)
            }
        }

    @Test
    fun `should emit NavigateToLostPetsList effect when ViewAllClicked dispatched`() =
        runTest {
            viewModel.effects.test {
                // When
                viewModel.dispatchIntent(LostPetsTeaserIntent.ViewAllClicked)
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is LostPetsTeaserEffect.NavigateToLostPetsList)
            }
        }

    @Test
    fun `should only load data once when LoadData dispatched multiple times`() =
        runTest {
            // Given
            val allAnimals = MockAnimalData.generateMockAnimals(5)
            fakeRepository.setAnimals(allAnimals)

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When - first load
                viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)
                awaitItem() // loading
                testDispatcher.scheduler.advanceUntilIdle()
                val firstLoad = awaitItem() // success

                // When - second load attempt (per spec: data loads once)
                viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)
                testDispatcher.scheduler.advanceUntilIdle()

                // Then - no new emissions (data already loaded, per spec)
                expectNoEvents()
                assertEquals(firstLoad.animals.size, viewModel.state.value.animals.size)
            }
        }

    @Test
    fun `RefreshData should reload data even after initial load`() =
        runTest {
            // Given - initial load
            val allAnimals = MockAnimalData.generateMockAnimals(5)
            fakeRepository.setAnimals(allAnimals)
            viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                // Skip to current state
                val current = awaitItem()
                assertFalse(current.isLoading)

                // When - refresh
                viewModel.dispatchIntent(LostPetsTeaserIntent.RefreshData)

                // Then - should reload (loading state)
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                testDispatcher.scheduler.advanceUntilIdle()
                val refreshed = awaitItem()
                assertFalse(refreshed.isLoading)
            }
        }

    /**
     * Fake repository for testing.
     */
    private class FakeAnimalRepository : AnimalRepository {
        private var animals: List<Animal> = emptyList()
        var shouldThrowException = false

        fun setAnimals(animals: List<Animal>) {
            this.animals = animals
        }

        override suspend fun getAnimals(
            lat: Double?,
            lng: Double?,
            range: Int?,
        ): List<Animal> {
            if (shouldThrowException) throw RuntimeException("Test exception")
            return animals
        }

        override suspend fun getAnimalById(id: String): Animal {
            if (shouldThrowException) throw RuntimeException("Test exception")
            return animals.find { it.id == id }
                ?: throw NoSuchElementException("Animal not found: $id")
        }
    }
}
