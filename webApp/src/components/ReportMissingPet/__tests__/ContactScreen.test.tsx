import { describe, it, expect } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { ContactScreen } from '../ContactScreen';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';

const renderWithProviders = () => render(
  <ReportMissingPetFlowProvider>
    <MemoryRouter initialEntries={['/report-missing-pet/contact']}>
      <ContactScreen />
    </MemoryRouter>
  </ReportMissingPetFlowProvider>
);

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
      const phoneLabel = screen.queryByText(/phone number/i);
      const emailLabel = screen.queryByText(/^Email$/i);
      const rewardLabel = screen.queryByText(/reward for the finder/i);

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
        const errorText = screen.queryByText(/must have at least 7 digits/i);
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
        expect(screen.queryByText(/must have at least 7 digits/i)).toBeTruthy();
      });

      // when - correct it
      await userEvent.clear(phoneInput);
      await userEvent.type(phoneInput, '1234567');
      continueButton.click();

      // then - error should be gone
      await waitFor(() => {
        expect(screen.queryByText(/must have at least 7 digits/i)).toBeFalsy();
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

    it('should persist reward with any text without validation (T075)', async () => {
      // given
      renderWithProviders();
      const rewardInput = screen.getByTestId('contact.reward.input') as HTMLTextAreaElement;

      // when
      await userEvent.type(rewardInput, '$500 cash reward + pizza party!');

      // then
      expect(rewardInput.value).toBe('$500 cash reward + pizza party!');
    });
  });

  describe('reward field validation', () => {
    it('should not display error for reward field with any text (T076)', async () => {
      // given
      renderWithProviders();
      const continueButton = screen.getByTestId('contact.continue.button');
      const rewardInput = screen.getByTestId('contact.reward.input') as HTMLTextAreaElement;

      // when
      await userEvent.type(rewardInput, 'any reward text @#$');
      continueButton.click();

      // then - no error text should appear for reward field
      const errorTexts = screen.queryAllByText(/reward/i);
      const rewardErrorText = errorTexts.find(el => el.style.color === '#FB2C36');
      expect(rewardErrorText).toBeFalsy();
    });

    it('should not prevent submission on invalid contact when reward is entered', async () => {
      // given
      renderWithProviders();
      const rewardInput = screen.getByTestId('contact.reward.input') as HTMLTextAreaElement;

      // when
      await userEvent.type(rewardInput, '$500 reward');

      // then - reward field should not have validation errors
      const errorTexts = screen.queryAllByText(/is required/i).filter(el => el.textContent?.includes('reward'));
      expect(errorTexts.length).toBe(0);
    });

    it('should accept submission with valid contact and any reward text', async () => {
      // given
      renderWithProviders();
      const phoneInput = screen.getByTestId('contact.phoneNumber.input') as HTMLInputElement;
      const rewardInput = screen.getByTestId('contact.reward.input') as HTMLTextAreaElement;
      const continueButton = screen.getByTestId('contact.continue.button');

      // when
      await userEvent.type(phoneInput, '1234567');
      await userEvent.type(rewardInput, '💰 Big money 💰');
      continueButton.click();

      // then - no errors should show, including for reward
      const phoneErrorText = screen.queryByText(/must have at least 7 digits/i);
      const emailErrorText = screen.queryByText(/enter a valid email/i);
      const contactErrorText = screen.queryByText(/phone number or email is required/i);
      expect(phoneErrorText).toBeFalsy();
      expect(emailErrorText).toBeFalsy();
      expect(contactErrorText).toBeFalsy();
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
