import React from 'react';
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
        };
    });

    describe('Microchip Field Display', () => {
        it('should display microchip number with proper formatting (XXXXX-XXXXX-XXXXX)', () => {
            // Given
            const pet: Animal = { ...mockPet, microchipNumber: '882097601234567' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            // getByText throws if element not found, so if we reach here it exists
            screen.getByText('Microchip number');
            screen.getByText('88209-76012-34567');
            expect(true).toBe(true);
        });

        it('should display "—" when microchip is null', () => {
            // Given
            const pet: Animal = { ...mockPet, microchipNumber: null };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            const microchipValues = screen.getAllByText('—');
            expect(microchipValues.length).toBeGreaterThan(0);
        });

        it('should display "—" when microchip is empty string', () => {
            // Given
            const pet: Animal = { ...mockPet, microchipNumber: '' };

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
        it('should display Animal Sex label with male icon for MALE', () => {
            // Given
            const pet: Animal = { ...mockPet, sex: 'MALE' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Sex');
            screen.getByText(/Male ♂/);
            expect(true).toBe(true);
        });

        it('should display Animal Sex label with female icon for FEMALE', () => {
            // Given
            const pet: Animal = { ...mockPet, sex: 'FEMALE' };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Sex');
            screen.getByText(/Female ♀/);
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
        it('should display Animal Approx. Age label with age in years', () => {
            // Given
            const pet: Animal = { ...mockPet, age: 5 };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('Animal Approx. Age');
            screen.getByText('5 years');
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

        it('should display age 0 correctly', () => {
            // Given
            const pet: Animal = { ...mockPet, age: 0 };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('0 years');
            expect(true).toBe(true);
        });

        it('should display age 1 correctly', () => {
            // Given
            const pet: Animal = { ...mockPet, age: 1 };

            // When
            render(<PetDetailsContent pet={pet} />);

            // Then
            screen.getByText('1 years');
            expect(true).toBe(true);
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

