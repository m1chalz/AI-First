package com.intive.aifirst.petspot.data

import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.data.api.AnnouncementApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnimalRepositoryImplTest {
    private val baseUrl = "http://localhost:3000"

    // region getAnimals - Success scenarios

    @Test
    fun `getAnimals should return list of animals when API returns success`() =
        runTest {
            // Given
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content =
                            """
                            {
                                "data": [
                                    {
                                        "id": "1",
                                        "petName": "Max",
                                        "species": "Golden Retriever",
                                        "breed": "Purebred",
                                        "sex": "MALE",
                                        "age": 5,
                                        "description": "Friendly dog",
                                        "microchipNumber": "123456789",
                                        "locationLatitude": 52.2297,
                                        "locationLongitude": 21.0122,
                                        "lastSeenDate": "2025-11-20",
                                        "email": "owner@example.com",
                                        "phone": "+48 123 456 789",
                                        "photoUrl": "https://example.com/photo.jpg",
                                        "status": "MISSING",
                                        "reward": "500 PLN",
                                        "createdAt": "2025-11-24T12:00:00Z",
                                        "updatedAt": "2025-11-24T12:00:00Z"
                                    },
                                    {
                                        "id": "2",
                                        "petName": "Luna",
                                        "species": "Cat",
                                        "sex": "FEMALE",
                                        "lastSeenDate": "2025-11-21",
                                        "photoUrl": "https://example.com/cat.jpg",
                                        "status": "FOUND",
                                        "createdAt": "2025-11-24T12:00:00Z",
                                        "updatedAt": "2025-11-24T12:00:00Z"
                                    }
                                ]
                            }
                            """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val repository = createRepository(mockEngine)

            // When
            val result = repository.getAnimals()

            // Then
            assertEquals(2, result.size)

            val firstAnimal = result[0]
            assertEquals("1", firstAnimal.id)
            assertEquals("Max", firstAnimal.name)
            assertEquals("Golden Retriever", firstAnimal.species)
            assertEquals(AnimalGender.MALE, firstAnimal.gender)
            assertEquals(AnimalStatus.MISSING, firstAnimal.status)
            assertEquals(52.2297, firstAnimal.location.latitude)
            assertEquals(21.0122, firstAnimal.location.longitude)
            assertEquals(5, firstAnimal.age)
            assertEquals("500 PLN", firstAnimal.rewardAmount)

            val secondAnimal = result[1]
            assertEquals("2", secondAnimal.id)
            assertEquals("Luna", secondAnimal.name)
            assertEquals(AnimalGender.FEMALE, secondAnimal.gender)
            assertEquals(AnimalStatus.FOUND, secondAnimal.status)
        }

    @Test
    fun `getAnimals should return empty list when API returns empty data array`() =
        runTest {
            // Given
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content = """{"data": []}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val repository = createRepository(mockEngine)

            // When
            val result = repository.getAnimals()

            // Then
            assertTrue(result.isEmpty())
        }

    // endregion

    // region getAnimals - Error scenarios

    @Test
    fun `getAnimals should throw exception when API returns server error`() =
        runTest {
            // Given
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content = """{"error": "Internal Server Error"}""",
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val repository = createRepository(mockEngine)

            // When/Then
            assertThrows<Exception> {
                repository.getAnimals()
            }
        }

    @Test
    fun `getAnimals should throw exception when API returns client error`() =
        runTest {
            // Given
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content = """{"error": "Bad Request"}""",
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val repository = createRepository(mockEngine)

            // When/Then
            assertThrows<Exception> {
                repository.getAnimals()
            }
        }

    @Test
    fun `getAnimals should throw exception when network is unavailable`() =
        runTest {
            // Given
            val mockEngine =
                MockEngine { _ ->
                    throw IOException("Network unavailable")
                }
            val repository = createRepository(mockEngine)

            // When/Then
            assertThrows<IOException> {
                repository.getAnimals()
            }
        }

    // endregion

    // region getAnimalById - Success scenarios

    @Test
    fun `getAnimalById should return animal when API returns announcement by ID`() =
        runTest {
            // Given
            val animalId = "550e8400-e29b-41d4-a716-446655440000"
            val mockEngine =
                MockEngine { request ->
                    assertEquals("$baseUrl/api/v1/announcements/$animalId", request.url.toString())
                    respond(
                        content =
                            """
                            {
                                "id": "$animalId",
                                "petName": "Buddy",
                                "species": "Golden Retriever",
                                "breed": "Purebred",
                                "sex": "MALE",
                                "age": 3,
                                "description": "Very friendly golden retriever",
                                "microchipNumber": "123456789012345",
                                "locationLatitude": 52.2297,
                                "locationLongitude": 21.0122,
                                "lastSeenDate": "2025-11-25",
                                "email": "owner@example.com",
                                "phone": "+48 123 456 789",
                                "photoUrl": "https://example.com/buddy.jpg",
                                "status": "MISSING",
                                "reward": "1000 PLN",
                                "createdAt": "2025-11-24T12:00:00Z",
                                "updatedAt": "2025-11-24T12:00:00Z"
                            }
                            """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val repository = createRepository(mockEngine)

            // When
            val result = repository.getAnimalById(animalId)

            // Then
            assertEquals(animalId, result.id)
            assertEquals("Buddy", result.name)
            assertEquals("Golden Retriever", result.species)
            assertEquals("Purebred", result.breed)
            assertEquals(AnimalGender.MALE, result.gender)
            assertEquals(AnimalStatus.MISSING, result.status)
            assertEquals(3, result.age)
            assertEquals("Very friendly golden retriever", result.description)
            assertEquals("123456789012345", result.microchipNumber)
            assertEquals(52.2297, result.location.latitude)
            assertEquals(21.0122, result.location.longitude)
            assertEquals("2025-11-25", result.lastSeenDate)
            assertEquals("owner@example.com", result.email)
            assertEquals("+48 123 456 789", result.phone)
            assertEquals("https://example.com/buddy.jpg", result.photoUrl)
            assertEquals("1000 PLN", result.rewardAmount)
        }

    // endregion

    // region getAnimalById - Error scenarios

    @Test
    fun `getAnimalById should throw exception when API returns 404 for unknown ID`() =
        runTest {
            // Given
            val unknownId = "unknown-id-12345"
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content = """{"error": "Announcement not found"}""",
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val repository = createRepository(mockEngine)

            // When/Then
            assertThrows<Exception> {
                repository.getAnimalById(unknownId)
            }
        }

    @Test
    fun `getAnimalById should throw exception when API returns 5xx server error`() =
        runTest {
            // Given
            val animalId = "some-id"
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content = """{"error": "Internal Server Error"}""",
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val repository = createRepository(mockEngine)

            // When/Then
            assertThrows<Exception> {
                repository.getAnimalById(animalId)
            }
        }

    @Test
    fun `getAnimalById should throw exception when network is unavailable`() =
        runTest {
            // Given
            val animalId = "some-id"
            val mockEngine =
                MockEngine { _ ->
                    throw IOException("Network unavailable")
                }
            val repository = createRepository(mockEngine)

            // When/Then
            assertThrows<IOException> {
                repository.getAnimalById(animalId)
            }
        }

    // endregion

    // region Test helpers

    private fun createRepository(mockEngine: MockEngine): AnimalRepositoryImpl {
        val httpClient =
            HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                    )
                }
            }
        val apiClient = AnnouncementApiClient(httpClient, baseUrl)
        return AnimalRepositoryImpl(apiClient, baseUrl)
    }

    // endregion
}
