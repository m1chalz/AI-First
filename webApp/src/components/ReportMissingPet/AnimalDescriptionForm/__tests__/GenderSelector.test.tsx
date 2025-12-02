import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { GenderSelector } from '../GenderSelector';

describe('GenderSelector', () => {
  const mockOnChange = vi.fn();

  it('should render Male and Female options', () => {
    render(<GenderSelector value="" onChange={mockOnChange} />);
    
    expect(screen.getByText('Male')).toBeDefined();
    expect(screen.getByText('Female')).toBeDefined();
  });

  it.each(['Male', 'Female'])(
    'should display %s as capitalized label',
    (label) => {
      render(<GenderSelector value="" onChange={mockOnChange} />);
      
      expect(screen.getByText(label)).toBeDefined();
    }
  );

  it('should select Male when clicked', () => {
    render(<GenderSelector value="" onChange={mockOnChange} />);
    
    const maleInput = screen.getByLabelText('Male');
    fireEvent.click(maleInput);
    
    expect(mockOnChange).toHaveBeenCalledWith('MALE');
  });

  it('should select Female when clicked', () => {
    render(<GenderSelector value="" onChange={mockOnChange} />);
    
    const femaleInput = screen.getByLabelText('Female');
    fireEvent.click(femaleInput);
    
    expect(mockOnChange).toHaveBeenCalledWith('FEMALE');
  });

  it('should show Male as checked when value is MALE', () => {
    render(<GenderSelector value="MALE" onChange={mockOnChange} />);
    
    const maleInput = screen.getByLabelText('Male') as HTMLInputElement;
    expect(maleInput.checked).toBe(true);
  });

  it('should show Female as checked when value is FEMALE', () => {
    render(<GenderSelector value="FEMALE" onChange={mockOnChange} />);
    
    const femaleInput = screen.getByLabelText('Female') as HTMLInputElement;
    expect(femaleInput.checked).toBe(true);
  });

  it('should have mutually exclusive selection', () => {
    const { rerender } = render(<GenderSelector value="MALE" onChange={mockOnChange} />);
    
    const maleInput = screen.getByLabelText('Male') as HTMLInputElement;
    const femaleInput = screen.getByLabelText('Female') as HTMLInputElement;
    
    expect(maleInput.checked).toBe(true);
    expect(femaleInput.checked).toBe(false);
    
    rerender(<GenderSelector value="FEMALE" onChange={mockOnChange} />);
    
    expect(maleInput.checked).toBe(false);
    expect(femaleInput.checked).toBe(true);
  });

  it('should display error message when provided', () => {
    const errorMessage = 'Please select a gender';
    render(<GenderSelector value="" onChange={mockOnChange} error={errorMessage} />);
    
    expect(screen.getByText(errorMessage)).toBeDefined();
  });

  it('should not display error message when not provided', () => {
    render(<GenderSelector value="" onChange={mockOnChange} />);
    
    const errorElement = screen.queryByRole('alert');
    expect(errorElement).toBeNull();
  });

  it('should include data-testid attribute', () => {
    const testId = 'details.sex.select';
    render(<GenderSelector value="" onChange={mockOnChange} testId={testId} />);
    
    const container = screen.getByTestId(testId);
    expect(container).toBeDefined();
  });
});

