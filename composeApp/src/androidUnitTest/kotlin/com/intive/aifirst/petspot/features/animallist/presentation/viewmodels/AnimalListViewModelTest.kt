package com.intive.aifirst.petspot.features.animallist.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.composeapp.domain.repositories.FakeAnimalRepository
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalsUseCase
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for AnimalListViewModel.
 * Tests intent handling, state transitions, and effect emissions using Turbine.
 * Follows Given-When-Then structure per project constitution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AnimalListViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `dispatchIntent Refresh should emit loading then success state`() = runTest {
        // Given - ViewModel with fake repository returning 5 animals
        val fakeRepository = FakeAnimalRepository(animalCount = 5)
        val useCase = GetAnimalsUseCase(fakeRepository)
        val viewModel = AnimalListViewModel(useCase)
        
        // When - observing state changes
        viewModel.state.test {
            // First emission: Initial state (from init which calls Refresh)
            val initialState = awaitItem()
            
            // Advance coroutine to process Refresh intent
            advanceUntilIdle()
            
            // Then - should emit loading state then success state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading, "Should be loading")
            
            val successState = awaitItem()
            assertFalse(successState.isLoading, "Should not be loading")
            assertEquals(5, successState.animals.size, "Should have 5 animals")
            assertNull(successState.error, "Should have no error")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `dispatchIntent Refresh should emit loading then error state when repository fails`() = runTest {
        // Given - ViewModel with fake repository configured to fail
        val fakeRepository = FakeAnimalRepository(
            animalCount = 0,
            shouldFail = true,
            exception = Exception("Network error")
        )
        val useCase = GetAnimalsUseCase(fakeRepository)
        val viewModel = AnimalListViewModel(useCase)
        
        // When - observing state changes
        viewModel.state.test {
            // Initial state emitted immediately
            awaitItem()
            
            // Advance coroutine to process Refresh intent
            advanceUntilIdle()
            
            // Then - should emit loading state then error state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading, "Should be loading")
            
            val errorState = awaitItem()
            assertFalse(errorState.isLoading, "Should not be loading after failure")
            assertEquals("Network error", errorState.error, "Should expose error message from failure")
            assertTrue(errorState.animals.isEmpty(), "Should not update animals on failure")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `dispatchIntent SelectAnimal should emit NavigateToDetails effect`() = runTest {
        // Given - ViewModel with fake repository
        val fakeRepository = FakeAnimalRepository(animalCount = 3)
        val useCase = GetAnimalsUseCase(fakeRepository)
        val viewModel = AnimalListViewModel(useCase)
        
        advanceUntilIdle() // Let init complete
        
        // When - SelectAnimal intent is dispatched
        viewModel.effects.test {
            viewModel.dispatchIntent(AnimalListIntent.SelectAnimal("animal-123"))
            
            // Then - NavigateToDetails effect is emitted
            val effect = awaitItem()
            assertTrue(effect is AnimalListEffect.NavigateToDetails, "Should emit NavigateToDetails effect")
            assertEquals("animal-123", (effect as AnimalListEffect.NavigateToDetails).animalId, "Should have correct animal ID")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `dispatchIntent ReportMissing should emit NavigateToReportMissing effect`() = runTest {
        // Given - ViewModel with fake repository
        val fakeRepository = FakeAnimalRepository(animalCount = 3)
        val useCase = GetAnimalsUseCase(fakeRepository)
        val viewModel = AnimalListViewModel(useCase)
        
        advanceUntilIdle() // Let init complete
        
        // When - ReportMissing intent is dispatched
        viewModel.effects.test {
            viewModel.dispatchIntent(AnimalListIntent.ReportMissing)
            
            // Then - NavigateToReportMissing effect is emitted
            val effect = awaitItem()
            assertTrue(effect is AnimalListEffect.NavigateToReportMissing, "Should emit NavigateToReportMissing effect")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `dispatchIntent ReportFound should emit NavigateToReportFound effect`() = runTest {
        // Given - ViewModel with fake repository
        val fakeRepository = FakeAnimalRepository(animalCount = 3)
        val useCase = GetAnimalsUseCase(fakeRepository)
        val viewModel = AnimalListViewModel(useCase)
        
        advanceUntilIdle() // Let init complete
        
        // When - ReportFound intent is dispatched
        viewModel.effects.test {
            viewModel.dispatchIntent(AnimalListIntent.ReportFound)
            
            // Then - NavigateToReportFound effect is emitted
            val effect = awaitItem()
            assertTrue(effect is AnimalListEffect.NavigateToReportFound, "Should emit NavigateToReportFound effect")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}

