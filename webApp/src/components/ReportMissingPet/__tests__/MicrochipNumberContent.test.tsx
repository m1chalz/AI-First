import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MicrochipNumberContent } from '../MicrochipNumberContent';

describe('MicrochipNumberContent', () => {
  const defaultProps = {
    formattedValue: '',
    onMicrochipChange: vi.fn(),
    onMicrochipPaste: vi.fn(),
    onContinue: vi.fn(),
    onBack: vi.fn(),
  };

  it('renders header with back button and progress', () => {
    // when
    render(<MicrochipNumberContent {...defaultProps} />);

    // then
    expect(screen.getByTestId('reportMissingPet.header.backButton.click')).toBeTruthy();
  });

  it('renders microchip input field', () => {
    // when
    render(<MicrochipNumberContent {...defaultProps} />);

    // then
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field');
    expect(input).toBeTruthy();
    expect(input.getAttribute('placeholder')).toBe('00000-00000-00000');
  });

  it('displays formatted value in input', () => {
    // given
    const props = { ...defaultProps, formattedValue: '12345-67890-12345' };

    // when
    render(<MicrochipNumberContent {...props} />);

    // then
    const input = screen.getByTestId('reportMissingPet.step1.microchipInput.field') as HTMLInputElement;
    expect(input.value).toBe('12345-67890-12345');
  });

  it('renders continue button that is always enabled', () => {
    // when
    render(<MicrochipNumberContent {...defaultProps} />);

    // then
    const button = screen.getByTestId('reportMissingPet.step1.continueButton.click') as HTMLButtonElement;
    expect(button).toBeTruthy();
    expect(button.disabled).toBe(false);
  });

  it('calls onBack when back button clicked', () => {
    // given
    const onBack = vi.fn();
    render(<MicrochipNumberContent {...defaultProps} onBack={onBack} />);

    // when
    screen.getByTestId('reportMissingPet.header.backButton.click').click();

    // then
    expect(onBack).toHaveBeenCalledTimes(1);
  });

  it('calls onContinue when continue button clicked', () => {
    // given
    const onContinue = vi.fn();
    render(<MicrochipNumberContent {...defaultProps} onContinue={onContinue} />);

    // when
    screen.getByTestId('reportMissingPet.step1.continueButton.click').click();

    // then
    expect(onContinue).toHaveBeenCalledTimes(1);
  });
});

