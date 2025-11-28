package com.intive.aifirst.petspot.features.animallist.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.composeapp.domain.repositories.FakeAnimalRepository
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalsUseCase
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus
import com.intive.aifirst.petspot.domain.models.RationaleDialogType
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.fakes.FakeLocationRepository
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
    fun `dispatchIntent Refresh should emit loading then success state`() =
        runTest {
            // Given - ViewModel with fake repository returning 5 animals
            val fakeRepository = FakeAnimalRepository(animalCount = 5)
            val useCase = GetAnimalsUseCase(fakeRepository)
            val viewModel = AnimalListViewModel(useCase)

            // When - observing state changes
            viewModel.state.test {
                // First emission: Initial state (loading, waiting for permission check)
                val initialState = awaitItem()
                assertTrue(initialState.isLoading, "Initial state should be loading")

                // Dispatch Refresh intent explicitly (simulating UI dispatching after permission flow)
                viewModel.dispatchIntent(AnimalListIntent.Refresh)

                // Advance coroutine to process Refresh intent
                advanceUntilIdle()

                // Then - should emit success state (loading already set, then success)
                val successState = awaitItem()
                assertFalse(successState.isLoading, "Should not be loading")
                assertEquals(5, successState.animals.size, "Should have 5 animals")
                assertNull(successState.error, "Should have no error")

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent Refresh should emit loading then error state when repository fails`() =
        runTest {
            // Given - ViewModel with fake repository configured to fail
            val fakeRepository =
                FakeAnimalRepository(
                    animalCount = 0,
                    shouldFail = true,
                    exception = Exception("Network error"),
                )
            val useCase = GetAnimalsUseCase(fakeRepository)
            val viewModel = AnimalListViewModel(useCase)

            // When - observing state changes
            viewModel.state.test {
                // Initial state (loading, waiting for permission check)
                val initialState = awaitItem()
                assertTrue(initialState.isLoading, "Initial state should be loading")

                // Dispatch Refresh intent explicitly
                viewModel.dispatchIntent(AnimalListIntent.Refresh)

                // Advance coroutine to process Refresh intent
                advanceUntilIdle()

                // Then - should emit error state
                val errorState = awaitItem()
                assertFalse(errorState.isLoading, "Should not be loading after failure")
                assertEquals("Network error", errorState.error, "Should expose error message from failure")
                assertTrue(errorState.animals.isEmpty(), "Should not update animals on failure")

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent SelectAnimal should emit NavigateToDetails effect`() =
        runTest {
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
                assertEquals(
                    "animal-123",
                    (effect as AnimalListEffect.NavigateToDetails).animalId,
                    "Should have correct animal ID",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent ReportMissing should emit NavigateToReportMissing effect`() =
        runTest {
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
                assertTrue(
                    effect is AnimalListEffect.NavigateToReportMissing,
                    "Should emit NavigateToReportMissing effect",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent ReportFound should emit NavigateToReportFound effect`() =
        runTest {
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

    // ========================================
    // User Story 3: Rationale Dialog Effect Tests (US3)
    // ========================================

    @Test
    fun `dispatchIntent PermissionResult denied should emit ShowRationaleDialog when not shown yet`() =
        runTest {
            // Given - ViewModel with denied permission state, rationale not yet shown
            val fakeAnimalRepository = FakeAnimalRepository(animalCount = 3)
            val useCase = GetAnimalsUseCase(fakeAnimalRepository)
            val fakeLocationRepository = FakeLocationRepository()
            val locationUseCase = GetCurrentLocationUseCase(fakeLocationRepository)
            val viewModel = AnimalListViewModel(useCase, locationUseCase)

            advanceUntilIdle() // Let init complete

            // When - permission denied with shouldShowRationale = false (Don't ask again)
            viewModel.effects.test {
                viewModel.dispatchIntent(
                    AnimalListIntent.PermissionResult(
                        granted = false,
                        fineLocation = false,
                        coarseLocation = false,
                        shouldShowRationale = false,
                    ),
                )
                advanceUntilIdle()

                // Then - ShowRationaleDialog with Informational type should be emitted
                val effect = awaitItem()
                assertTrue(
                    effect is AnimalListEffect.ShowRationaleDialog,
                    "Should emit ShowRationaleDialog effect",
                )
                val rationaleEffect = effect as AnimalListEffect.ShowRationaleDialog
                assertTrue(
                    rationaleEffect.type is RationaleDialogType.Informational,
                    "Should show Informational rationale for denied permission without rationale",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent PermissionResult denied should not emit rationale when already shown this session`() =
        runTest {
            // Given - ViewModel with rationale already shown this session
            val fakeAnimalRepository = FakeAnimalRepository(animalCount = 3)
            val useCase = GetAnimalsUseCase(fakeAnimalRepository)
            val fakeLocationRepository = FakeLocationRepository()
            val locationUseCase = GetCurrentLocationUseCase(fakeLocationRepository)
            val viewModel = AnimalListViewModel(useCase, locationUseCase)

            advanceUntilIdle() // Let init complete

            // First denial - shows rationale
            viewModel.dispatchIntent(
                AnimalListIntent.PermissionResult(
                    granted = false,
                    fineLocation = false,
                    coarseLocation = false,
                    shouldShowRationale = false,
                ),
            )
            advanceUntilIdle()

            // Dismiss rationale
            viewModel.dispatchIntent(AnimalListIntent.RationaleDismissed)
            advanceUntilIdle()

            // When - permission denied again in same session
            viewModel.effects.test {
                viewModel.dispatchIntent(
                    AnimalListIntent.PermissionResult(
                        granted = false,
                        fineLocation = false,
                        coarseLocation = false,
                        shouldShowRationale = false,
                    ),
                )
                advanceUntilIdle()

                // Then - No ShowRationaleDialog effect should be emitted (rationale already shown)
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent OpenSettingsRequested should emit OpenSettings effect`() =
        runTest {
            // Given - ViewModel
            val fakeAnimalRepository = FakeAnimalRepository(animalCount = 3)
            val useCase = GetAnimalsUseCase(fakeAnimalRepository)
            val viewModel = AnimalListViewModel(useCase)

            advanceUntilIdle() // Let init complete

            // When - OpenSettingsRequested intent is dispatched
            viewModel.effects.test {
                viewModel.dispatchIntent(AnimalListIntent.OpenSettingsRequested)
                advanceUntilIdle()

                // Then - OpenSettings effect is emitted
                val effect = awaitItem()
                assertTrue(
                    effect is AnimalListEffect.OpenSettings,
                    "Should emit OpenSettings effect",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // User Story 4: Educational Rationale Effect Tests (US4)
    // ========================================

    @Test
    fun `dispatchIntent PermissionResult denied with shouldShowRationale should emit Educational dialog`() =
        runTest {
            // Given - ViewModel
            val fakeAnimalRepository = FakeAnimalRepository(animalCount = 3)
            val useCase = GetAnimalsUseCase(fakeAnimalRepository)
            val fakeLocationRepository = FakeLocationRepository()
            val locationUseCase = GetCurrentLocationUseCase(fakeLocationRepository)
            val viewModel = AnimalListViewModel(useCase, locationUseCase)

            advanceUntilIdle() // Let init complete

            // When - permission denied with shouldShowRationale = true
            viewModel.effects.test {
                viewModel.dispatchIntent(
                    AnimalListIntent.PermissionResult(
                        granted = false,
                        fineLocation = false,
                        coarseLocation = false,
                        shouldShowRationale = true,
                    ),
                )
                advanceUntilIdle()

                // Then - ShowRationaleDialog with Educational type should be emitted
                val effect = awaitItem()
                assertTrue(
                    effect is AnimalListEffect.ShowRationaleDialog,
                    "Should emit ShowRationaleDialog effect",
                )
                val rationaleEffect = effect as AnimalListEffect.ShowRationaleDialog
                assertTrue(
                    rationaleEffect.type is RationaleDialogType.Educational,
                    "Should show Educational rationale when shouldShowRationale is true",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent RationaleContinue should emit RequestPermission effect`() =
        runTest {
            // Given - ViewModel
            val fakeAnimalRepository = FakeAnimalRepository(animalCount = 3)
            val useCase = GetAnimalsUseCase(fakeAnimalRepository)
            val viewModel = AnimalListViewModel(useCase)

            advanceUntilIdle() // Let init complete

            // When - RationaleContinue intent is dispatched (user tapped Continue on educational dialog)
            viewModel.effects.test {
                viewModel.dispatchIntent(AnimalListIntent.RationaleContinue)
                advanceUntilIdle()

                // Then - RequestPermission effect is emitted to trigger system dialog
                val effect = awaitItem()
                assertTrue(
                    effect is AnimalListEffect.RequestPermission,
                    "Should emit RequestPermission effect after Continue",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // User Story 5: Dynamic Permission Change Tests (US5)
    // ========================================

    @Test
    fun `dispatchIntent PermissionStateChanged from denied to granted should fetch location`() =
        runTest {
            // Given - ViewModel with location use case, currently denied
            val fakeAnimalRepository = FakeAnimalRepository(animalCount = 3)
            val useCase = GetAnimalsUseCase(fakeAnimalRepository)
            val fakeLocationRepository =
                FakeLocationRepository(
                    cachedLocation = LocationCoordinates(52.2297, 21.0122),
                )
            val locationUseCase = GetCurrentLocationUseCase(fakeLocationRepository)
            val viewModel = AnimalListViewModel(useCase, locationUseCase)

            advanceUntilIdle() // Let init complete

            // Set initial denied state
            viewModel.dispatchIntent(
                AnimalListIntent.PermissionResult(
                    granted = false,
                    shouldShowRationale = false,
                ),
            )
            advanceUntilIdle()

            // When - permission state changes to granted (user returned from Settings)
            viewModel.dispatchIntent(
                AnimalListIntent.PermissionStateChanged(
                    granted = true,
                    shouldShowRationale = false,
                ),
            )
            advanceUntilIdle()

            // Then - state should show location loading, then location fetched
            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(
                    currentState.permissionStatus is PermissionStatus.Granted,
                    "Permission should be granted",
                )
                assertTrue(
                    currentState.location != null || currentState.isLocationLoading,
                    "Location should be loading or already fetched",
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dispatchIntent PermissionStateChanged from granted to denied should clear location`() =
        runTest {
            // Given - ViewModel with granted permission and location
            val fakeAnimalRepository = FakeAnimalRepository(animalCount = 3)
            val useCase = GetAnimalsUseCase(fakeAnimalRepository)
            val fakeLocationRepository =
                FakeLocationRepository(
                    cachedLocation = LocationCoordinates(52.2297, 21.0122),
                )
            val locationUseCase = GetCurrentLocationUseCase(fakeLocationRepository)
            val viewModel = AnimalListViewModel(useCase, locationUseCase)

            advanceUntilIdle() // Let init complete

            // Set initial granted state with location
            viewModel.dispatchIntent(
                AnimalListIntent.PermissionResult(
                    granted = true,
                    fineLocation = true,
                    coarseLocation = true,
                ),
            )
            advanceUntilIdle()

            // When - permission state changes to denied (user revoked in Settings)
            viewModel.dispatchIntent(
                AnimalListIntent.PermissionStateChanged(
                    granted = false,
                    shouldShowRationale = false,
                ),
            )
            advanceUntilIdle()

            // Then - state should have denied permission and no location
            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(
                    currentState.permissionStatus is PermissionStatus.Denied,
                    "Permission should be denied",
                )
                assertNull(currentState.location, "Location should be cleared when permission revoked")

                cancelAndIgnoreRemainingEvents()
            }
        }
}
