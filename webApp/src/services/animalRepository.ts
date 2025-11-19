import type { Animal } from '../../../shared/build/js/packages/shared/kotlin/shared';

/**
 * Repository implementation with mocked data for web UI development.
 * Returns hardcoded test data matching Android/iOS implementations.
 * Will be replaced with RemoteAnimalRepository when backend is ready.
 * 
 * This is production code with mocked data - backend integration will replace mock data later.
 */
export class AnimalRepositoryImpl {
    private readonly networkDelayMs = 500;
    
    /**
     * Fetches mock animal data.
     * Simulates network delay and returns list of animals.
     * Uses same mock data structure as Android/iOS for cross-platform consistency.
     * 
     * @returns Promise resolving to array of Animal entities
     */
    async getAnimals(): Promise<Animal[]> {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, this.networkDelayMs));
        
        // Return mock data (matching MockAnimalData from shared module)
        return this.getMockAnimals();
    }
    
    /**
     * Generates mock animal list.
     * Data matches Android/iOS MockAnimalData for consistency across platforms.
     * 
     * @returns Array of 16 Animal entities with varied attributes
     */
    private getMockAnimals(): Animal[] {
        // Note: Using shared Kotlin/JS types imported from built shared module
        // Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus are from shared
        return [
            {
                id: '1',
                name: 'Fluffy',
                photoUrl: 'placeholder_cat',
                location: { city: 'Pruszkow', radiusKm: 5 },
                species: 'CAT' as any,
                breed: 'Maine Coon',
                gender: 'MALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '18/11/2025',
                description: 'Friendly orange tabby cat, last seen near the park.',
                email: 'john.doe@example.com',
                phone: '+48 123 456 789'
            },
            {
                id: '2',
                name: 'Rex',
                photoUrl: 'placeholder_dog',
                location: { city: 'Warsaw', radiusKm: 10 },
                species: 'DOG' as any,
                breed: 'German Shepherd',
                gender: 'FEMALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '17/11/2025',
                description: 'Large black and tan dog, wearing red collar.',
                email: 'anna.smith@example.com',
                phone: null
            },
            {
                id: '3',
                name: 'Bella',
                photoUrl: 'placeholder_cat',
                location: { city: 'Krakow', radiusKm: 3 },
                species: 'CAT' as any,
                breed: 'Siamese',
                gender: 'FEMALE' as any,
                status: 'FOUND' as any,
                lastSeenDate: '19/11/2025',
                description: 'Blue-eyed white cat found near train station.',
                email: null,
                phone: '+48 987 654 321'
            },
            {
                id: '4',
                name: 'Buddy',
                photoUrl: 'placeholder_dog',
                location: { city: 'Wroclaw', radiusKm: 7 },
                species: 'DOG' as any,
                breed: 'Labrador Retriever',
                gender: 'MALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '16/11/2025',
                description: "Yellow lab, very friendly, responds to 'Buddy'.",
                email: 'mike@example.com',
                phone: '+48 111 222 333'
            },
            {
                id: '5',
                name: 'Tweety',
                photoUrl: 'placeholder_bird',
                location: { city: 'Gdansk', radiusKm: 15 },
                species: 'BIRD' as any,
                breed: 'Cockatiel',
                gender: 'UNKNOWN' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '15/11/2025',
                description: 'Gray and yellow bird, escaped from balcony.',
                email: 'sarah@example.com',
                phone: null
            },
            {
                id: '6',
                name: 'Snowball',
                photoUrl: 'placeholder_cat',
                location: { city: 'Poznan', radiusKm: 8 },
                species: 'CAT' as any,
                breed: 'Persian',
                gender: 'FEMALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '14/11/2025',
                description: 'White long-haired cat, very shy.',
                email: null,
                phone: null
            },
            {
                id: '7',
                name: 'Snoopy',
                photoUrl: 'placeholder_dog',
                location: { city: 'Lodz', radiusKm: 12 },
                species: 'DOG' as any,
                breed: 'Beagle',
                gender: 'MALE' as any,
                status: 'FOUND' as any,
                lastSeenDate: '20/11/2025',
                description: 'Tri-color beagle found wandering near shopping center.',
                email: 'finder@example.com',
                phone: '+48 555 666 777'
            },
            {
                id: '8',
                name: 'Thumper',
                photoUrl: 'placeholder_rabbit',
                location: { city: 'Katowice', radiusKm: 6 },
                species: 'RABBIT' as any,
                breed: 'Dwarf Rabbit',
                gender: 'FEMALE' as any,
                status: 'CLOSED' as any,
                lastSeenDate: '13/11/2025',
                description: 'Small gray rabbit, reunited with owner.',
                email: 'owner@example.com',
                phone: '+48 444 333 222'
            },
            {
                id: '9',
                name: 'Shadow',
                photoUrl: 'placeholder_dog',
                location: { city: 'Szczecin', radiusKm: 20 },
                species: 'DOG' as any,
                breed: 'Husky',
                gender: 'MALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '12/11/2025',
                description: 'Blue-eyed Siberian Husky, very energetic.',
                email: null,
                phone: '+48 888 999 000'
            },
            {
                id: '10',
                name: 'Whiskers',
                photoUrl: 'placeholder_cat',
                location: { city: 'Bialystok', radiusKm: 4 },
                species: 'CAT' as any,
                breed: 'British Shorthair',
                gender: 'MALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '11/11/2025',
                description: 'Gray tabby cat with green eyes.',
                email: 'cat.owner@example.com',
                phone: null
            },
            {
                id: '11',
                name: 'Luna',
                photoUrl: 'placeholder_dog',
                location: { city: 'Lublin', radiusKm: 9 },
                species: 'DOG' as any,
                breed: 'Golden Retriever',
                gender: 'FEMALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '10/11/2025',
                description: 'Golden retriever puppy, very playful.',
                email: 'luna.owner@example.com',
                phone: '+48 222 333 444'
            },
            {
                id: '12',
                name: 'Charlie',
                photoUrl: 'placeholder_cat',
                location: { city: 'Rzeszow', radiusKm: 5 },
                species: 'CAT' as any,
                breed: 'Ragdoll',
                gender: 'MALE' as any,
                status: 'FOUND' as any,
                lastSeenDate: '21/11/2025',
                description: 'Blue-eyed ragdoll cat, found in garage.',
                email: 'finder123@example.com',
                phone: null
            },
            {
                id: '13',
                name: 'Max',
                photoUrl: 'placeholder_dog',
                location: { city: 'Torun', radiusKm: 11 },
                species: 'DOG' as any,
                breed: 'Dachshund',
                gender: 'MALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '09/11/2025',
                description: 'Small brown dachshund, wears blue collar.',
                email: 'max.family@example.com',
                phone: '+48 333 444 555'
            },
            {
                id: '14',
                name: 'Milo',
                photoUrl: 'placeholder_bird',
                location: { city: 'Gliwice', radiusKm: 6 },
                species: 'BIRD' as any,
                breed: 'Parrot',
                gender: 'UNKNOWN' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '08/11/2025',
                description: "Green parrot, can say 'Hello'.",
                email: null,
                phone: '+48 666 777 888'
            },
            {
                id: '15',
                name: 'Daisy',
                photoUrl: 'placeholder_cat',
                location: { city: 'Bydgoszcz', radiusKm: 7 },
                species: 'CAT' as any,
                breed: 'Bengal',
                gender: 'FEMALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '07/11/2025',
                description: 'Spotted bengal cat, very active.',
                email: 'daisy.home@example.com',
                phone: '+48 777 888 999'
            },
            {
                id: '16',
                name: 'Rocky',
                photoUrl: 'placeholder_dog',
                location: { city: 'Olsztyn', radiusKm: 13 },
                species: 'DOG' as any,
                breed: 'Rottweiler',
                gender: 'MALE' as any,
                status: 'ACTIVE' as any,
                lastSeenDate: '06/11/2025',
                description: 'Large rottweiler, friendly despite size.',
                email: 'rocky.owner@example.com',
                phone: null
            }
        ];
    }
}

// Export singleton instance
export const animalRepository = new AnimalRepositoryImpl();

