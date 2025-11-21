/**
 * Test fixtures for animal data.
 * These should match the mock data used in webApp/src/services/animal-repository.ts
 */

/**
 * Test constants for E2E tests
 */
export const testConstants = {
    /** Total number of animals in mock data */
    totalAnimalsCount: 16,
    /** Default test animal ID for detailed assertions */
    defaultTestAnimalId: '1',
    /** Timeout for console log messages to appear (ms) */
    consoleLogTimeoutMs: 100,
    /** Expected console message when navigating to report missing form */
    expectedReportMissingMessage: 'Navigate to report missing',
    /** Expected console message pattern for animal details navigation */
    expectedAnimalDetailsMessagePrefix: 'Navigate to animal details: ',
    /** Minimum height of search placeholder in pixels */
    searchPlaceholderMinHeightPx: 60,
} as const;

export interface Location {
    city: string;
    radiusKm: number;
}

export enum AnimalSpecies {
    DOG = 'Dog',
    CAT = 'Cat',
    BIRD = 'Bird',
    RABBIT = 'Rabbit',
    OTHER = 'Other'
}

export enum AnimalGender {
    MALE = 'Male',
    FEMALE = 'Female',
    UNKNOWN = 'Unknown'
}

export enum AnimalStatus {
    ACTIVE = 'Active',
    FOUND = 'Found',
    CLOSED = 'Closed'
}

export interface Animal {
    id: string;
    name: string;
    photoUrl: string;
    location: Location;
    species: AnimalSpecies;
    breed: string;
    gender: AnimalGender;
    status: AnimalStatus;
    lastSeenDate: string;
    description: string;
    email?: string;
    phone?: string;
}

/**
 * Mock animals data matching webApp mock repository.
 * Used for E2E test assertions.
 */
export const mockAnimals: Animal[] = [
    {
        id: '1',
        name: 'Fluffy',
        photoUrl: 'https://koty.pl/wp-content/uploads/2025/08/tuxedo_cat_7b7e699fc0.jpg',
        location: { city: 'Pruszkow', radiusKm: 5 },
        species: AnimalSpecies.CAT,
        breed: 'Maine Coon',
        gender: AnimalGender.MALE,
        status: AnimalStatus.ACTIVE,
        lastSeenDate: '18/11/2025',
        description: 'Friendly orange tabby cat, last seen near the park.',
        email: 'john.doe@example.com',
        phone: '+48 123 456 789'
    },
    {
        id: '2',
        name: 'Rex',
        photoUrl: 'https://pets-style.pl/wp-content/uploads/2023/01/header-york-dog.jpg',
        location: { city: 'Warsaw', radiusKm: 10 },
        species: AnimalSpecies.DOG,
        breed: 'German Shepherd',
        gender: AnimalGender.FEMALE,
        status: AnimalStatus.ACTIVE,
        lastSeenDate: '17/11/2025',
        description: 'Large black and tan dog, wearing red collar.',
        email: 'anna.smith@example.com',
    },
    {
        id: '3',
        name: 'Bella',
        photoUrl: 'https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg',
        location: { city: 'Krakow', radiusKm: 3 },
        species: AnimalSpecies.CAT,
        breed: 'Siamese',
        gender: AnimalGender.FEMALE,
        status: AnimalStatus.FOUND,
        lastSeenDate: '19/11/2025',
        description: 'Blue-eyed white cat found near train station.',
        phone: '+48 987 654 321'
    },
];

/**
 * Get expected formatted location string as displayed in UI.
 */
export function getExpectedLocation(animal: Animal): string {
    return `${animal.location.city}, +${animal.location.radiusKm} km`;
}

/**
 * Get expected species and breed string as displayed in UI.
 */
export function getExpectedSpeciesBreed(animal: Animal): string {
    return `${animal.species} | ${animal.breed}`;
}

/**
 * Get animal by ID from mock data.
 */
export function getAnimalById(id: string): Animal | undefined {
    return mockAnimals.find(animal => animal.id === id);
}

/**
 * Get expected console message for animal details navigation.
 */
export function getExpectedAnimalDetailsMessage(id: string): string {
    return `${testConstants.expectedAnimalDetailsMessagePrefix}${id}`;
}

