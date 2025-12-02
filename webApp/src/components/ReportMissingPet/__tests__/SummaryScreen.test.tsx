import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { SummaryScreen } from '../SummaryScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

const renderWithProviders = () => {
  return render(
    <ReportMissingPetFlowProvider>
      <MemoryRouter initialEntries={['/report-missing-pet/summary']}>
        <SummaryScreen />
      </MemoryRouter>
    </ReportMissingPetFlowProvider>
  );
};

describe('SummaryScreen', () => {
  describe('rendering', () => {
    it('should display flow state data', () => {
      // given
      renderWithProviders();

      // when
      const heading = screen.getByText(/flow state summary/i);

      // then
      expect(heading).toBeTruthy();
    });

    it('should display header', () => {
      // given
      renderWithProviders();

      // when
      const header = screen.getByTestId('reportMissingPet.header.title');

      // then
      expect(header).toBeTruthy();
    });
  });

  describe('buttons', () => {
    it('should display back button', () => {
      // given
      renderWithProviders();

      // when
      const backButton = screen.queryByTestId('reportMissingPet.header.backButton.click');

      // then
      expect(backButton).toBeTruthy();
    });

    it('should display complete button', () => {
      // given
      renderWithProviders();

      // when
      const completeButton = screen.queryByTestId('summary.complete.button');

      // then
      expect(completeButton).toBeTruthy();
    });

    it('should have clickable back button', () => {
      // given
      renderWithProviders();
      const backButton = screen.getByTestId('reportMissingPet.header.backButton.click') as HTMLButtonElement;

      // when
      const isDisabled = backButton.disabled;

      // then
      expect(isDisabled).toBe(false);
    });

    it('should have clickable complete button', () => {
      // given
      renderWithProviders();
      const completeButton = screen.getByTestId('summary.complete.button') as HTMLButtonElement;

      // when
      const isDisabled = completeButton.disabled;

      // then
      expect(isDisabled).toBe(false);
    });
  });
});
