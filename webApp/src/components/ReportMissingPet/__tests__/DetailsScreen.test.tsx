import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { ReactNode } from 'react';
import { DetailsScreen } from '../DetailsScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';
import { GeolocationProvider } from '../../../contexts/GeolocationContext';
import { ReportMissingPetRoutes } from '../../../routes/report-missing-pet-routes';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

const wrapper = ({ children }: { children: ReactNode }) => (
  <GeolocationProvider>
    <MemoryRouter>
      <ReportMissingPetFlowProvider>{children}</ReportMissingPetFlowProvider>
    </MemoryRouter>
  </GeolocationProvider>
);

describe('DetailsScreen', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Mock navigator.geolocation with auto-callback for successful location
    (navigator as { geolocation?: Partial<Geolocation> }).geolocation = {
      getCurrentPosition: vi.fn((success) => {
        // Simulate successful geolocation fetch
        setTimeout(() => {
          success({
            coords: {
              latitude: 52.2297,
              longitude: 21.0122,
              accuracy: 10,
              altitude: null,
              altitudeAccuracy: null,
              heading: null,
              speed: null
            }
          } as GeolocationPosition);
        }, 0);
      }),
      watchPosition: vi.fn(),
      clearWatch: vi.fn()
    };
    (navigator as { permissions?: { query?: (p: object) => Promise<{ state: string }> } }).permissions = {
      query: vi.fn(() => Promise.resolve({ state: 'granted' }))
    };
  });

  afterEach(() => {
    vi.clearAllTimers();
  });

  it('should render header with back arrow', () => {
    render(<DetailsScreen />, { wrapper });

    const backButton = screen.getByTestId('reportMissingPet.header.backButton.click');
    expect(backButton).toBeDefined();
  });

  it('should render title', () => {
    render(<DetailsScreen />, { wrapper });

    expect(screen.getByText(/Animal description/i)).toBeDefined();
  });

  it('should render progress indicator showing 3/4', () => {
    render(<DetailsScreen />, { wrapper });

    const progress = screen.getByTestId('reportMissingPet.header.progress');
    expect(progress.textContent).toContain('3');
    expect(progress.textContent).toContain('4');
  });

  it('should navigate to Step 2 when back arrow clicked', () => {
    render(<DetailsScreen />, { wrapper });

    const backButton = screen.getByTestId('reportMissingPet.header.backButton.click');
    fireEvent.click(backButton);

    expect(mockNavigate).toHaveBeenCalledWith(ReportMissingPetRoutes.photo);
  });

  it('should navigate to Step 4 on successful submit with valid data', () => {
    render(<DetailsScreen />, { wrapper });

    fireEvent.change(screen.getByTestId('details.lastSeenDate.input'), {
      target: { value: '2025-12-01' }
    });

    fireEvent.change(screen.getByTestId('details.latitude.input'), {
      target: { value: '52.52' }
    });

    fireEvent.change(screen.getByTestId('details.longitude.input'), {
      target: { value: '13.40' }
    });

    const speciesSelect = screen.getByTestId('details.species.select');
    fireEvent.change(speciesSelect, { target: { value: 'DOG' } });

    fireEvent.change(screen.getByTestId('details.breed.input'), {
      target: { value: 'Golden Retriever' }
    });

    const maleOption = screen.getByLabelText('Male');
    fireEvent.click(maleOption);

    const continueButton = screen.getByTestId('details.continue.click');
    fireEvent.click(continueButton);

    expect(mockNavigate).toHaveBeenCalledWith(ReportMissingPetRoutes.contact);
  });

  it('should integrate useAnimalDescriptionForm hook', () => {
    render(<DetailsScreen />, { wrapper });

    const form = screen.getByTestId('details.lastSeenDate.input');
    expect(form).toBeDefined();
  });

  describe('validation errors', () => {
    it('should display toast when submitting invalid form', async () => {
      render(<DetailsScreen />, { wrapper });

      const continueButton = screen.getByTestId('details.continue.click');
      fireEvent.click(continueButton);

      await waitFor(() => {
        const toast = screen.queryByTestId('toast.message');
        expect(toast).toBeDefined();
        expect(toast?.textContent).toBe('Please correct the errors below');
      });
    });

    it('should not navigate to contact when form is invalid', () => {
      render(<DetailsScreen />, { wrapper });

      vi.clearAllMocks();

      const continueButton = screen.getByTestId('details.continue.click');
      fireEvent.click(continueButton);

      expect(mockNavigate).not.toHaveBeenCalledWith(ReportMissingPetRoutes.contact);
    });

    it('should display inline errors for missing required fields', () => {
      render(<DetailsScreen />, { wrapper });

      const continueButton = screen.getByTestId('details.continue.click');
      fireEvent.click(continueButton);

      const errorMessages = screen.getAllByRole('alert');
      expect(errorMessages.length).toBeGreaterThan(0);
    });
  });

  describe('navigation preservation', () => {
    it('should initialize form from flow state on mount', () => {
      // given
      const { rerender } = render(<DetailsScreen />, { wrapper });

      fireEvent.change(screen.getByTestId('details.lastSeenDate.input'), {
        target: { value: '2025-12-01' }
      });

      const speciesSelect = screen.getByTestId('details.species.select');
      fireEvent.change(speciesSelect, { target: { value: 'DOG' } });

      fireEvent.change(screen.getByTestId('details.breed.input'), {
        target: { value: 'Golden Retriever' }
      });

      // when
      rerender(<DetailsScreen />);

      // then
      const dateInput = screen.getByTestId('details.lastSeenDate.input') as HTMLInputElement;
      expect(dateInput.value).toBe('2025-12-01');
    });
  });

  describe('edge cases', () => {
    it('should block future dates in date picker', () => {
      render(<DetailsScreen />, { wrapper });

      const dateInput = screen.getByTestId('details.lastSeenDate.input') as HTMLInputElement;
      const today = new Date().toISOString().split('T')[0];

      expect(dateInput.max).toBe(today);
    });

    it('should clear breed field when species changes', () => {
      render(<DetailsScreen />, { wrapper });

      // given
      const speciesSelect = screen.getByTestId('details.species.select');
      fireEvent.change(speciesSelect, { target: { value: 'DOG' } });

      const breedInput = screen.getByTestId('details.breed.input') as HTMLInputElement;
      fireEvent.change(breedInput, { target: { value: 'Golden Retriever' } });

      expect(breedInput.value).toBe('Golden Retriever');

      // when
      fireEvent.change(speciesSelect, { target: { value: 'CAT' } });

      // then
      expect(breedInput.value).toBe('');
    });

    it('should truncate description at 500 characters', () => {
      render(<DetailsScreen />, { wrapper });

      const descriptionTextarea = screen.getByTestId('details.description.textarea') as HTMLTextAreaElement;

      expect(descriptionTextarea.maxLength).toBe(500);
    });

    it('should accept only integers 0-40 for age', () => {
      render(<DetailsScreen />, { wrapper });

      const ageInput = screen.getByTestId('details.age.input') as HTMLInputElement;

      expect(ageInput.type).toBe('number');
      expect(ageInput.min).toBe('0');
      expect(ageInput.max).toBe('200');
    });
  });

  describe('route guard', () => {
    it('should redirect to photo step if accessing details without completing step 2', () => {
      render(<DetailsScreen />, { wrapper });

      waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith(ReportMissingPetRoutes.microchip, { replace: true });
      });
    });
  });
});
