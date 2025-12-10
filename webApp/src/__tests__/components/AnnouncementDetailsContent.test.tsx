import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { AnnouncementDetailsContent } from '../../components/AnnouncementDetailsModal/AnnouncementDetailsContent';
import type { Announcement } from '../../types/animal';

const MOCK_ANNOUNCEMENT: Announcement = {
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
  updatedAt: '2024-11-25T10:00:00Z'
};

describe('AnnouncementDetailsContent - Identification Fields (User Story 2)', () => {
  describe('Microchip Field Display', () => {
    it('should display microchip number with proper formatting (XXXXX-XXXXX-XXXXX)', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, microchipNumber: '882097601234567' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText('Microchip number');
      screen.getByText('88209-76012-34567');
    });

    it.each([
      { microchipNumber: null, description: 'should display "—" when microchip is null' },
      { microchipNumber: '', description: 'should display "—" when microchip is empty string' }
    ])('$description', ({ microchipNumber }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, microchipNumber };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const microchipValues = screen.getAllByText('—');
      expect(microchipValues.length).toBeGreaterThan(0);
    });
  });

  describe('Species and Breed Fields Display', () => {
    it('should display Animal Species label and formatted species', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, species: 'DOG' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText('Animal Species');
      screen.getByText('Dog');
    });

    it('should display Animal Race label and breed', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, breed: 'Labrador' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText('Animal Race');
      screen.getByText('Labrador');
    });

    it('should display "—" for breed when null', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, breed: null };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const dashes = screen.getAllByText('—');
      expect(dashes.length).toBeGreaterThan(0);
    });

    it('should display species and breed in two-column layout', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, species: 'CAT', breed: 'Persian' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

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
      { sex: 'FEMALE' as const, expectedRegex: /Female ♀/, description: 'should display Animal Sex label with female icon for FEMALE' }
    ])('$description', ({ sex, expectedRegex }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, sex };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText('Animal Sex');
      screen.getByText(expectedRegex);
    });

    it('should display "—" for UNKNOWN sex', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, sex: 'UNKNOWN' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const sexLabel = screen.getByText('Animal Sex').closest('.fieldRow') || screen.getByText('Animal Sex').parentElement;
      expect(sexLabel?.textContent).toContain('—');
    });
  });

  describe('Age Field Display', () => {
    it.each([
      { age: 5, expectedText: '5 years', description: 'should display Animal Approx. Age label with age in years' },
      { age: 0, expectedText: '0 years', description: 'should display age 0 correctly' },
      { age: 1, expectedText: '1 years', description: 'should display age 1 correctly' }
    ])('$description', ({ age, expectedText }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, age };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText('Animal Approx. Age');
      screen.getByText(expectedText);
    });

    it('should display "—" when age is null', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, age: null };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const ageLabel = screen.getByText('Animal Approx. Age').closest('.fieldRow') || screen.getByText('Animal Approx. Age').parentElement;
      expect(ageLabel?.textContent).toContain('—');
    });
  });

  describe('Identification Fields Together', () => {
    it('should display all identification fields with proper values', () => {
      // Given
      const announcement: Announcement = {
        ...MOCK_ANNOUNCEMENT,
        microchipNumber: '123456789012345',
        species: 'DOG',
        breed: 'Golden Retriever',
        sex: 'FEMALE',
        age: 3
      };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

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
      const announcement: Announcement = {
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
        updatedAt: '2024-11-25T10:00:00Z'
      };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const dashes = screen.getAllByText('—');
      expect(dashes.length).toBeGreaterThanOrEqual(4);
    });
  });

  describe('Identification Layout', () => {
    it('should render identification section with all fields', () => {
      // Given
      const announcement: Announcement = MOCK_ANNOUNCEMENT;

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      // Verify that all identification fields are rendered
      screen.getByText('Animal Species');
      screen.getByText('Animal Race');
      screen.getByText('Animal Sex');
      screen.getByText('Animal Approx. Age');
    });
  });
});

describe('AnnouncementDetailsContent - Location & Contact Information (User Story 3)', () => {
  describe('Location Display', () => {
    it('should display location coordinates when available', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, locationLatitude: 52.2297, locationLongitude: 21.0122 };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText('Lat / Long');
      screen.getByText(/52\.2297° N, 21\.0122° E/);
    });

    it('should hide location section when coordinates unavailable', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, locationLatitude: null, locationLongitude: null };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      // Should not have the location section at all
      const latLongText = screen.queryByText('Lat / Long');
      expect(latLongText).toBeNull();
    });

    it('should display map button when coordinates available', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, locationLatitude: 40.7128, locationLongitude: -74.006 };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const mapButton = screen.getByTestId('announcementDetails.mapButton.click');
      expect(mapButton).toBeTruthy();
      expect(mapButton.getAttribute('href')).toContain('maps');
      expect(mapButton.getAttribute('target')).toBe('_blank');
    });

    it('should not display map button when coordinates unavailable', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, locationLatitude: null, locationLongitude: null };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const mapButton = screen.queryByTestId('announcementDetails.mapButton.click');
      expect(mapButton).toBeNull();
    });

    it('should handle partial coordinates (one null)', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, locationLatitude: 52.2297, locationLongitude: null };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      // Should hide location section if either coordinate is null
      const latLongText = screen.queryByText('Lat / Long');
      expect(latLongText).toBeNull();
    });
  });

  describe('Contact Information Display', () => {
    it.each([
      { phone: '+48 123 456 789', description: 'should display phone number in header' },
      { phone: '+1 (555) 123-4567', description: 'should display phone with formatting' }
    ])('$description', ({ phone }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, phone };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText(phone);
    });

    it.each([
      { email: 'owner@example.com', description: 'should display email in header' },
      { email: 'contact@petfinder.org', description: 'should display email with organization domain' }
    ])('$description', ({ email }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, email };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText(email);
    });

    it.each([
      { contact: 'phone' as const, description: 'should display "—" when phone is null' },
      { contact: 'email' as const, description: 'should display "—" when email is null' }
    ])('$description', ({ contact }) => {
      // Given
      const petUpdates = contact === 'phone' ? { phone: null } : { email: null };
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, ...petUpdates };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const dashes = screen.getAllByText('—');
      expect(dashes.length).toBeGreaterThan(0);
    });

    it('should display both phone and email exactly as received', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, phone: '+1 (555) 123-4567', email: 'contact@petfinder.org' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      screen.getByText('+1 (555) 123-4567');
      screen.getByText('contact@petfinder.org');
    });
  });

  describe('Location and Contact Together', () => {
    it('should display all location and contact fields with proper values', () => {
      // Given
      const announcement: Announcement = {
        ...MOCK_ANNOUNCEMENT,
        locationLatitude: 48.8566,
        locationLongitude: 2.3522,
        phone: '+33 1 2345 6789',
        email: 'owner@paris.fr'
      };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      // Location
      screen.getByText('Lat / Long');
      screen.getByText(/48\.8566° N, 2\.3522° E/);
      screen.getByTestId('announcementDetails.mapButton.click');

      // Contact
      screen.getByText('+33 1 2345 6789');
      screen.getByText('owner@paris.fr');
    });
  });
});

describe('AnnouncementDetailsContent - Additional Details (User Story 4)', () => {
  describe('Description Display', () => {
    it('should display additional description with label', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, description: 'Friendly and playful dog with brown spots' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText('Animal Additional Description')).toBeTruthy();
      expect(screen.getByText('Friendly and playful dog with brown spots')).toBeTruthy();
    });

    it('should display multi-line description text', () => {
      // Given
      const multiLineDescription = 'Friendly and playful dog\nWith brown spots\nAnswers to "Bella"';
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, description: multiLineDescription };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText(/Friendly and playful dog/)).toBeTruthy();
    });

    it.each([
      { description: null, testName: 'should display "—" when description is null' },
      { description: '', testName: 'should display "—" when description is empty string' }
    ])('$testName', ({ description }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, description };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const descriptions = screen.getAllByText('—');
      expect(descriptions.length).toBeGreaterThan(0);
    });

    it('should handle long descriptions', () => {
      // Given
      const longDescription =
        'This is a very long description that contains detailed information about the pet including behavior, physical characteristics, and other identifying features. The description helps people identify if this is their pet.';
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, description: longDescription };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText(/This is a very long description/)).toBeTruthy();
    });

    it('should handle descriptions with special characters', () => {
      // Given
      const descriptionWithSpecialChars = 'Dog with spots & marks (brown/white) - very friendly!';
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, description: descriptionWithSpecialChars };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText('Dog with spots & marks (brown/white) - very friendly!')).toBeTruthy();
    });
  });

  describe('Description with Other Fields', () => {
    it('should display description along with other identification fields', () => {
      // Given
      const announcement: Announcement = {
        ...MOCK_ANNOUNCEMENT,
        description: 'Friendly dog',
        microchipNumber: '123456789012345',
        species: 'DOG',
        breed: 'Retriever'
      };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

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

describe('AnnouncementDetailsContent - Status Badge (User Story 6)', () => {
  describe('Status Badge Display', () => {
    it.each([
      { status: 'MISSING' as const, description: 'should display badge for MISSING status' },
      { status: 'FOUND' as const, description: 'should display badge for FOUND status' },
      { status: 'CLOSED' as const, description: 'should display badge for CLOSED status' }
    ])('$description', ({ status }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, status };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText(status)).toBeTruthy();
    });

    it('should display status badge text exactly as status value', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, status: 'MISSING' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText('MISSING')).toBeTruthy();
    });
  });

  describe('Status Badge with Other Badges', () => {
    it('should display both status badge and reward badge when both present', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, status: 'MISSING', reward: '500 PLN' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText('MISSING')).toBeTruthy();
      expect(screen.getByText('Reward 500 PLN')).toBeTruthy();
    });

    it('should display status badge but no reward badge when reward is null', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, status: 'FOUND', reward: null };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText('FOUND')).toBeTruthy();
      const rewardElements = screen.queryAllByText(/^Reward/);
      expect(rewardElements.length).toBe(0);
    });
  });
});

describe('AnnouncementDetailsContent - Reward Badge (User Story 5)', () => {
  describe('Reward Badge Display', () => {
    it.each([
      { reward: '500 PLN', description: 'should display reward badge when reward is present' },
      { reward: '1000 EUR', description: 'should display reward with different currency' },
      { reward: 'Contact for reward', description: 'should display reward with custom text' }
    ])('$description', ({ reward }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, reward };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText(`Reward ${reward}`)).toBeTruthy();
    });

    it.each([
      { reward: null, description: 'should not display reward badge when reward is null' },
      { reward: '', description: 'should not display reward badge when reward is empty string' }
    ])('$description', ({ reward }) => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, reward };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      const rewardElements = screen.queryAllByText(/^Reward/);
      expect(rewardElements.length).toBe(0);
    });
  });

  describe('Reward Badge with Status Badge', () => {
    it('should display both status badge (top-right) and reward badge (bottom-left)', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, status: 'MISSING', reward: '500 PLN' };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText('MISSING')).toBeTruthy();
      expect(screen.getByText('Reward 500 PLN')).toBeTruthy();
    });

    it('should display status badge but no reward badge when reward is null', () => {
      // Given
      const announcement: Announcement = { ...MOCK_ANNOUNCEMENT, status: 'FOUND', reward: null };

      // When
      render(<AnnouncementDetailsContent announcement={announcement} />);

      // Then
      expect(screen.getByText('FOUND')).toBeTruthy();
      const rewardElements = screen.queryAllByText(/^Reward/);
      expect(rewardElements.length).toBe(0);
    });
  });
});
