package com.intive.aifirst.petspot.features.reportmissing.domain.usecases

import com.intive.aifirst.petspot.features.reportmissing.domain.models.AnimalGender
import com.intive.aifirst.petspot.features.reportmissing.domain.models.CreatedAnnouncement
import com.intive.aifirst.petspot.features.reportmissing.fakes.FakeAnnouncementRepository
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.FlowData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [SubmitAnnouncementUseCase].
 * Tests the 2-step submission flow: create announcement, then upload photo.
 */
class SubmitAnnouncementUseCaseTest {
    private lateinit var repository: FakeAnnouncementRepository
    private lateinit var useCase: SubmitAnnouncementUseCase

    @BeforeEach
    fun setup() {
        repository = FakeAnnouncementRepository()
        useCase = SubmitAnnouncementUseCase(repository)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Success Path Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `invoke should return success with managementPassword when both steps succeed`() = runTest {
        // Given
        val flowData = createValidFlowData()
        repository.createAnnouncementResult = Result.success(
            CreatedAnnouncement(id = "uuid-123", managementPassword = "654321"),
        )

        // When
        val result = useCase(flowData)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("654321", result.getOrNull())
        assertTrue(repository.createAnnouncementCalled)
        assertTrue(repository.uploadPhotoCalled)
    }

    @Test
    fun `invoke should pass correct data to createAnnouncement`() = runTest {
        // Given
        val flowData = createValidFlowData()

        // When
        useCase(flowData)

        // Then
        val request = repository.lastCreateRequest
        assertNotNull(request)
        assertEquals("DOG", request.species)
        assertEquals("MALE", request.sex)
        assertEquals("owner@example.com", request.email)
        assertEquals("+48123456789", request.phone)
        assertEquals("MISSING", request.status)
        assertEquals(52.2297, request.locationLatitude)
        assertEquals(21.0122, request.locationLongitude)
    }

    @Test
    fun `invoke should pass correct data to uploadPhoto`() = runTest {
        // Given
        val flowData = createValidFlowData()
        repository.createAnnouncementResult = Result.success(
            CreatedAnnouncement(id = "uuid-abc", managementPassword = "999888"),
        )

        // When
        useCase(flowData)

        // Then
        assertEquals("uuid-abc", repository.lastUploadAnnouncementId)
        assertEquals("999888", repository.lastUploadPassword)
        assertEquals("content://photo/1", repository.lastUploadPhotoUri)
    }

    @Test
    fun `invoke should skip photo upload when photoUri is null`() = runTest {
        // Given
        val flowData = createValidFlowData().copy(photoUri = null)

        // When
        val result = useCase(flowData)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(repository.createAnnouncementCalled)
        assertFalse(repository.uploadPhotoCalled)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Step 1 Failure Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `invoke should return failure when createAnnouncement fails`() = runTest {
        // Given
        val flowData = createValidFlowData()
        repository.createAnnouncementResult = Result.failure(Exception("Network error"))

        // When
        val result = useCase(flowData)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        assertTrue(repository.createAnnouncementCalled)
        assertFalse(repository.uploadPhotoCalled) // Should not proceed to step 2
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Step 2 Failure Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `invoke should return failure when uploadPhoto fails`() = runTest {
        // Given
        val flowData = createValidFlowData()
        repository.uploadPhotoResult = Result.failure(Exception("Upload failed"))

        // When
        val result = useCase(flowData)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Upload failed", result.exceptionOrNull()?.message)
        assertTrue(repository.createAnnouncementCalled)
        assertTrue(repository.uploadPhotoCalled)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Edge Cases
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `invoke should handle optional fields correctly`() = runTest {
        // Given
        val flowData = createValidFlowData().copy(
            chipNumber = "",
            additionalDescription = "",
            rewardDescription = "",
        )

        // When
        useCase(flowData)

        // Then
        val request = repository.lastCreateRequest
        assertNotNull(request)
        assertEquals(null, request.microchipNumber)
        assertEquals(null, request.description)
        assertEquals(null, request.reward)
    }

    @Test
    fun `invoke should include optional fields when provided`() = runTest {
        // Given
        val flowData = createValidFlowData().copy(
            chipNumber = "123456789012345",
            additionalDescription = "Brown fur, white spot",
            rewardDescription = "$250 reward",
        )

        // When
        useCase(flowData)

        // Then
        val request = repository.lastCreateRequest
        assertNotNull(request)
        assertEquals("123456789012345", request.microchipNumber)
        assertEquals("Brown fur, white spot", request.description)
        assertEquals("$250 reward", request.reward)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Test Helpers
    // ═══════════════════════════════════════════════════════════════════════════

    private fun createValidFlowData(): FlowData = FlowData(
        // Step 1
        chipNumber = "123456789012345",
        // Step 2
        photoUri = "content://photo/1",
        // Step 3
        animalSpecies = "Dog",
        animalRace = "Labrador",
        animalGender = AnimalGender.MALE,
        animalAge = 3,
        disappearanceDate = LocalDate.of(2024, 1, 15),
        latitude = 52.2297,
        longitude = 21.0122,
        additionalDescription = "Friendly dog",
        // Step 4
        contactEmail = "owner@example.com",
        contactPhone = "+48123456789",
        rewardDescription = "$100 reward",
    )
}

