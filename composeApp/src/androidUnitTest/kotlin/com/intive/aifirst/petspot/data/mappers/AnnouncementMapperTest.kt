package com.intive.aifirst.petspot.data.mappers

import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.data.api.dto.AnnouncementDto
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnnouncementMapperTest {
    // region toDomain - Full mapping tests

    @Test
    fun `toDomain should map all required fields correctly`() {
        // Given
        val dto = createTestDto()

        // When
        val animal = dto.toDomain()

        // Then
        assertEquals("test-id-123", animal.id)
        assertEquals("https://example.com/photo.jpg", animal.photoUrl)
        assertEquals("2025-11-20", animal.lastSeenDate)
    }

    @Test
    fun `toDomain should map petName to name with Unknown fallback`() {
        // Given
        val dtoWithName = createTestDto(petName = "Buddy")
        val dtoWithNull = createTestDto(petName = null)

        // When
        val animalWithName = dtoWithName.toDomain()
        val animalWithNull = dtoWithNull.toDomain()

        // Then
        assertEquals("Buddy", animalWithName.name)
        assertEquals("Unknown", animalWithNull.name)
    }

    @Test
    fun `toDomain should map optional fields with fallbacks`() {
        // Given
        val dtoWithNulls =
            createTestDto(
                breed = null,
                description = null,
                email = null,
                phone = null,
                microchipNumber = null,
                reward = null,
                age = null,
            )

        // When
        val animal = dtoWithNulls.toDomain()

        // Then
        assertEquals("", animal.breed)
        assertEquals("", animal.description)
        assertNull(animal.email)
        assertNull(animal.phone)
        assertNull(animal.microchipNumber)
        assertNull(animal.rewardAmount)
        assertNull(animal.age)
    }

    @Test
    fun `toDomain should map optional fields when present`() {
        // Given
        val dtoWithValues =
            createTestDto(
                breed = "Golden Retriever",
                description = "Friendly dog",
                email = "owner@example.com",
                phone = "+1 555 123 4567",
                microchipNumber = "123456789012345",
                reward = "500 USD",
                age = 5,
            )

        // When
        val animal = dtoWithValues.toDomain()

        // Then
        assertEquals("Golden Retriever", animal.breed)
        assertEquals("Friendly dog", animal.description)
        assertEquals("owner@example.com", animal.email)
        assertEquals("+1 555 123 4567", animal.phone)
        assertEquals("123456789012345", animal.microchipNumber)
        assertEquals("500 USD", animal.rewardAmount)
        assertEquals(5, animal.age)
    }

    @Test
    fun `toDomain should create Location with coordinates`() {
        // Given
        val dto = createTestDto(locationLatitude = 40.785091, locationLongitude = -73.968285)

        // When
        val animal = dto.toDomain()

        // Then
        assertEquals(40.785091, animal.location.latitude)
        assertEquals(-73.968285, animal.location.longitude)
    }

    @Test
    fun `toDomain should handle null coordinates in Location`() {
        // Given
        val dto = createTestDto(locationLatitude = null, locationLongitude = null)

        // When
        val animal = dto.toDomain()

        // Then
        assertNull(animal.location.latitude)
        assertNull(animal.location.longitude)
    }

    @Test
    fun `toDomain should pass through status enum directly`() {
        // Given
        val dtoMissing = createTestDto(status = AnimalStatus.MISSING)
        val dtoFound = createTestDto(status = AnimalStatus.FOUND)
        val dtoClosed = createTestDto(status = AnimalStatus.CLOSED)

        // When/Then
        assertEquals(AnimalStatus.MISSING, dtoMissing.toDomain().status)
        assertEquals(AnimalStatus.FOUND, dtoFound.toDomain().status)
        assertEquals(AnimalStatus.CLOSED, dtoClosed.toDomain().status)
    }

    @Test
    fun `toDomain should pass through gender enum directly`() {
        // Given
        val dtoMale = createTestDto(sex = AnimalGender.MALE)
        val dtoFemale = createTestDto(sex = AnimalGender.FEMALE)
        val dtoUnknown = createTestDto(sex = AnimalGender.UNKNOWN)

        // When/Then
        assertEquals(AnimalGender.MALE, dtoMale.toDomain().gender)
        assertEquals(AnimalGender.FEMALE, dtoFemale.toDomain().gender)
        assertEquals(AnimalGender.UNKNOWN, dtoUnknown.toDomain().gender)
    }

    @Test
    fun `toDomain should pass through species string directly`() {
        // Given
        val dto = createTestDto(species = "Golden Retriever")

        // When
        val animal = dto.toDomain()

        // Then
        assertEquals("Golden Retriever", animal.species)
    }

    // endregion

    // region Test helpers

    private fun createTestDto(
        id: String = "test-id-123",
        petName: String? = "Test Pet",
        species: String = "Dog",
        breed: String? = "Mixed",
        sex: AnimalGender = AnimalGender.MALE,
        age: Int? = 3,
        description: String? = "A friendly pet",
        microchipNumber: String? = null,
        locationLatitude: Double? = 40.0,
        locationLongitude: Double? = -74.0,
        lastSeenDate: String = "2025-11-20",
        email: String? = "test@example.com",
        phone: String? = "+1 555 000 0000",
        photoUrl: String = "https://example.com/photo.jpg",
        status: AnimalStatus = AnimalStatus.MISSING,
        reward: String? = null,
    ): AnnouncementDto =
        AnnouncementDto(
            id = id,
            petName = petName,
            species = species,
            breed = breed,
            sex = sex,
            age = age,
            description = description,
            microchipNumber = microchipNumber,
            locationLatitude = locationLatitude,
            locationLongitude = locationLongitude,
            lastSeenDate = lastSeenDate,
            email = email,
            phone = phone,
            photoUrl = photoUrl,
            status = status,
            reward = reward,
            createdAt = "2025-11-24T12:34:56.789Z",
            updatedAt = "2025-11-24T12:34:56.789Z",
        )

    // endregion
}
