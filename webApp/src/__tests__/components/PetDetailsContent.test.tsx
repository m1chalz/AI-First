import { render, screen } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import { PetDetailsContent } from '../../components/PetDetailsModal/PetDetailsContent';
import type { Animal } from '../../types/animal';

describe('PetDetailsContent - Identification Fields (User Story 2)', () => {
    let mockPet: Animal;

    beforeEach(() => {
        mockPet = {
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
    });

    describe('Microchip Field Display', () => {
        it('should display microchip number with proper formatting (XXXXX-XXXXX-XXXXX)', () => {
            // Given
            const pet: Animal = { ...mockPet, microchipNumber: '882097601234567' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Microchip number');
            screen.getByText('88209-76012-34567');
            expect(true).toBe(true);
        });

        it.each([
            { microchipNumber: null, description: 'should display "—" when microchip is null' },
            { microchipNumber: '', description: 'should display "—" when microchip is empty string' },
        ])('$description', ({ microchipNumber }) => {
            // Given
            const pet: Animal = { ...mockPet, microchipNumber };

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
            const pet: Animal = { ...mockPet, species: 'DOG' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Species');
            screen.getByText('Dog');
            expect(true).toBe(true);
        });

        it('should display Animal Race label and breed', () => {
            // Given
            const pet: Animal = { ...mockPet, breed: 'Labrador' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Race');
            screen.getByText('Labrador');
            expect(true).toBe(true);
        });

        it('should display "—" for breed when null', () => {
            // Given
            const pet: Animal = { ...mockPet, breed: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const dashes = screen.getAllByText('—');
            expect(dashes.length).toBeGreaterThan(0);
        });

        it('should display species and breed in two-column layout', () => {
            // Given
            const pet: Animal = { ...mockPet, species: 'CAT', breed: 'Persian' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Species');
            screen.getByText('Cat');
            screen.getByText('Animal Race');
            screen.getByText('Persian');
            expect(true).toBe(true);
        });
    });

    describe('Sex Field Display', () => {
        it.each([
            { sex: 'MALE' as const, expectedRegex: /Male ♂/, description: 'should display Animal Sex label with male icon for MALE' },
            { sex: 'FEMALE' as const, expectedRegex: /Female ♀/, description: 'should display Animal Sex label with female icon for FEMALE' },
        ])('$description', ({ sex, expectedRegex }) => {
            // Given
            const pet: Animal = { ...mockPet, sex };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Sex');
            screen.getByText(expectedRegex);
            expect(true).toBe(true);
        });

        it('should display "—" for UNKNOWN sex', () => {
            // Given
            const pet: Animal = { ...mockPet, sex: 'UNKNOWN' };

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
            const pet: Animal = { ...mockPet, age };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Approx. Age');
            screen.getByText(expectedText);
            expect(true).toBe(true);
        });

        it('should display "—" when age is null', () => {
            // Given
            const pet: Animal = { ...mockPet, age: null };

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
                ...mockPet,
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
            expect(true).toBe(true);
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
                photoUrl: null,
                status: 'FOUND',
                lastSeenDate: '2024-11-25T10:00:00Z',
                phone: null,
                email: null,
                description: null,
                reward: null,
                locationLatitude: null,
                locationLongitude: null,
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
            const pet: Animal = mockPet;

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // Verify that all identification fields are rendered
            screen.getByText('Animal Species');
            screen.getByText('Animal Race');
            screen.getByText('Animal Sex');
            screen.getByText('Animal Approx. Age');
            expect(true).toBe(true);
        });
    });
});

describe('PetDetailsContent - Location & Contact Information (User Story 3)', () => {
    let mockPet: Animal;

    beforeEach(() => {
        mockPet = {
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
            locationLatitude: 52.2297,
            locationLongitude: 21.0122,
            createdAt: '2024-11-25T10:00:00Z',
            updatedAt: '2024-11-25T10:00:00Z',
        };
    });

    describe('Location Display', () => {
        it('should display location coordinates when available', () => {
            // Given
            const pet: Animal = { ...mockPet, locationLatitude: 52.2297, locationLongitude: 21.0122 };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Lat / Long');
            screen.getByText(/52\.2297° N, 21\.0122° E/);
            expect(true).toBe(true);
        });

        it('should hide location section when coordinates unavailable', () => {
            // Given
            const pet: Animal = { ...mockPet, locationLatitude: null, locationLongitude: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // Should not have the location section at all
            const latLongText = screen.queryByText('Lat / Long');
            expect(latLongText).toBeNull();
        });

        it('should display map button when coordinates available', () => {
            // Given
            const pet: Animal = { ...mockPet, locationLatitude: 40.7128, locationLongitude: -74.006 };

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
            const pet: Animal = { ...mockPet, locationLatitude: null, locationLongitude: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const mapButton = screen.queryByTestId('petDetails.mapButton.click');
            expect(mapButton).toBeNull();
        });

        it('should handle partial coordinates (one null)', () => {
            // Given
            const pet: Animal = { ...mockPet, locationLatitude: 52.2297, locationLongitude: null };

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
            const pet: Animal = { ...mockPet, phone };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText(phone);
            expect(true).toBe(true);
        });

        it.each([
            { email: 'owner@example.com', description: 'should display email in header' },
            { email: 'contact@petfinder.org', description: 'should display email with organization domain' },
        ])('$description', ({ email }) => {
            // Given
            const pet: Animal = { ...mockPet, email };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText(email);
            expect(true).toBe(true);
        });

        it.each([
            { contact: 'phone' as const, description: 'should display "—" when phone is null' },
            { contact: 'email' as const, description: 'should display "—" when email is null' },
        ])('$description', ({ contact }) => {
            // Given
            const petUpdates = contact === 'phone' ? { phone: null } : { email: null };
            const pet: Animal = { ...mockPet, ...petUpdates };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const dashes = screen.getAllByText('—');
            expect(dashes.length).toBeGreaterThan(0);
        });

        it('should display both phone and email exactly as received', () => {
            // Given
            const pet: Animal = { ...mockPet, phone: '+1 (555) 123-4567', email: 'contact@petfinder.org' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('+1 (555) 123-4567');
            screen.getByText('contact@petfinder.org');
            expect(true).toBe(true);
        });
    });

    describe('Location and Contact Together', () => {
        it('should display all location and contact fields with proper values', () => {
            // Given
            const pet: Animal = {
                ...mockPet,
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
            
            expect(true).toBe(true);
        });
    });
});

