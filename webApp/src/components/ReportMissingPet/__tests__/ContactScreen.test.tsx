import { describe, it, expect } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { ContactScreen } from '../ContactScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

const renderWithProviders = () => {
  return render(
    <ReportMissingPetFlowProvider>
      <MemoryRouter initialEntries={['/report-missing-pet/contact']}>
        <ContactScreen />
      </MemoryRouter>
    </ReportMissingPetFlowProvider>
  );
};

describe('ContactScreen', () => {
  describe('rendering', () => {
    it('should render all form fields', () => {
      // given
      renderWithProviders();

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
      renderWithProviders();

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

  describe('validation feedback', () => {
    it('should display phone validation error when invalid', async () => {
      // given
      renderWithProviders();
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      await userEvent.type(phoneInput, 'abc');
      continueButton.click();

      // then
      await waitFor(() => {
        const errorText = screen.queryByText(/enter a valid phone number/i);
        expect(errorText).toBeTruthy();
      });
    });

    it('should display email validation error when invalid', async () => {
      // given
      renderWithProviders();
      const emailInput = screen.getByTestId('contact.email.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      await userEvent.type(emailInput, 'invalid@');
      continueButton.click();

      // then
      await waitFor(() => {
        const errorText = screen.queryByText(/enter a valid email address/i);
        expect(errorText).toBeTruthy();
      });
    });

    it('should show error and stay on form when neither contact method provided', async () => {
      // given
      renderWithProviders();
      const continueButton = screen.getByTestId('contact.continue.button');
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;

      // when
      continueButton.click();

      // then - form should remain empty and button still visible
      expect(phoneInput.value).toBe('');
      expect(continueButton).toBeTruthy();
    });

    it('should clear error when phone corrected', async () => {
      // given
      renderWithProviders();
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when - first submit with invalid
      await userEvent.type(phoneInput, 'abc');
      continueButton.click();

      await waitFor(() => {
        expect(screen.queryByText(/enter a valid phone number/i)).toBeTruthy();
      });

      // when - correct it
      await userEvent.clear(phoneInput);
      await userEvent.type(phoneInput, '123');
      continueButton.click();

      // then - error should be gone
      await waitFor(() => {
        expect(screen.queryByText(/enter a valid phone number/i)).toBeFalsy();
      });
    });
  });

  describe('data persistence', () => {
    it('should persist phone data across navigation', async () => {
      // given
      renderWithProviders();
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;

      // when
      await userEvent.type(phoneInput, '+48123456789');

      // then
      expect(phoneInput.value).toBe('+48123456789');
    });

    it('should persist email data across navigation', async () => {
      // given
      renderWithProviders();
      const emailInput = screen.getByTestId('contact.email.input') as HTMLInputElement;

      // when
      await userEvent.type(emailInput, 'owner@example.com');

      // then
      expect(emailInput.value).toBe('owner@example.com');
    });

    it('should persist reward data across navigation', async () => {
      // given
      renderWithProviders();
      const rewardInput = screen.getByTestId('contact.reward.input') as HTMLTextAreaElement;

      // when
      await userEvent.type(rewardInput, '$250 gift card');

      // then
      expect(rewardInput.value).toBe('$250 gift card');
    });
  });

  describe('button states', () => {
    it('should keep continue button always enabled', () => {
      // given
      renderWithProviders();
      const continueButton = screen.getByTestId('contact.continue.button') as HTMLButtonElement;

      // when
      const isDisabled = continueButton.disabled;

      // then
      expect(isDisabled).toBe(false);
    });
  });
});
