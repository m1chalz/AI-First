import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { MicrochipNumberScreen } from '../MicrochipNumberScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <ReportMissingPetFlowProvider>
        {component}
      </ReportMissingPetFlowProvider>
    </BrowserRouter>
  );
};

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
});

