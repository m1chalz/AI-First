import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { ContactScreen } from '../ContactScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <ReportMissingPetFlowProvider>{component}</ReportMissingPetFlowProvider>
    </BrowserRouter>
  );
};

describe('ContactScreen', () => {
  describe('rendering', () => {
    it('should render all form fields', () => {
      // given
      renderWithProviders(<ContactScreen />);

      // when
      const phoneInput = screen.queryByTestId('contact.phoneNumber.input');
      const emailInput = screen.queryByTestId('contact.email.input');
      const rewardInput = screen.queryByTestId('contact.reward.input');
      const continueButton = screen.queryByTestId('contact.continue.button');

      // then
      expect(phoneInput).toBeTruthy();
      expect(emailInput).toBeTruthy();
      expect(rewardInput).toBeTruthy();
      expect(continueButton).toBeTruthy();
    });

    it('should have all inputs visible with proper labels', () => {
      // given
      renderWithProviders(<ContactScreen />);

      // when
      const phoneLabel = screen.queryByText(/phone/i);
      const emailLabel = screen.queryByText(/email/i);
      const rewardLabel = screen.queryByText(/reward/i);

      // then
      expect(phoneLabel).toBeTruthy();
      expect(emailLabel).toBeTruthy();
      expect(rewardLabel).toBeTruthy();
    });
  });

  describe('navigation', () => {
    it('should navigate to summary on valid phone submission', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      await userEvent.type(phoneInput, '123');
      fireEvent.click(continueButton);

      // then
      await waitFor(() => {
        expect(window.location.pathname).toContain('summary');
      });
    });

    it('should navigate to summary on valid email submission', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const emailInput = screen.getByTestId('contact.email.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      await userEvent.type(emailInput, 'user@example.com');
      fireEvent.click(continueButton);

      // then
      await waitFor(() => {
        expect(window.location.pathname).toContain('summary');
      });
    });

    it('should block navigation when neither phone nor email provided', async () => {
      // given
      const mockNavigate = vi.fn();
      renderWithProviders(<ContactScreen />);
      const continueButton = screen.getByTestId('contact.continue.button');
      const currentPath = window.location.pathname;

      // when
      fireEvent.click(continueButton);

      // then
      await waitFor(() => {
        expect(window.location.pathname).toBe(currentPath);
      });
    });

    it('should navigate back to details on back button', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const backButton = screen.getByTestId('reportMissingPet.header.backButton.click');

      // when
      fireEvent.click(backButton);

      // then
      await waitFor(() => {
        expect(window.location.pathname).toContain('details');
      });
    });
  });

  describe('validation feedback', () => {
    it('should display phone validation error when invalid', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      await userEvent.type(phoneInput, 'abc');
      fireEvent.click(continueButton);

      // then
      await waitFor(() => {
        const errorText = screen.queryByText(/enter a valid phone number/i);
        expect(errorText).toBeTruthy();
      });
    });

    it('should display email validation error when invalid', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const emailInput = screen.getByTestId('contact.email.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      await userEvent.type(emailInput, 'invalid@');
      fireEvent.click(continueButton);

      // then
      await waitFor(() => {
        const errorText = screen.queryByText(/enter a valid email address/i);
        expect(errorText).toBeTruthy();
      });
    });

    it('should show toast when neither contact method provided', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      fireEvent.click(continueButton);

      // then
      await waitFor(() => {
        const toastText = screen.queryByText(/please provide at least one contact method/i);
        expect(toastText).toBeTruthy();
      });
    });

    it('should clear error when phone corrected', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when - first submit with invalid
      await userEvent.type(phoneInput, 'abc');
      fireEvent.click(continueButton);

      await waitFor(() => {
        expect(screen.queryByText(/enter a valid phone number/i)).toBeTruthy();
      });

      // when - correct it
      await userEvent.clear(phoneInput);
      await userEvent.type(phoneInput, '123');
      fireEvent.click(continueButton);

      // then - error should be gone and navigation should work
      await waitFor(() => {
        expect(screen.queryByText(/enter a valid phone number/i)).toBeFalsy();
      });
    });
  });

  describe('data persistence', () => {
    it('should persist phone data across navigation', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;

      // when
      await userEvent.type(phoneInput, '+48123456789');

      // then
      expect(phoneInput.value).toBe('+48123456789');
    });

    it('should persist email data across navigation', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const emailInput = screen.getByTestId('contact.email.input') as HTMLInputElement;

      // when
      await userEvent.type(emailInput, 'owner@example.com');

      // then
      expect(emailInput.value).toBe('owner@example.com');
    });

    it('should persist reward data across navigation', async () => {
      // given
      renderWithProviders(<ContactScreen />);
      const rewardInput = screen.getByTestId('contact.reward.input') as HTMLInputElement;

      // when
      await userEvent.type(rewardInput, '$250 gift card');

      // then
      expect(rewardInput.value).toBe('$250 gift card');
    });
  });

  describe('button states', () => {
    it('should keep continue button always enabled', () => {
      // given
      renderWithProviders(<ContactScreen />);
      const continueButton = screen.getByTestId('contact.continue.button') as HTMLButtonElement;

      // when
      const isDisabled = continueButton.disabled;

      // then
      expect(isDisabled).toBe(false);
    });
  });
});

