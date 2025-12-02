import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { SpeciesDropdown } from '../SpeciesDropdown';

describe('SpeciesDropdown', () => {
  const mockOnChange = vi.fn();

  it('should render all 5 species options', () => {
    render(<SpeciesDropdown value="" onChange={mockOnChange} />);
    
    const select = screen.getByRole('combobox');
    expect(select).toBeDefined();
    
    const options = screen.getAllByRole('option');
    expect(options).toHaveLength(6);
  });

  it.each(['Dog', 'Cat', 'Bird', 'Rabbit', 'Other'])(
    'should display %s as capitalized label',
    (label) => {
      render(<SpeciesDropdown value="" onChange={mockOnChange} />);
      
      const element = screen.getByText(label);
      expect(element).toBeDefined();
    }
  );

  it('should call onChange with correct enum value when option selected', () => {
    render(<SpeciesDropdown value="" onChange={mockOnChange} />);
    
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: 'DOG' } });
    
    expect(mockOnChange).toHaveBeenCalledWith('DOG');
  });

  it('should display error message when provided', () => {
    const errorMessage = 'Please select a species';
    render(<SpeciesDropdown value="" onChange={mockOnChange} error={errorMessage} />);
    
    const element = screen.getByText(errorMessage);
    expect(element).toBeDefined();
  });

  it('should not display error message when not provided', () => {
    render(<SpeciesDropdown value="" onChange={mockOnChange} />);
    
    const errorElement = screen.queryByRole('alert');
    expect(errorElement).toBeNull();
  });

  it('should include data-testid attribute', () => {
    const testId = 'details.species.select';
    render(<SpeciesDropdown value="" onChange={mockOnChange} testId={testId} />);
    
    const select = screen.getByRole('combobox');
    expect(select.getAttribute('data-testid')).toBe(testId);
  });

  it('should set selected value', () => {
    render(<SpeciesDropdown value="DOG" onChange={mockOnChange} />);
    
    const select = screen.getByRole('combobox') as HTMLSelectElement;
    expect(select.value).toBe('DOG');
  });
});

