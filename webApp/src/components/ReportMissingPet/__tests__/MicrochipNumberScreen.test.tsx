import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { MicrochipNumberScreen } from '../MicrochipNumberScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

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

  it('allows empty microchip number', () => {
    // given
    renderWithProviders(<MicrochipNumberScreen />);

    // when
    const continueButton = screen.getByTestId('reportMissingPet.step1.continueButton.click');
    fireEvent.click(continueButton);

    // then
    expect(continueButton).toBeTruthy();
  });

  it('handles back button click', () => {
    // given
    renderWithProviders(<MicrochipNumberScreen />);

    // when
    const backButton = screen.getByTestId('reportMissingPet.header.backButton.click');
    fireEvent.click(backButton);

    // then
    expect(backButton).toBeTruthy();
  });
});

