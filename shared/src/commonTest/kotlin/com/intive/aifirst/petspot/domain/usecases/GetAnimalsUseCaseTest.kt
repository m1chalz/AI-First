package com.intive.aifirst.petspot.domain.usecases

import com.intive.aifirst.petspot.domain.repositories.FakeAnimalRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for GetAnimalsUseCase.
 * Tests all scenarios: success, failure, and empty list.
 * Follows Given-When-Then structure per project constitution.
 */
class GetAnimalsUseCaseTest {
    
    @Test
    fun `should return success with animals when repository returns data`() = runTest {
        // Given - fake repository with 16 mock animals
        val fakeRepository = FakeAnimalRepository(animalCount = 16, shouldFail = false)
        val useCase = GetAnimalsUseCase(fakeRepository)
        
        // When - use case is invoked
        val animals = useCase()
        
        // Then - returns 16 animals list
        assertEquals(16, animals.size, "Should return 16 animals")
        assertEquals(1, fakeRepository.getAnimalsCallCount, "Repository should be called once")
    }
    
    @Test
    fun `should return failure when repository throws exception`() = runTest {
        // Given - failing fake repository
        val expectedException = Exception("Network error")
        val fakeRepository = FakeAnimalRepository(
            shouldFail = true,
            exception = expectedException
        )
        val useCase = GetAnimalsUseCase(fakeRepository)
        
        // When - use case is invoked
        val exception = assertFailsWith<Exception> {
            useCase()
        }
        
        // Then - exception message matches
        assertEquals("Network error", exception.message, "Exception message should match")
        assertEquals(1, fakeRepository.getAnimalsCallCount, "Repository should be called once")
    }
    
    @Test
    fun `should return empty list when repository returns no animals`() = runTest {
        // Given - fake repository with 0 animals
        val fakeRepository = FakeAnimalRepository(animalCount = 0, shouldFail = false)
        val useCase = GetAnimalsUseCase(fakeRepository)
        
        // When - use case is invoked
        val animals = useCase()
        
        // Then - returns empty list
        assertTrue(animals.isEmpty(), "Animals list should be empty")
        assertEquals(1, fakeRepository.getAnimalsCallCount, "Repository should be called once")
    }
}

