package com.intive.aifirst.petspot.domain.usecases

import com.intive.aifirst.petspot.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.domain.models.Animal
import com.intive.aifirst.petspot.domain.repositories.AnimalRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for GetAnimalByIdUseCase.
 * Follows Given-When-Then pattern.
 */
class GetAnimalByIdUseCaseTest {
    
    private lateinit var useCase: GetAnimalByIdUseCase
    private lateinit var repository: FakeAnimalRepository
    
    @BeforeEach
    fun setUp() {
        repository = FakeAnimalRepository()
        useCase = GetAnimalByIdUseCase(repository)
    }
    
    @Test
    fun `should return animal when repository finds it`() = runTest {
        // Given
        val expectedAnimal = MockAnimalData.generateMockAnimals().first()
        repository.setAnimals(MockAnimalData.generateMockAnimals())
        
        // When
        val result = useCase(expectedAnimal.id)
        
        // Then
        assertEquals(expectedAnimal.id, result.id)
        assertEquals(expectedAnimal.name, result.name)
    }
    
    @Test
    fun `should throw NoSuchElementException when animal not found`() = runTest {
        // Given
        repository.setAnimals(emptyList())
        
        // When & Then
        assertThrows(NoSuchElementException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase("non-existent-id")
            }
        }
    }
    
    @Test
    fun `should propagate repository exception`() = runTest {
        // Given
        repository.shouldThrowException = true
        
        // When & Then
        assertThrows(RuntimeException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase("any-id")
            }
        }
    }
    
    /**
     * Fake repository for testing.
     */
    private class FakeAnimalRepository : AnimalRepository {
        private var animals: List<Animal> = emptyList()
        var shouldThrowException = false
        
        fun setAnimals(animals: List<Animal>) {
            this.animals = animals
        }
        
        override suspend fun getAnimals(): List<Animal> {
            if (shouldThrowException) throw RuntimeException("Test exception")
            return animals
        }
        
        override suspend fun getAnimalById(id: String): Animal {
            if (shouldThrowException) throw RuntimeException("Test exception")
            return animals.find { it.id == id }
                ?: throw NoSuchElementException("Animal not found: $id")
        }
    }
}

