import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { AnimalDescriptionForm } from '../AnimalDescriptionForm';

describe('AnimalDescriptionForm', () => {
  const mockOnSubmit = vi.fn();
  const defaultFormData = {
    lastSeenDate: '2025-12-01',
    species: '',
    breed: '',
    sex: '',
    age: '',
    description: '',
    latitude: '',
    longitude: '',
    validationErrors: {}
  };

  it('should render date input field', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const dateInput = screen.getByTestId('details.lastSeenDate.input');
    expect(dateInput).toBeDefined();
    expect(dateInput.getAttribute('type')).toBe('date');
  });

  it('should render species dropdown', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const speciesDropdown = screen.getByTestId('details.species.select');
    expect(speciesDropdown).toBeDefined();
  });

  it('should render breed input field', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const breedInput = screen.getByTestId('details.breed.input');
    expect(breedInput).toBeDefined();
  });

  it('should render gender selector', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const genderSelector = screen.getByTestId('details.sex.select');
    expect(genderSelector).toBeDefined();
  });

  it('should render age input field', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const ageInput = screen.getByTestId('details.age.input');
    expect(ageInput).toBeDefined();
    expect(ageInput.getAttribute('type')).toBe('number');
  });

  it('should render description textarea', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const textarea = screen.getByTestId('details.description.textarea');
    expect(textarea).toBeDefined();
    expect(textarea.getAttribute('maxLength')).toBe('500');
  });

  it('should render disabled GPS button', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const gpsButton = screen.getByTestId('details.gpsButton.click');
    expect(gpsButton).toBeDefined();
    expect((gpsButton as HTMLButtonElement).disabled).toBe(true);
  });

  it('should render Continue button', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const continueButton = screen.getByTestId('details.continue.click');
    expect(continueButton).toBeDefined();
  });

  it('should disable breed field when species is not selected', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const breedInput = screen.getByTestId('details.breed.input') as HTMLInputElement;
    expect(breedInput.disabled).toBe(true);
  });

  it('should enable breed field when species is selected', () => {
    const formDataWithSpecies = { ...defaultFormData, species: 'DOG' };
    
    render(
      <AnimalDescriptionForm
        formData={formDataWithSpecies}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const breedInput = screen.getByTestId('details.breed.input') as HTMLInputElement;
    expect(breedInput.disabled).toBe(false);
  });

  it('should set max attribute to today on date picker', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const dateInput = screen.getByTestId('details.lastSeenDate.input');
    const today = new Date().toISOString().split('T')[0];
    expect(dateInput.getAttribute('max')).toBe(today);
  });

  it('should call onSubmit when Continue button is clicked', () => {
    render(
      <AnimalDescriptionForm
        formData={defaultFormData}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    const continueButton = screen.getByTestId('details.continue.click');
    fireEvent.click(continueButton);
    
    expect(mockOnSubmit).toHaveBeenCalled();
  });

  it('should display character counter for description', () => {
    const formDataWithDescription = { ...defaultFormData, description: 'Test description' };
    
    render(
      <AnimalDescriptionForm
        formData={formDataWithDescription}
        onFieldChange={vi.fn()}
        onSubmit={mockOnSubmit}
      />
    );
    
    expect(screen.getByText(/16\/500 characters/)).toBeDefined();
  });
});

