import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { PetDetailsContent } from '../../components/PetDetailsModal/PetDetailsContent';
import type { Animal } from '../../types/animal';

const MOCK_PET: Animal = {
    id: '123',
    petName: 'Bella',
    species: 'DOG',
    breed: 'Golden Retriever',
    sex: 'FEMALE',
    age: 3,
    microchipNumber: '123456789012345',
    photoUrl: 'https://example.com/photo.jpg',
    status: 'MISSING',
    lastSeenDate: '2024-11-25T10:00:00Z',
    phone: '+1234567890',
    email: 'owner@example.com',
    description: 'Friendly and playful',
    reward: null,
    locationLatitude: null,
    locationLongitude: null,
    createdAt: '2024-11-25T10:00:00Z',
    updatedAt: '2024-11-25T10:00:00Z',
};

describe('PetDetailsContent - Identification Fields (User Story 2)', () => {

    describe('Microchip Field Display', () => {
        it('should display microchip number with proper formatting (XXXXX-XXXXX-XXXXX)', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, microchipNumber: '882097601234567' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Microchip number');
            screen.getByText('88209-76012-34567');
        });

        it.each([
            { microchipNumber: null, description: 'should display "—" when microchip is null' },
            { microchipNumber: '', description: 'should display "—" when microchip is empty string' },
        ])('$description', ({ microchipNumber }) => {
            // Given
            const pet: Animal = { ...MOCK_PET, microchipNumber };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const microchipValues = screen.getAllByText('—');
            expect(microchipValues.length).toBeGreaterThan(0);
        });
    });

    describe('Species and Breed Fields Display', () => {
        it('should display Animal Species label and formatted species', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, species: 'DOG' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Species');
            screen.getByText('Dog');
        });

        it('should display Animal Race label and breed', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, breed: 'Labrador' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Race');
            screen.getByText('Labrador');
        });

        it('should display "—" for breed when null', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, breed: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const dashes = screen.getAllByText('—');
            expect(dashes.length).toBeGreaterThan(0);
        });

        it('should display species and breed in two-column layout', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, species: 'CAT', breed: 'Persian' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Species');
            screen.getByText('Cat');
            screen.getByText('Animal Race');
            screen.getByText('Persian');
        });
    });

    describe('Sex Field Display', () => {
        it.each([
            { sex: 'MALE' as const, expectedRegex: /Male ♂/, description: 'should display Animal Sex label with male icon for MALE' },
            { sex: 'FEMALE' as const, expectedRegex: /Female ♀/, description: 'should display Animal Sex label with female icon for FEMALE' },
        ])('$description', ({ sex, expectedRegex }) => {
            // Given
            const pet: Animal = { ...MOCK_PET, sex };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Sex');
            screen.getByText(expectedRegex);
        });

        it('should display "—" for UNKNOWN sex', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, sex: 'UNKNOWN' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const sexLabel = screen.getByText('Animal Sex').closest('.fieldRow') || screen.getByText('Animal Sex').parentElement;
            expect(sexLabel?.textContent).toContain('—');
        });
    });

    describe('Age Field Display', () => {
        it.each([
            { age: 5, expectedText: '5 years', description: 'should display Animal Approx. Age label with age in years' },
            { age: 0, expectedText: '0 years', description: 'should display age 0 correctly' },
            { age: 1, expectedText: '1 years', description: 'should display age 1 correctly' },
        ])('$description', ({ age, expectedText }) => {
            // Given
            const pet: Animal = { ...MOCK_PET, age };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Approx. Age');
            screen.getByText(expectedText);
        });

        it('should display "—" when age is null', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, age: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const ageLabel = screen.getByText('Animal Approx. Age').closest('.fieldRow') || screen.getByText('Animal Approx. Age').parentElement;
            expect(ageLabel?.textContent).toContain('—');
        });
    });

    describe('Identification Fields Together', () => {
        it('should display all identification fields with proper values', () => {
            // Given
            const pet: Animal = {
                ...MOCK_PET,
                microchipNumber: '123456789012345',
                species: 'DOG',
                breed: 'Golden Retriever',
                sex: 'FEMALE',
                age: 3,
            };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Microchip number');
            screen.getByText('12345-67890-12345');
            screen.getByText('Animal Species');
            screen.getByText('Dog');
            screen.getByText('Animal Race');
            screen.getByText('Golden Retriever');
            screen.getByText('Animal Sex');
            screen.getByText(/Female ♀/);
            screen.getByText('Animal Approx. Age');
            screen.getByText('3 years');
        });

        it('should display all fields with null/empty values as dashes', () => {
            // Given
            const pet: Animal = {
                id: '456',
                petName: 'Unknown',
                species: 'DOG',
                breed: null,
                sex: 'UNKNOWN',
                age: null,
                microchipNumber: null,
                photoUrl: 'https://example.com/photo.jpg',
                status: 'FOUND',
                lastSeenDate: '2024-11-25T10:00:00Z',
                phone: null,
                email: null,
                description: null,
                reward: null,
                locationLatitude: null,
                locationLongitude: null,
                createdAt: '2024-11-25T10:00:00Z',
                updatedAt: '2024-11-25T10:00:00Z',
            };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const dashes = screen.getAllByText('—');
            expect(dashes.length).toBeGreaterThanOrEqual(4);
        });
    });

    describe('Identification Layout', () => {
        it('should render identification section with all fields', () => {
            // Given
            const pet: Animal = MOCK_PET;

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // Verify that all identification fields are rendered
            screen.getByText('Animal Species');
            screen.getByText('Animal Race');
            screen.getByText('Animal Sex');
            screen.getByText('Animal Approx. Age');
        });
    });
});

describe('PetDetailsContent - Location & Contact Information (User Story 3)', () => {
    describe('Location Display', () => {
        it('should display location coordinates when available', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, locationLatitude: 52.2297, locationLongitude: 21.0122 };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Lat / Long');
            screen.getByText(/52\.2297° N, 21\.0122° E/);
        });

        it('should hide location section when coordinates unavailable', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, locationLatitude: null, locationLongitude: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // Should not have the location section at all
            const latLongText = screen.queryByText('Lat / Long');
            expect(latLongText).toBeNull();
        });

        it('should display map button when coordinates available', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, locationLatitude: 40.7128, locationLongitude: -74.006 };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const mapButton = screen.getByTestId('petDetails.mapButton.click');
            expect(mapButton).toBeTruthy();
            expect(mapButton.getAttribute('href')).toContain('maps');
            expect(mapButton.getAttribute('target')).toBe('_blank');
        });

        it('should not display map button when coordinates unavailable', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, locationLatitude: null, locationLongitude: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const mapButton = screen.queryByTestId('petDetails.mapButton.click');
            expect(mapButton).toBeNull();
        });

        it('should handle partial coordinates (one null)', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, locationLatitude: 52.2297, locationLongitude: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // Should hide location section if either coordinate is null
            const latLongText = screen.queryByText('Lat / Long');
            expect(latLongText).toBeNull();
        });
    });

    describe('Contact Information Display', () => {
        it.each([
            { phone: '+48 123 456 789', description: 'should display phone number in header' },
            { phone: '+1 (555) 123-4567', description: 'should display phone with formatting' },
        ])('$description', ({ phone }) => {
            // Given
            const pet: Animal = { ...MOCK_PET, phone };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText(phone);
        });

        it.each([
            { email: 'owner@example.com', description: 'should display email in header' },
            { email: 'contact@petfinder.org', description: 'should display email with organization domain' },
        ])('$description', ({ email }) => {
            // Given
            const pet: Animal = { ...MOCK_PET, email };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText(email);
        });

        it.each([
            { contact: 'phone' as const, description: 'should display "—" when phone is null' },
            { contact: 'email' as const, description: 'should display "—" when email is null' },
        ])('$description', ({ contact }) => {
            // Given
            const petUpdates = contact === 'phone' ? { phone: null } : { email: null };
            const pet: Animal = { ...MOCK_PET, ...petUpdates };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const dashes = screen.getAllByText('—');
            expect(dashes.length).toBeGreaterThan(0);
        });

        it('should display both phone and email exactly as received', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, phone: '+1 (555) 123-4567', email: 'contact@petfinder.org' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('+1 (555) 123-4567');
            screen.getByText('contact@petfinder.org');
        });
    });

    describe('Location and Contact Together', () => {
        it('should display all location and contact fields with proper values', () => {
            // Given
            const pet: Animal = {
                ...MOCK_PET,
                locationLatitude: 48.8566,
                locationLongitude: 2.3522,
                phone: '+33 1 2345 6789',
                email: 'owner@paris.fr',
            };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // Location
            screen.getByText('Lat / Long');
            screen.getByText(/48\.8566° N, 2\.3522° E/);
            screen.getByTestId('petDetails.mapButton.click');
            
            // Contact
            screen.getByText('+33 1 2345 6789');
            screen.getByText('owner@paris.fr');
            
        });
    });
});

describe('PetDetailsContent - Additional Pet Details (User Story 4)', () => {

    describe('Description Display', () => {
        it('should display additional description with label', () => {
            // Given
            const pet: Animal = { ...MOCK_PET, description: 'Friendly and playful dog with brown spots' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            expect(screen.getByText('Animal Additional Description')).toBeTruthy();
            expect(screen.getByText('Friendly and playful dog with brown spots')).toBeTruthy();
        });

        it('should display multi-line description text', () => {
            // Given
            const multiLineDescription = 'Friendly and playful dog\nWith brown spots\nAnswers to "Bella"';
            const pet: Animal = { ...MOCK_PET, description: multiLineDescription };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            expect(screen.getByText(/Friendly and playful dog/)).toBeTruthy();
        });

        it.each([
            { description: null, testName: 'should display "—" when description is null' },
            { description: '', testName: 'should display "—" when description is empty string' },
        ])('$testName', ({ description }) => {
            // Given
            const pet: Animal = { ...MOCK_PET, description };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const descriptions = screen.getAllByText('—');
            expect(descriptions.length).toBeGreaterThan(0);
        });

        it('should handle long descriptions', () => {
            // Given
            const longDescription = 'This is a very long description that contains detailed information about the pet including behavior, physical characteristics, and other identifying features. The description helps people identify if this is their pet.';
            const pet: Animal = { ...MOCK_PET, description: longDescription };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            expect(screen.getByText(/This is a very long description/)).toBeTruthy();
        });

        it('should handle descriptions with special characters', () => {
            // Given
            const descriptionWithSpecialChars = 'Dog with spots & marks (brown/white) - very friendly!';
            const pet: Animal = { ...MOCK_PET, description: descriptionWithSpecialChars };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            expect(screen.getByText('Dog with spots & marks (brown/white) - very friendly!')).toBeTruthy();
        });
    });

    describe('Description with Other Fields', () => {
        it('should display description along with other identification fields', () => {
            // Given
            const pet: Animal = {
                ...MOCK_PET,
                description: 'Friendly dog',
                microchipNumber: '123456789012345',
                species: 'DOG',
                breed: 'Retriever',
            };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // Identification fields
            expect(screen.getByText('Microchip number')).toBeTruthy();
            expect(screen.getByText('Animal Species')).toBeTruthy();
            expect(screen.getByText('Animal Race')).toBeTruthy();
            
            // Description
            expect(screen.getByText('Animal Additional Description')).toBeTruthy();
            expect(screen.getByText('Friendly dog')).toBeTruthy();
        });
    });
});

