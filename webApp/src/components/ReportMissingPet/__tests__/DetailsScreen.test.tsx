import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { ReactNode } from 'react';
import { DetailsScreen } from '../DetailsScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const wrapper = ({ children }: { children: ReactNode }) => (
  <MemoryRouter>
    <ReportMissingPetFlowProvider>{children}</ReportMissingPetFlowProvider>
  </MemoryRouter>
);

describe('DetailsScreen', () => {
  it('should render header with back arrow', () => {
    render(<DetailsScreen />, { wrapper });
    
    const backButton = screen.getByTestId('details.back.click');
    expect(backButton).toBeDefined();
  });

  it('should render title', () => {
    render(<DetailsScreen />, { wrapper });
    
    expect(screen.getByText(/Animal Description/i)).toBeDefined();
  });

  it('should render progress indicator showing 3/4', () => {
    render(<DetailsScreen />, { wrapper });
    
    const progress = screen.getByTestId('details.progress.text');
    expect(progress.textContent).toContain('3');
    expect(progress.textContent).toContain('4');
  });

  it('should navigate to Step 2 when back arrow clicked', () => {
    render(<DetailsScreen />, { wrapper });
    
    const backButton = screen.getByTestId('details.back.click');
    fireEvent.click(backButton);
    
    expect(mockNavigate).toHaveBeenCalledWith('/report-missing/photo');
  });

  it('should navigate to Step 4 on successful submit with valid data', () => {
    render(<DetailsScreen />, { wrapper });
    
    fireEvent.change(screen.getByTestId('details.lastSeenDate.input'), {
      target: { value: '2025-12-01' }
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
    
    expect(mockNavigate).toHaveBeenCalledWith('/report-missing/contact');
  });

  it('should integrate useAnimalDescriptionForm hook', () => {
    render(<DetailsScreen />, { wrapper });
    
    const form = screen.getByTestId('details.lastSeenDate.input');
    expect(form).toBeDefined();
  });
});

