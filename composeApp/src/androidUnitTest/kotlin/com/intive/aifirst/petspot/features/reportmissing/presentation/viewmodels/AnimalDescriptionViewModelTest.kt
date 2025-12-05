package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.fakes.FakeLocationRepository
import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.AnimalDescriptionUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
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
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for AnimalDescriptionViewModel.
 * Tests MVI behavior with hybrid pattern: FlowState + callbacks.
 * Follows Given-When-Then structure per project constitution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AnimalDescriptionViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var flowState: ReportMissingFlowState
    private lateinit var fakeLocationRepository: FakeLocationRepository

    // Track callback invocations
    private var navigateToContactDetailsCalled = false
    private var navigateBackCalled = false

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        flowState = ReportMissingFlowState()
        fakeLocationRepository = FakeLocationRepository(cachedLocation = FakeLocationRepository.SAMPLE_WARSAW)
        navigateToContactDetailsCalled = false
        navigateBackCalled = false
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(locationRepository: FakeLocationRepository = fakeLocationRepository) =
        AnimalDescriptionViewModel(
            flowState = flowState,
            getCurrentLocationUseCase = GetCurrentLocationUseCase(locationRepository),
            onNavigateToContactDetails = { navigateToContactDetailsCalled = true },
            onNavigateBack = { navigateBackCalled = true },
        )

    // ========================================
    // Initial State Tests
    // ========================================

    @Test
    fun `initial state should have default values when flow state is empty`() =
        runTest {
            // Given - empty flow state

            // When
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertNotNull(currentState.disappearanceDate, "Should have default date")
                assertEquals("", currentState.petName)
                assertEquals("", currentState.animalSpecies)
                assertEquals("", currentState.animalRace)
                assertNull(currentState.animalGender)
                assertEquals("", currentState.animalAge)
                assertFalse(currentState.isRaceFieldEnabled, "Race should be disabled initially")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initial state should load values from flow state when available`() =
        runTest {
            // Given - flow state with existing data
            flowState.updateAnimalDescription(
                disappearanceDate = LocalDate.of(2024, 1, 15),
                petName = "Buddy",
                animalSpecies = "Dog",
                animalRace = "Labrador",
                animalGender = AnimalGender.MALE,
                animalAge = 5,
                latitude = null,
                longitude = null,
                additionalDescription = "Brown color",
            )

            // When
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(LocalDate.of(2024, 1, 15), currentState.disappearanceDate)
                assertEquals("Buddy", currentState.petName)
                assertEquals("Dog", currentState.animalSpecies)
                assertEquals("Labrador", currentState.animalRace)
                assertEquals(AnimalGender.MALE, currentState.animalGender)
                assertEquals("5", currentState.animalAge)
                assertEquals("Brown color", currentState.additionalDescription)
                assertTrue(currentState.isRaceFieldEnabled, "Race should be enabled when species set")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdatePetName Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdatePetName should update pet name in state`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdatePetName("Max"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("Max", currentState.petName)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdateSpecies Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdateSpecies should update species and enable race field`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("Dog", currentState.animalSpecies)
                assertTrue(currentState.isRaceFieldEnabled, "Race field should be enabled")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateSpecies should clear race when species changes`() =
        runTest {
            // Given - ViewModel with existing species and race
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Labrador"))
            advanceUntilIdle()

            // When - change species
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Cat"))
            advanceUntilIdle()

            // Then - race should be cleared
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("Cat", currentState.animalSpecies)
                assertEquals("", currentState.animalRace, "Race should be cleared on species change")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateSpecies should NOT clear race when same species selected`() =
        runTest {
            // Given - ViewModel with existing species and race
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Labrador"))
            advanceUntilIdle()

            // When - select same species again
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            advanceUntilIdle()

            // Then - race should NOT be cleared
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("Labrador", currentState.animalRace, "Race should NOT change")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdateRace Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdateRace should update race in state`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Golden Retriever"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("Golden Retriever", currentState.animalRace)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdateGender Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdateGender should update gender in state`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(AnimalGender.FEMALE))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(AnimalGender.FEMALE, currentState.animalGender)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdateAge Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdateAge should accept valid age`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateAge("7"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("7", currentState.animalAge)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateAge should filter non-digits`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When - input with non-digits
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateAge("1a2b3"))
            advanceUntilIdle()

            // Then - should extract digits only
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("123", currentState.animalAge)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdateDescription Intent Tests
    // ========================================

    @Test
    fun `handleIntent UpdateDescription should update description`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateDescription("Brown fur with white spots"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("Brown fur with white spots", currentState.additionalDescription)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateDescription should truncate at max characters`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()
            val longText = "a".repeat(600) // Exceeds 500 char limit

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateDescription(longText))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(500, currentState.additionalDescription.length, "Should truncate to max chars")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // ContinueClicked Intent Tests - Validation Failure
    // ========================================

    @Test
    fun `handleIntent ContinueClicked with invalid form should show validation errors`() =
        runTest {
            // Given - empty required fields
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then - should have validation errors
            viewModel.state.test {
                val currentState = awaitItem()
                assertNotNull(currentState.speciesError, "Should have species error")
                assertNotNull(currentState.genderError, "Should have gender error")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent ContinueClicked with invalid form should emit ShowSnackbar effect`() =
        runTest {
            // Given - empty required fields
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When & Then
            viewModel.effects.test {
                viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked)
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is AnimalDescriptionUiEffect.ShowSnackbar)
                assertEquals("Please correct the errors", (effect as AnimalDescriptionUiEffect.ShowSnackbar).message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent ContinueClicked with invalid form should NOT navigate`() =
        runTest {
            // Given - empty required fields
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then
            assertFalse(navigateToContactDetailsCalled, "Should NOT navigate with invalid form")
        }

    // ========================================
    // ContinueClicked Intent Tests - Success
    // ========================================

    @Test
    fun `handleIntent ContinueClicked with valid form should save to flow state`() =
        runTest {
            // Given - valid form with all required fields including lat/long
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Labrador"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(AnimalGender.MALE))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdatePetName("Buddy"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude("52.2297"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude("21.0122"))
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then - verify flow state was updated
            val savedData = flowState.data.value
            assertEquals("Dog", savedData.animalSpecies)
            assertEquals("Labrador", savedData.animalRace)
            assertEquals(AnimalGender.MALE, savedData.animalGender)
            assertEquals("Buddy", savedData.petName)
            assertEquals(52.2297, savedData.latitude)
            assertEquals(21.0122, savedData.longitude)
        }

    @Test
    fun `handleIntent ContinueClicked with valid form should trigger navigation`() =
        runTest {
            // Given - valid form with all required fields including lat/long
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Labrador"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(AnimalGender.MALE))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude("52.2297"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude("21.0122"))
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then
            assertTrue(navigateToContactDetailsCalled, "Should trigger navigation callback")
        }

    // ========================================
    // BackClicked Intent Tests
    // ========================================

    @Test
    fun `handleIntent BackClicked should save current state to flow`() =
        runTest {
            // Given - partially filled form
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Cat"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdatePetName("Whiskers"))
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.BackClicked)
            advanceUntilIdle()

            // Then - should save to flow state (preserve data on back)
            val savedData = flowState.data.value
            assertEquals("Cat", savedData.animalSpecies)
            assertEquals("Whiskers", savedData.petName)
        }

    @Test
    fun `handleIntent BackClicked should trigger navigation callback`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.BackClicked)
            advanceUntilIdle()

            // Then
            assertTrue(navigateBackCalled, "Should trigger onNavigateBack callback")
            assertFalse(navigateToContactDetailsCalled, "Should NOT trigger forward navigation")
        }

    // ========================================
    // Date Picker Tests
    // ========================================

    @Test
    fun `handleIntent OpenDatePicker should show date picker`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.OpenDatePicker)
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(currentState.isDatePickerVisible, "Date picker should be visible")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent DismissDatePicker should hide date picker`() =
        runTest {
            // Given - date picker open
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.OpenDatePicker)
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.DismissDatePicker)
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertFalse(currentState.isDatePickerVisible, "Date picker should be hidden")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateDate should update date and close picker`() =
        runTest {
            // Given - date picker open
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.OpenDatePicker)
            advanceUntilIdle()
            val newDate = LocalDate.of(2024, 6, 15)

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateDate(newDate))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(newDate, currentState.disappearanceDate)
                assertFalse(currentState.isDatePickerVisible, "Date picker should close after selection")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // Clearing Validation Errors Tests
    // ========================================

    @Test
    fun `handleIntent UpdateSpecies should clear species error`() =
        runTest {
            // Given - ViewModel with validation error
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked) // Trigger validation
            advanceUntilIdle()

            // Verify error exists
            assertTrue(viewModel.state.value.speciesError != null)

            // When - update species
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            advanceUntilIdle()

            // Then - error should be cleared
            viewModel.state.test {
                val currentState = awaitItem()
                assertNull(currentState.speciesError, "Species error should be cleared")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateGender should clear gender error`() =
        runTest {
            // Given - ViewModel with validation error
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked) // Trigger validation
            advanceUntilIdle()

            // Verify error exists
            assertTrue(viewModel.state.value.genderError != null)

            // When - update gender
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(AnimalGender.FEMALE))
            advanceUntilIdle()

            // Then - error should be cleared
            viewModel.state.test {
                val currentState = awaitItem()
                assertNull(currentState.genderError, "Gender error should be cleared")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // GPS Request Tests (Phase 4 - US2)
    // ========================================

    @Test
    fun `handleIntent RequestGpsPosition should set loading state`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.RequestGpsPosition)

            // Then - should be in loading state (before coroutine completes)
            assertTrue(viewModel.state.value.isGpsLoading, "Should be loading during GPS request")
            advanceUntilIdle()
        }

    @Test
    fun `handleIntent RequestGpsPosition success should populate coordinates with 5 decimal places`() =
        runTest {
            // Given - repository returns Warsaw coordinates
            val repository = FakeLocationRepository(cachedLocation = FakeLocationRepository.SAMPLE_WARSAW)
            val viewModel = createViewModel(locationRepository = repository)
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.RequestGpsPosition)
            advanceUntilIdle()

            // Then - coordinates should be populated with 5 decimal places per FR-009
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("52.22970", currentState.latitude)
                assertEquals("21.01220", currentState.longitude)
                assertFalse(currentState.isGpsLoading, "Should not be loading after completion")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent RequestGpsPosition with permission denied should emit ShowSnackbar`() =
        runTest {
            // Given - repository throws SecurityException
            val repository = FakeLocationRepository(shouldFailWithSecurityException = true)
            val viewModel = createViewModel(locationRepository = repository)
            advanceUntilIdle()

            // When & Then
            viewModel.effects.test {
                viewModel.handleIntent(AnimalDescriptionUserIntent.RequestGpsPosition)
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is AnimalDescriptionUiEffect.ShowSnackbar)
                assertTrue(
                    (effect as AnimalDescriptionUiEffect.ShowSnackbar).message.contains(
                        "permission",
                        ignoreCase = true,
                    ),
                    "Message should mention permission",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent RequestGpsPosition with permission denied should emit OpenLocationSettings`() =
        runTest {
            // Given - repository throws SecurityException
            val repository = FakeLocationRepository(shouldFailWithSecurityException = true)
            val viewModel = createViewModel(locationRepository = repository)
            advanceUntilIdle()

            // When & Then
            viewModel.effects.test {
                viewModel.handleIntent(AnimalDescriptionUserIntent.RequestGpsPosition)
                advanceUntilIdle()

                // First effect is Snackbar, second is OpenLocationSettings
                val snackbarEffect = awaitItem()
                assertTrue(snackbarEffect is AnimalDescriptionUiEffect.ShowSnackbar)

                val settingsEffect = awaitItem()
                assertTrue(
                    settingsEffect is AnimalDescriptionUiEffect.OpenLocationSettings,
                    "Should emit OpenLocationSettings effect",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent RequestGpsPosition with no location available should show error`() =
        runTest {
            // Given - repository returns null (no location available)
            val repository = FakeLocationRepository(cachedLocation = null, freshLocation = null)
            val viewModel = createViewModel(locationRepository = repository)
            advanceUntilIdle()

            // When & Then
            viewModel.effects.test {
                viewModel.handleIntent(AnimalDescriptionUserIntent.RequestGpsPosition)
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is AnimalDescriptionUiEffect.ShowSnackbar)
                assertTrue(
                    effect.message.contains("unavailable", ignoreCase = true) ||
                        effect.message.contains("not available", ignoreCase = true) ||
                        effect.message.contains("could not", ignoreCase = true),
                    "Message should indicate location unavailable",
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent RequestGpsPosition should clear loading state on failure`() =
        runTest {
            // Given - repository throws exception
            val repository = FakeLocationRepository(shouldFailWithException = true)
            val viewModel = createViewModel(locationRepository = repository)
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.RequestGpsPosition)
            advanceUntilIdle()

            // Then - loading should be cleared
            viewModel.state.test {
                val currentState = awaitItem()
                assertFalse(currentState.isGpsLoading, "Loading should be cleared after failure")
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========================================
    // UpdateLatitude/Longitude Tests (Phase 4 - US2)
    // ========================================

    @Test
    fun `handleIntent UpdateLatitude should update latitude in state`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude("52.2297"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("52.2297", currentState.latitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateLatitude should limit precision to 5 decimal places`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When - input with more than 5 decimal places
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude("52.12345678"))
            advanceUntilIdle()

            // Then - should be truncated to 5 decimal places
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("52.12345", currentState.latitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateLongitude should update longitude in state`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude("21.0122"))
            advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("21.0122", currentState.longitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateLongitude should limit precision to 5 decimal places`() =
        runTest {
            // Given
            val viewModel = createViewModel()
            advanceUntilIdle()

            // When - input with more than 5 decimal places
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude("-122.4194567890"))
            advanceUntilIdle()

            // Then - should be truncated to 5 decimal places
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("-122.41945", currentState.longitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateLatitude should clear latitude error`() =
        runTest {
            // Given - ViewModel with invalid latitude
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude("invalid"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Labrador"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(AnimalGender.MALE))
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked) // Trigger validation
            advanceUntilIdle()

            // Verify error exists
            assertTrue(viewModel.state.value.latitudeError != null)

            // When - update latitude
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude("52.2297"))
            advanceUntilIdle()

            // Then - error should be cleared
            viewModel.state.test {
                val currentState = awaitItem()
                assertNull(currentState.latitudeError, "Latitude error should be cleared")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent UpdateLongitude should clear longitude error`() =
        runTest {
            // Given - ViewModel with invalid longitude
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude("invalid"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Labrador"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(AnimalGender.MALE))
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked) // Trigger validation
            advanceUntilIdle()

            // Verify error exists
            assertTrue(viewModel.state.value.longitudeError != null)

            // When - update longitude
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude("21.0122"))
            advanceUntilIdle()

            // Then - error should be cleared
            viewModel.state.test {
                val currentState = awaitItem()
                assertNull(currentState.longitudeError, "Longitude error should be cleared")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent ContinueClicked should save coordinates to flow state`() =
        runTest {
            // Given - valid form with coordinates
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateSpecies("Dog"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateRace("Labrador"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateGender(AnimalGender.MALE))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLatitude("52.2297"))
            viewModel.handleIntent(AnimalDescriptionUserIntent.UpdateLongitude("21.0122"))
            advanceUntilIdle()

            // When
            viewModel.handleIntent(AnimalDescriptionUserIntent.ContinueClicked)
            advanceUntilIdle()

            // Then - verify coordinates were saved to flow state
            val savedData = flowState.data.value
            assertEquals(52.2297, savedData.latitude)
            assertEquals(21.0122, savedData.longitude)
        }
}
