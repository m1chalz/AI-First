package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import app.cash.turbine.test
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for [OwnerDetailsViewModel].
 * Tests validation logic for phone and email fields (US2).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OwnerDetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var flowState: ReportMissingFlowState
    private lateinit var viewModel: OwnerDetailsViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        flowState = ReportMissingFlowState()
        viewModel = OwnerDetailsViewModel(flowState)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Phone Update Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `UpdatePhone should update state and clear error`() = runTest {
        // Given
        val phone = "+48123456789"

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone(phone))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(phone, viewModel.state.value.phone)
        assertNull(viewModel.state.value.phoneError)
    }

    @Test
    fun `UpdatePhone should sync with flow state`() = runTest {
        // Given
        val phone = "+48123456789"

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone(phone))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(phone, flowState.data.value.contactPhone)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Email Update Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `UpdateEmail should update state and clear error`() = runTest {
        // Given
        val email = "owner@example.com"

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail(email))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(email, viewModel.state.value.email)
        assertNull(viewModel.state.value.emailError)
    }

    @Test
    fun `UpdateEmail should sync with flow state`() = runTest {
        // Given
        val email = "owner@example.com"

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail(email))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(email, flowState.data.value.contactEmail)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Reward Update Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `UpdateReward should update state`() = runTest {
        // Given
        val reward = "$250 gift card"

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateReward(reward))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(reward, viewModel.state.value.reward)
    }

    @Test
    fun `UpdateReward should truncate at 120 characters`() = runTest {
        // Given
        val longReward = "A".repeat(150)

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateReward(longReward))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(120, viewModel.state.value.reward.length)
    }

    @Test
    fun `UpdateReward should sync with flow state`() = runTest {
        // Given
        val reward = "$250 gift card"

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateReward(reward))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(reward, flowState.data.value.rewardDescription)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Validation Tests (US2)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `ContinueClicked with too few digits should set phoneError`() = runTest {
        // Given
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone("123"))
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail("owner@example.com"))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.state.value.phoneError)
        assertEquals("Enter at least 7 digits", viewModel.state.value.phoneError)
    }

    @Test
    fun `ContinueClicked with too many digits should set phoneError`() = runTest {
        // Given
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone("123456789012345"))
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail("owner@example.com"))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.state.value.phoneError)
        assertEquals("Enter no more than 11 digits", viewModel.state.value.phoneError)
    }

    @Test
    fun `ContinueClicked with invalid email should set emailError`() = runTest {
        // Given
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone("+48123456789"))
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail("owner@"))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.state.value.emailError)
        assertEquals("Enter a valid email address", viewModel.state.value.emailError)
    }

    @Test
    fun `ContinueClicked with both invalid should set both errors`() = runTest {
        // Given
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone("123"))
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail("invalid"))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.state.value.phoneError)
        assertNotNull(viewModel.state.value.emailError)
    }

    @Test
    fun `ContinueClicked with valid inputs should not set errors`() = runTest {
        // Given
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone("+48123456789"))
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail("owner@example.com"))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.phoneError)
        assertNull(viewModel.state.value.emailError)
    }

    @Test
    fun `ContinueClicked with invalid inputs should emit ShowSnackbar effect`() = runTest {
        // Given
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone("123"))
        viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail("owner@example.com"))
        testDispatcher.scheduler.advanceUntilIdle()

        // When/Then
        viewModel.effects.test {
            viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assert(effect is OwnerDetailsUiEffect.ShowSnackbar)
            assertEquals("Please fix the errors above", (effect as OwnerDetailsUiEffect.ShowSnackbar).message)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Navigation Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `BackClicked should emit NavigateBack effect`() = runTest {
        // When/Then
        viewModel.effects.test {
            viewModel.dispatchIntent(OwnerDetailsUserIntent.BackClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assert(effect is OwnerDetailsUiEffect.NavigateBack)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // State Persistence Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `ViewModel should initialize from flow state`() = runTest {
        // Given
        flowState.updateContactPhone("+48111222333")
        flowState.updateContactEmail("test@test.com")
        flowState.updateRewardDescription("$100")

        // When
        val newViewModel = OwnerDetailsViewModel(flowState)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("+48111222333", newViewModel.state.value.phone)
        assertEquals("test@test.com", newViewModel.state.value.email)
        assertEquals("$100", newViewModel.state.value.reward)
    }
}

