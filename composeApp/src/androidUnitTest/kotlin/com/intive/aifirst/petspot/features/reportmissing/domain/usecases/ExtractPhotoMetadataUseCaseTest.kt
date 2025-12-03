package com.intive.aifirst.petspot.features.reportmissing.domain.usecases

import com.intive.aifirst.petspot.features.reportmissing.domain.repositories.PhotoMetadataRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for ExtractPhotoMetadataUseCase.
 * Verifies that the use case correctly delegates to the repository.
 */
class ExtractPhotoMetadataUseCaseTest {
    private lateinit var useCase: ExtractPhotoMetadataUseCase
    private lateinit var fakeRepository: FakePhotoMetadataRepository

    @BeforeEach
    fun setup() {
        fakeRepository = FakePhotoMetadataRepository()
        useCase = ExtractPhotoMetadataUseCase(fakeRepository)
    }

    @Test
    fun `invoke should return metadata from repository`() =
        runTest {
            // Given
            val uri = "content://photo/test"
            fakeRepository.setResponse(uri, "test.jpg", 1024L)

            // When
            val result = useCase(uri)

            // Then
            assertEquals("test.jpg", result.first)
            assertEquals(1024L, result.second)
        }

    @Test
    fun `invoke should propagate exception from repository`() =
        runTest {
            // Given
            val uri = "content://photo/error"
            fakeRepository.setError(uri, IllegalStateException("Metadata extraction failed"))

            // When/Then
            assertFailsWith<IllegalStateException> {
                useCase(uri)
            }
        }

    @Test
    fun `invoke should handle large file sizes`() =
        runTest {
            // Given - 10MB file
            val uri = "content://photo/large"
            fakeRepository.setResponse(uri, "large_photo.jpg", 10_485_760L)

            // When
            val result = useCase(uri)

            // Then
            assertEquals("large_photo.jpg", result.first)
            assertEquals(10_485_760L, result.second)
        }

    /** Configurable fake repository for testing */
    private class FakePhotoMetadataRepository : PhotoMetadataRepository {
        private val responses = mutableMapOf<String, Pair<String, Long>>()
        private val errors = mutableMapOf<String, Exception>()

        fun setResponse(
            uri: String,
            filename: String,
            sizeBytes: Long,
        ) {
            responses[uri] = Pair(filename, sizeBytes)
        }

        fun setError(
            uri: String,
            exception: Exception,
        ) {
            errors[uri] = exception
        }

        override suspend fun extractMetadata(uri: String): Pair<String, Long> {
            errors[uri]?.let { throw it }
            return responses[uri] ?: throw IllegalStateException("No response configured for $uri")
        }
    }
}
