package com.intive.aifirst.petspot.domain.fixtures

import com.intive.aifirst.petspot.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.AnimalGender
import com.intive.aifirst.petspot.domain.models.AnimalSpecies
import com.intive.aifirst.petspot.domain.models.AnimalStatus
import com.intive.aifirst.petspot.domain.models.Location

/**
 * Single source of truth for mock animal data across all tests and platform implementations.
 * Provides consistent test data to ensure predictable behavior across Android, iOS, and Web.
 *
 * Use this helper in:
 * - Shared module tests (FakeAnimalRepository)
 * - Android production mock (AnimalRepositoryImpl)
 * - iOS production mock (AnimalRepositoryImpl.swift)
 * - Web production mock (animalRepository.ts)
 */
object MockAnimalData {
    
    /**
     * Generates a list of mock animals for testing and UI development.
     * Returns 16 animals with varied attributes (species, status, locations, contact info).
     *
     * @param count Number of animals to generate (default: 16 for MVP, max: 16)
     * @return List of Animal entities with realistic mock data
     */
    fun generateMockAnimals(count: Int = 16): List<Animal> {
        val allAnimals = listOf(
            Animal(
                id = "1",
                name = "Fluffy",
                photoUrl = "placeholder_cat",
                location = Location("Pruszkow", 5),
                species = AnimalSpecies.CAT,
                breed = "Maine Coon",
                gender = AnimalGender.MALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "18/11/2025",
                description = "Friendly orange tabby cat, last seen near the park.",
                email = "john.doe@example.com",
                phone = "+48 123 456 789"
            ),
            Animal(
                id = "2",
                name = "Rex",
                photoUrl = "placeholder_dog",
                location = Location("Warsaw", 10),
                species = AnimalSpecies.DOG,
                breed = "German Shepherd",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "17/11/2025",
                description = "Large black and tan dog, wearing red collar.",
                email = "anna.smith@example.com",
                phone = null
            ),
            Animal(
                id = "3",
                name = "Bella",
                photoUrl = "placeholder_cat",
                location = Location("Krakow", 3),
                species = AnimalSpecies.CAT,
                breed = "Siamese",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.FOUND,
                lastSeenDate = "19/11/2025",
                description = "Blue-eyed white cat found near train station.",
                email = null,
                phone = "+48 987 654 321"
            ),
            Animal(
                id = "4",
                name = "Buddy",
                photoUrl = "placeholder_dog",
                location = Location("Wroclaw", 7),
                species = AnimalSpecies.DOG,
                breed = "Labrador Retriever",
                gender = AnimalGender.MALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "16/11/2025",
                description = "Yellow lab, very friendly, responds to 'Buddy'.",
                email = "mike@example.com",
                phone = "+48 111 222 333"
            ),
            Animal(
                id = "5",
                name = "Tweety",
                photoUrl = "placeholder_bird",
                location = Location("Gdansk", 15),
                species = AnimalSpecies.BIRD,
                breed = "Cockatiel",
                gender = AnimalGender.UNKNOWN,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "15/11/2025",
                description = "Gray and yellow bird, escaped from balcony.",
                email = "sarah@example.com",
                phone = null
            ),
            Animal(
                id = "6",
                name = "Snowball",
                photoUrl = "placeholder_cat",
                location = Location("Poznan", 8),
                species = AnimalSpecies.CAT,
                breed = "Persian",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "14/11/2025",
                description = "White long-haired cat, very shy.",
                email = null,
                phone = null
            ),
            Animal(
                id = "7",
                name = "Snoopy",
                photoUrl = "placeholder_dog",
                location = Location("Lodz", 12),
                species = AnimalSpecies.DOG,
                breed = "Beagle",
                gender = AnimalGender.MALE,
                status = AnimalStatus.FOUND,
                lastSeenDate = "20/11/2025",
                description = "Tri-color beagle found wandering near shopping center.",
                email = "finder@example.com",
                phone = "+48 555 666 777"
            ),
            Animal(
                id = "8",
                name = "Thumper",
                photoUrl = "placeholder_rabbit",
                location = Location("Katowice", 6),
                species = AnimalSpecies.RABBIT,
                breed = "Dwarf Rabbit",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.CLOSED,
                lastSeenDate = "13/11/2025",
                description = "Small gray rabbit, reunited with owner.",
                email = "owner@example.com",
                phone = "+48 444 333 222"
            ),
            Animal(
                id = "9",
                name = "Shadow",
                photoUrl = "placeholder_dog",
                location = Location("Szczecin", 20),
                species = AnimalSpecies.DOG,
                breed = "Husky",
                gender = AnimalGender.MALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "12/11/2025",
                description = "Blue-eyed Siberian Husky, very energetic.",
                email = null,
                phone = "+48 888 999 000"
            ),
            Animal(
                id = "10",
                name = "Whiskers",
                photoUrl = "placeholder_cat",
                location = Location("Bialystok", 4),
                species = AnimalSpecies.CAT,
                breed = "British Shorthair",
                gender = AnimalGender.MALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "11/11/2025",
                description = "Gray tabby cat with green eyes.",
                email = "cat.owner@example.com",
                phone = null
            ),
            Animal(
                id = "11",
                name = "Luna",
                photoUrl = "placeholder_dog",
                location = Location("Lublin", 9),
                species = AnimalSpecies.DOG,
                breed = "Golden Retriever",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "10/11/2025",
                description = "Golden retriever puppy, very playful.",
                email = "luna.owner@example.com",
                phone = "+48 222 333 444"
            ),
            Animal(
                id = "12",
                name = "Charlie",
                photoUrl = "placeholder_cat",
                location = Location("Rzeszow", 5),
                species = AnimalSpecies.CAT,
                breed = "Ragdoll",
                gender = AnimalGender.MALE,
                status = AnimalStatus.FOUND,
                lastSeenDate = "21/11/2025",
                description = "Blue-eyed ragdoll cat, found in garage.",
                email = "finder123@example.com",
                phone = null
            ),
            Animal(
                id = "13",
                name = "Max",
                photoUrl = "placeholder_dog",
                location = Location("Torun", 11),
                species = AnimalSpecies.DOG,
                breed = "Dachshund",
                gender = AnimalGender.MALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "09/11/2025",
                description = "Small brown dachshund, wears blue collar.",
                email = "max.family@example.com",
                phone = "+48 333 444 555"
            ),
            Animal(
                id = "14",
                name = "Milo",
                photoUrl = "placeholder_bird",
                location = Location("Gliwice", 6),
                species = AnimalSpecies.BIRD,
                breed = "Parrot",
                gender = AnimalGender.UNKNOWN,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "08/11/2025",
                description = "Green parrot, can say 'Hello'.",
                email = null,
                phone = "+48 666 777 888"
            ),
            Animal(
                id = "15",
                name = "Daisy",
                photoUrl = "placeholder_cat",
                location = Location("Bydgoszcz", 7),
                species = AnimalSpecies.CAT,
                breed = "Bengal",
                gender = AnimalGender.FEMALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "07/11/2025",
                description = "Spotted bengal cat, very active.",
                email = "daisy.home@example.com",
                phone = "+48 777 888 999"
            ),
            Animal(
                id = "16",
                name = "Rocky",
                photoUrl = "placeholder_dog",
                location = Location("Olsztyn", 13),
                species = AnimalSpecies.DOG,
                breed = "Rottweiler",
                gender = AnimalGender.MALE,
                status = AnimalStatus.ACTIVE,
                lastSeenDate = "06/11/2025",
                description = "Large rottweiler, friendly despite size.",
                email = "rocky.owner@example.com",
                phone = null
            )
        )
        
        return allAnimals.take(count.coerceIn(0, 16))
    }
}

