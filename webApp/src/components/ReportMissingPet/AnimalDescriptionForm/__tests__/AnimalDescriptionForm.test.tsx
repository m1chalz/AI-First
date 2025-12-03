import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AnimalDescriptionForm } from '../AnimalDescriptionForm';

describe('AnimalDescriptionForm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Mock navigator.geolocation
    (navigator as any).geolocation = {
      getCurrentPosition: vi.fn(),
      watchPosition: vi.fn(),
      clearWatch: vi.fn(),
    };
    (navigator as any).permissions = {
      query: vi.fn(async () => ({ state: 'granted' })),
    };
  });
  const mockOnSubmit = vi.fn();
  const defaultFormData = {
    lastSeenDate: '2025-12-01',
    species: '',
    breed: '',
    sex: '',
    age: '',
    description: '',
    latitude: '',
    longitude: '',
    validationErrors: {}
  };

  it('should render date input field', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const dateInput = screen.getByTestId('details.lastSeenDate.input');
    expect(dateInput).toBeDefined();
    expect(dateInput.getAttribute('type')).toBe('date');
  });

  it('should render species dropdown', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const speciesDropdown = screen.getByTestId('details.species.select');
    expect(speciesDropdown).toBeDefined();
  });

  it('should render breed input field', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const breedInput = screen.getByTestId('details.breed.input');
    expect(breedInput).toBeDefined();
  });

  it('should render gender selector', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const genderSelector = screen.getByTestId('details.sex.select');
    expect(genderSelector).toBeDefined();
  });

  it('should render age input field', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const ageInput = screen.getByTestId('details.age.input');
    expect(ageInput).toBeDefined();
    expect(ageInput.getAttribute('type')).toBe('number');
  });

  it('should render description textarea', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const textarea = screen.getByTestId('details.description.textarea');
    expect(textarea).toBeDefined();
    expect(textarea.getAttribute('maxLength')).toBe('500');
  });

  it('should render enabled GPS button', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const gpsButton = screen.getByTestId('details.gpsButton.click');
    expect(gpsButton).toBeDefined();
    expect((gpsButton as HTMLButtonElement).disabled).toBe(false);
  });

  it('should request GPS position on GPS button click', async () => {
    // given
    const mockGetCurrentPosition = vi.fn((success) => {
      success({
        coords: {
          latitude: 52.2297,
          longitude: 21.0122,
        },
      } as GeolocationPosition);
    });
    (navigator.geolocation.getCurrentPosition as any) = mockGetCurrentPosition;

    const mockOnFieldChange = vi.fn();

    // when
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={mockOnFieldChange}
        onSubmit={mockOnSubmit}
      />
    );

    const gpsButton = screen.getByTestId('details.gpsButton.click');
    fireEvent.click(gpsButton);

    // then
    await waitFor(() => {
      expect(mockGetCurrentPosition).toHaveBeenCalled();
      expect(mockOnFieldChange).toHaveBeenCalledWith('latitude', '52.2297');
      expect(mockOnFieldChange).toHaveBeenCalledWith('longitude', '21.0122');
    });
  });

  it('should populate lat/long fields when coordinates are fetched', async () => {
    // given
    const mockGetCurrentPosition = vi.fn((success) => {
      success({
        coords: {
          latitude: 52.2297,
          longitude: 21.0122,
        },
      } as GeolocationPosition);
    });
    (navigator.geolocation.getCurrentPosition as any) = mockGetCurrentPosition;

    const mockOnFieldChange = vi.fn();

    // when
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={mockOnFieldChange}
        onSubmit={mockOnSubmit}
      />
    );

    const gpsButton = screen.getByTestId('details.gpsButton.click');
    fireEvent.click(gpsButton);

    // then
    await waitFor(() => {
      expect(mockOnFieldChange).toHaveBeenCalledWith('latitude', '52.2297');
      expect(mockOnFieldChange).toHaveBeenCalledWith('longitude', '21.0122');
    });
  });

  it('should show "Locating..." while GPS is being requested', async () => {
    // given
    const mockGetCurrentPosition = vi.fn(() => {
      // Intentionally not calling success to simulate loading state
    });
    (navigator.geolocation.getCurrentPosition as any) = mockGetCurrentPosition;
    // Mock permissions query to return 'granted' so it skips permission check
    (navigator.permissions.query as any) = vi.fn(async () => ({ state: 'granted' }));

    // when
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );

    const gpsButton = screen.getByTestId('details.gpsButton.click') as HTMLButtonElement;
    fireEvent.click(gpsButton);

    // then
    await waitFor(() => {
      expect(gpsButton.textContent).toBe('Locating...');
      expect(gpsButton.disabled).toBe(true);
    });
  });

  it('should show "Location not available" when permission is denied after clicking the button', async () => {
    // given
    (navigator.permissions.query as any) = vi.fn(async () => ({ state: 'denied' }));

    // when
    const mockOnFieldChange = vi.fn();
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={mockOnFieldChange}
        onSubmit={mockOnSubmit}
      />
    );

    const gpsButton = screen.getByTestId('details.gpsButton.click') as HTMLButtonElement;
    fireEvent.click(gpsButton);

    // then
    await waitFor(() => {
      expect(gpsButton.textContent).toBe('Location not available');
      expect(gpsButton.disabled).toBe(true);
    });
  });

  it('should show "Location not available" when geolocation returns permission denied error', async () => {
    // given
    const mockGetCurrentPosition = vi.fn((_, errorCallback) => {
      errorCallback({
        code: 1, // PERMISSION_DENIED
        message: 'User denied geolocation',
        PERMISSION_DENIED: 1,
        POSITION_UNAVAILABLE: 2,
        TIMEOUT: 3,
      } as GeolocationPositionError);
    });
    (navigator.geolocation.getCurrentPosition as any) = mockGetCurrentPosition;
    (navigator.permissions.query as any) = vi.fn(async () => ({ state: 'prompt' }));

    // when
    const mockOnFieldChange = vi.fn();
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={mockOnFieldChange}
        onSubmit={mockOnSubmit}
      />
    );

    const gpsButton = screen.getByTestId('details.gpsButton.click') as HTMLButtonElement;
    fireEvent.click(gpsButton);

    // then
    await waitFor(() => {
      expect(gpsButton.textContent).toBe('Location not available');
      expect(gpsButton.disabled).toBe(true);
    });
  });

  it('should render Continue button', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const continueButton = screen.getByTestId('details.continue.click');
    expect(continueButton).toBeDefined();
  });

  it('should disable breed field when species is not selected', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const breedInput = screen.getByTestId('details.breed.input') as HTMLInputElement;
    expect(breedInput.disabled).toBe(true);
  });

  it('should enable breed field when species is selected', () => {
    const formDataWithSpecies = { ...defaultFormData, species: 'DOG' };
    
    render(
      <AnimalDescriptionForm
        formData={formDataWithSpecies}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const breedInput = screen.getByTestId('details.breed.input') as HTMLInputElement;
    expect(breedInput.disabled).toBe(false);
  });

  it('should set max attribute to today on date picker', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const dateInput = screen.getByTestId('details.lastSeenDate.input');
    const today = new Date().toISOString().split('T')[0];
    expect(dateInput.getAttribute('max')).toBe(today);
  });

  it('should call onSubmit when Continue button is clicked', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const continueButton = screen.getByTestId('details.continue.click');
    fireEvent.click(continueButton);
    
    expect(mockOnSubmit).toHaveBeenCalled();
  });

});

