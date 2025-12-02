import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { SummaryScreen } from '../SummaryScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <ReportMissingPetFlowProvider>{component}</ReportMissingPetFlowProvider>
    </BrowserRouter>
  );
};

describe('SummaryScreen', () => {
  describe('data display', () => {
    it('should display flow state data', () => {
      // given
      renderWithProviders(<SummaryScreen />);

      // when
      const content = screen.getByText(/summary/i, { selector: 'h' });

      // then
      expect(content).toBeTruthy();
    });

    it('should display header', () => {
      // given
      renderWithProviders(<SummaryScreen />);

      // when
      const header = screen.getByTestId('reportMissingPet.header.title');

      // then
      expect(header).toBeTruthy();
    });
  });

  describe('navigation', () => {
    it('should navigate back to contact on back button', async () => {
      // given
      renderWithProviders(<SummaryScreen />);
      const backButton = screen.getByTestId('reportMissingPet.header.backButton.click');

      // when
      fireEvent.click(backButton);

      // then
      await waitFor(() => {
        expect(window.location.pathname).toContain('contact');
      });
    });

    it('should navigate to home on complete button', async () => {
      // given
      renderWithProviders(<SummaryScreen />);
      const completeButton = screen.getByTestId('summary.complete.button');

      // when
      fireEvent.click(completeButton);

      // then
      await waitFor(() => {
        expect(window.location.pathname).toBe('/');
      });
    });
  });
});

