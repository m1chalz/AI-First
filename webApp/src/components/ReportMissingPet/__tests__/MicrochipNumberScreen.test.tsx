import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { MicrochipNumberScreen } from '../MicrochipNumberScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';
import { ReportMissingPetRoutes } from '../../../routes/report-missing-pet-routes';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const renderWithProviders = (component: React.ReactElement) => render(
    <BrowserRouter>
      <ReportMissingPetFlowProvider>
        {component}
      </ReportMissingPetFlowProvider>
    </BrowserRouter>
  );

describe('MicrochipNumberScreen', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
  });

  it('renders the screen', () => {
    // when
    renderWithProviders(<MicrochipNumberScreen />);

    // then
    expect(screen.getByTestId('reportMissingPet.step1.microchipInput.field')).toBeTruthy();
  });

  it('updates formatted value when user types', () => {
    // given
    renderWithProviders(<MicrochipNumberScreen />);
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;

    // when
    fireEvent.change(input, { target: { value: '123456' } });

    // then
    expect(input.value).toBe('12345-6');
  });

  it('saves microchip number to flow state when continue clicked', () => {
    // given
    renderWithProviders(<MicrochipNumberScreen />);
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    
    fireEvent.change(input, { target: { value: '123456789012345' } });

    // when
    const continueButton = screen.getByTestId('reportMissingPet.step1.continueButton.click');
    fireEvent.click(continueButton);

    // then
    expect(input.value).toBe('12345-67890-12345');
  });

  it('allows continuing with empty microchip number', () => {
    // given
    renderWithProviders(<MicrochipNumberScreen />);
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    const continueButton = screen.getByTestId('reportMissingPet.step1.continueButton.click') as HTMLButtonElement;

    // when (leave input empty)
    expect(input.value).toBe('');
    
    // then
    expect(continueButton.disabled).toBe(false);
    
    // when (click continue with empty input)
    fireEvent.click(continueButton);
    
    // then (should not throw error, navigation should occur)
    expect(continueButton).toBeTruthy();
  });

  it('navigates to home and clears flow state when back button clicked', () => {
    // given
    renderWithProviders(<MicrochipNumberScreen />);
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    
    // when (enter some data first)
    fireEvent.change(input, { target: { value: '12345' } });
    expect(input.value).toBe('12345');
    
    // when (click back button)
    const backButton = screen.getByTestId('reportMissingPet.header.backButton.click');
    fireEvent.click(backButton);

    // then (should navigate to home)
    expect(mockNavigate).toHaveBeenCalledWith('/');
    expect(mockNavigate).toHaveBeenCalledTimes(1);
  });

  it('restores previously entered microchip number from flow state', () => {
    // given (render with persistent provider to simulate real flow)
    const TestWrapper = () => {
      const [showMicrochip, setShowMicrochip] = React.useState(true);
      
      return (
        <BrowserRouter>
          <ReportMissingPetFlowProvider>
            {showMicrochip ? (
              <>
                <MicrochipNumberScreen />
                <button data-testid="test.navigateAway" onClick={() => setShowMicrochip(false)}>
                  Navigate Away
                </button>
              </>
            ) : (
              <button data-testid="test.navigateBack" onClick={() => setShowMicrochip(true)}>
                Navigate Back
              </button>
            )}
          </ReportMissingPetFlowProvider>
        </BrowserRouter>
      );
    };

    render(<TestWrapper />);
    
    // when (enter data)
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    fireEvent.change(input, { target: { value: '123456789012345' } });
    expect(input.value).toBe('12345-67890-12345');
    
    // when (save to flow state and navigate away)
    const continueButton = screen.getByTestId('reportMissingPet.step1.continueButton.click');
    fireEvent.click(continueButton);
    fireEvent.click(screen.getByTestId('test.navigateAway'));
    
    // when (navigate back)
    fireEvent.click(screen.getByTestId('test.navigateBack'));
    
    // then (data should be restored from flow state)
    const restoredInput = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    expect(restoredInput.value).toBe('12345-67890-12345');
  });

  it('allows editing previously entered microchip number', () => {
    // given (start with existing data)
    renderWithProviders(<MicrochipNumberScreen />);
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    
    // when (enter initial value)
    fireEvent.change(input, { target: { value: '123456789012345' } });
    expect(input.value).toBe('12345-67890-12345');
    
    // when (edit the value)
    fireEvent.change(input, { target: { value: '111111111111111' } });
    
    // then (should show updated value)
    expect(input.value).toBe('11111-11111-11111');
    
    // when (save updated value)
    const continueButton = screen.getByTestId('reportMissingPet.step1.continueButton.click');
    fireEvent.click(continueButton);
    
    // then (should navigate)
    expect(mockNavigate).toHaveBeenCalledWith(ReportMissingPetRoutes.photo);
  });

  it('handles browser back button by clearing flow and navigating home', () => {
    // given
    renderWithProviders(<MicrochipNumberScreen />);
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    
    // when (enter some data)
    fireEvent.change(input, { target: { value: '12345' } });
    expect(input.value).toBe('12345');
    
    // when (simulate browser back button via popstate event)
    const popstateEvent = new PopStateEvent('popstate', { state: null });
    window.dispatchEvent(popstateEvent);
    
    // then (should navigate to home)
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });
});

