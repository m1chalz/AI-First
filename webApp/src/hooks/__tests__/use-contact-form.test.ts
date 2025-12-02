import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import React, { ReactNode } from 'react';
import { useContactForm } from '../use-contact-form';
import { ReportMissingPetFlowProvider } from '../../contexts/ReportMissingPetFlowContext';

const wrapper = ({ children }: { children: ReactNode }) =>
  React.createElement(ReportMissingPetFlowProvider, { children });

describe('useContactForm', () => {
  describe('input field state management', () => {
    it('should initialize with empty values from context', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      const { phone, email, reward } = result.current;

      // then
      expect(phone).toBe('');
      expect(email).toBe('');
      expect(reward).toBe('');
    });

    it('should update phone on change', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('+48123456789');
      });

      // then
      expect(result.current.phone).toBe('+48123456789');
    });

    it('should update email on change', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handleEmailChange('test@example.com');
      });

      // then
      expect(result.current.email).toBe('test@example.com');
    });

    it('should update reward on change', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handleRewardChange('$250 gift card + hugs');
      });

      // then
      expect(result.current.reward).toBe('$250 gift card + hugs');
    });
  });

  describe('error state management', () => {
    it('should set phone error on validation failure', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.validatePhone('abc');
      });

      // then
      expect(result.current.phoneError).toBe('Phone number must have at least 7 digits');
    });

    it('should clear phone error on validation success', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.validatePhone('1234567');
      });

      // then
      expect(result.current.phoneError).toBe('');
    });

    it('should set email error on validation failure', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.validateEmail('invalid@');
      });

      // then
      expect(result.current.emailError).toBe('Enter a valid email address');
    });

    it('should clear email error on validation success', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.validateEmail('user@example.com');
      });

      // then
      expect(result.current.emailError).toBe('');
    });

    it('should persist phone error after failed submit', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('123');
      });

      act(() => {
        result.current.handleEmailChange('');
      });

      act(() => {
        result.current.handleSubmit();
      });

      // then
      expect(result.current.phoneError).toBe('Phone number must have at least 7 digits');
    });

    it('should persist email error after failed submit', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('');
      });

      act(() => {
        result.current.handleEmailChange('invalid@');
      });

      act(() => {
        result.current.handleSubmit();
      });

      // then
      expect(result.current.emailError).toBe('Enter a valid email address');
    });
  });

  describe('submit behavior', () => {
    it('should return true with valid phone only', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('1234567');
        result.current.handleEmailChange('');
      });

      // then
      expect(result.current.handleSubmit()).toBe(true);
    });

    it('should return true with valid email only', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('');
        result.current.handleEmailChange('user@example.com');
      });

      // then
      expect(result.current.handleSubmit()).toBe(true);
    });

    it('should return false with no contact methods', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('');
        result.current.handleEmailChange('');
      });

      // then
      expect(result.current.handleSubmit()).toBe(false);
    });

    it('should return false with valid phone but invalid email', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('123');
        result.current.handleEmailChange('invalid');
      });

      // then
      expect(result.current.handleSubmit()).toBe(false);
    });

    it('should accept reward with any text', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('1234567');
        result.current.handleRewardChange('any text @#$%');
      });

      // then
      expect(result.current.handleSubmit()).toBe(true);
    });
  });

  describe('reward field', () => {
    it('should persist reward value', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handleRewardChange('$250');
      });

      // then
      expect(result.current.reward).toBe('$250');
    });

    it('should allow empty reward', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('1234567');
        result.current.handleRewardChange('');
      });

      // then
      expect(result.current.handleSubmit()).toBe(true);
    });

    it('should save reward to flow state exactly as entered (T073)', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('1234567');
        result.current.handleRewardChange('$250 gift card + hugs');
        result.current.handleSubmit();
      });

      // then
      expect(result.current.reward).toBe('$250 gift card + hugs');
    });

    it('should accept submission with any reward text regardless of content (T074)', () => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('1234567');
        result.current.handleRewardChange('!@#$%^&*()_+-=[]{}|;:,.<>?');
      });

      // then
      expect(result.current.handleSubmit()).toBe(true);
    });

    it.each([
      '$250',
      '$250 gift card',
      '$250 gift card + hugs',
      'Free ice cream for life',
      '😀 Happy face reward',
      'Multiple\nlines\nof\ntext',
      '',
    ])('should accept submission with reward text: "%s"', (rewardText) => {
      // given
      const { result } = renderHook(() => useContactForm(), { wrapper });

      // when
      act(() => {
        result.current.handlePhoneChange('1234567');
        result.current.handleRewardChange(rewardText);
      });

      // then
      expect(result.current.handleSubmit()).toBe(true);
    });
  });
});
