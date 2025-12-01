import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useMicrochipFormatter } from '../use-microchip-formatter';

describe('useMicrochipFormatter', () => {
  describe('typing digits', () => {
    it.each([
      { input: '1', expectedValue: '1', expectedFormatted: '1' },
      { input: '12345', expectedValue: '12345', expectedFormatted: '12345' },
      { input: '123456', expectedValue: '123456', expectedFormatted: '12345-6' },
      { input: '1234567890', expectedValue: '1234567890', expectedFormatted: '12345-67890' },
      { input: '123456789012345', expectedValue: '123456789012345', expectedFormatted: '12345-67890-12345' },
    ])('formats $input as $expectedFormatted', ({ input, expectedValue, expectedFormatted }) => {
      // given
      const { result } = renderHook(() => useMicrochipFormatter());

      // when
      act(() => {
        result.current.handleChange({ target: { value: input } } as React.ChangeEvent<HTMLInputElement>);
      });

      // then
      expect(result.current.value).toBe(expectedValue);
      expect(result.current.formattedValue).toBe(expectedFormatted);
    });
  });

  describe('max length enforcement', () => {
    it('limits input to 15 digits', () => {
      // given
      const { result } = renderHook(() => useMicrochipFormatter());

      // when
      act(() => {
        result.current.handleChange({ target: { value: '12345678901234567890' } } as React.ChangeEvent<HTMLInputElement>);
      });

      // then
      expect(result.current.value).toBe('123456789012345');
      expect(result.current.formattedValue).toBe('12345-67890-12345');
    });
  });

  describe('paste with non-numeric characters', () => {
    it.each([
      { pasted: 'ABC123XYZ456', expectedValue: '123456', expectedFormatted: '12345-6' },
      { pasted: '12-34-56-78-90', expectedValue: '1234567890', expectedFormatted: '12345-67890' },
      { pasted: 'ABCXYZ', expectedValue: '', expectedFormatted: '' },
      { pasted: '123.456.789', expectedValue: '123456789', expectedFormatted: '12345-6789' },
    ])('strips non-digits from $pasted to $expectedValue', ({ pasted, expectedValue, expectedFormatted }) => {
      // given
      const { result } = renderHook(() => useMicrochipFormatter());

      // when
      act(() => {
        result.current.handlePaste({
          // eslint-disable-next-line @typescript-eslint/no-empty-function
          preventDefault: () => {},
          clipboardData: { getData: () => pasted },
        } as unknown as React.ClipboardEvent<HTMLInputElement>);
      });

      // then
      expect(result.current.value).toBe(expectedValue);
      expect(result.current.formattedValue).toBe(expectedFormatted);
    });

    it('limits pasted content to 15 digits', () => {
      // given
      const { result } = renderHook(() => useMicrochipFormatter());

      // when
      act(() => {
        result.current.handlePaste({
          // eslint-disable-next-line @typescript-eslint/no-empty-function
          preventDefault: () => {},
          clipboardData: { getData: () => '12345678901234567890' },
        } as unknown as React.ClipboardEvent<HTMLInputElement>);
      });

      // then
      expect(result.current.value).toBe('123456789012345');
      expect(result.current.formattedValue).toBe('12345-67890-12345');
    });
  });

  describe('reset', () => {
    it('clears value and formattedValue', () => {
      // given
      const { result } = renderHook(() => useMicrochipFormatter());
      act(() => {
        result.current.handleChange({ target: { value: '123456789012345' } } as React.ChangeEvent<HTMLInputElement>);
      });

      // when
      act(() => {
        result.current.reset();
      });

      // then
      expect(result.current.value).toBe('');
      expect(result.current.formattedValue).toBe('');
    });
  });
});

