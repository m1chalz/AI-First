package com.intive.aifirst.petspot.features.petdetails.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalByIdUseCase
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsEffect
import com.intive.aifirst.petspot.features.petdetails.presentation.mvi.PetDetailsIntent
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
 * Unit tests for PetDetailsViewModel.
 * Follows Given-When-Then pattern with Turbine for Flow testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PetDetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAnimalRepository
    private lateinit var useCase: GetAnimalByIdUseCase
    private lateinit var viewModel: PetDetailsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAnimalRepository()
        useCase = GetAnimalByIdUseCase(fakeRepository)
        viewModel = PetDetailsViewModel(useCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadPet should emit loading then success state`() =
        runTest {
            // Given
            val pet = MockAnimalData.generateMockAnimals().first()
            fakeRepository.result = pet

            viewModel.state.test {
                // Initial state
                val initial = awaitItem()
                assertFalse(initial.isLoading)
                assertNull(initial.pet)

                // When
                viewModel.dispatchIntent(PetDetailsIntent.LoadPet(pet.id))

                // Then - loading state
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Then - success state
                testDispatcher.scheduler.advanceUntilIdle()
                val success = awaitItem()
                assertFalse(success.isLoading)
                assertEquals(pet.id, success.pet?.id)
                assertNull(success.error)
            }
        }

    @Test
    fun `LoadPet should emit loading then error state on failure`() =
        runTest {
            // Given
            val errorMessage = "Pet not found"
            fakeRepository.error = RuntimeException(errorMessage)

            viewModel.state.test {
                // Initial state
                awaitItem()

                // When
                viewModel.dispatchIntent(PetDetailsIntent.LoadPet("invalid-id"))

                // Then - loading state
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                // Then - error state
                testDispatcher.scheduler.advanceUntilIdle()
                val error = awaitItem()
                assertFalse(error.isLoading)
                assertNull(error.pet)
                assertEquals(errorMessage, error.error)
            }
        }

    @Test
    fun `NavigateBack should emit NavigateBack effect`() =
        runTest {
            // Given
            viewModel.effects.test {
                // When
                viewModel.dispatchIntent(PetDetailsIntent.NavigateBack)
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is PetDetailsEffect.NavigateBack)
            }
        }

    @Test
    fun `ShowOnMap should emit ShowMap effect when location has coordinates`() =
        runTest {
            // Given
            val pet = MockAnimalData.generateMockAnimals().first()
            fakeRepository.result = pet

            // Load pet first
            viewModel.dispatchIntent(PetDetailsIntent.LoadPet(pet.id))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.effects.test {
                // When
                viewModel.dispatchIntent(PetDetailsIntent.ShowOnMap)
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is PetDetailsEffect.ShowMap)
            }
        }

    @Test
    fun `RetryLoad should reload pet with stored ID`() =
        runTest {
            // Given
            val pet = MockAnimalData.generateMockAnimals().first()
            fakeRepository.result = pet

            // Load pet first
            viewModel.dispatchIntent(PetDetailsIntent.LoadPet(pet.id))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                // Skip to current state
                val current = awaitItem()
                assertEquals(pet.id, current.pet?.id)

                // When
                viewModel.dispatchIntent(PetDetailsIntent.RetryLoad)

                // Then - should reload
                val loading = awaitItem()
                assertTrue(loading.isLoading)

                testDispatcher.scheduler.advanceUntilIdle()
                val reloaded = awaitItem()
                assertEquals(pet.id, reloaded.pet?.id)
            }
        }

    /**
     * Fake repository for testing.
     */
    private class FakeAnimalRepository : com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository {
        var result: Animal? = null
        var error: Throwable? = null

        override suspend fun getAnimals(): List<Animal> = emptyList()

        override suspend fun getAnimalById(id: String): Animal {
            error?.let { throw it }
            return result ?: throw NoSuchElementException("No result set")
        }
    }
}
