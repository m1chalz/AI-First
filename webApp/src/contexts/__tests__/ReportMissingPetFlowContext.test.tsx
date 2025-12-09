import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { ReportMissingPetFlowProvider, useReportMissingPetFlow } from '../ReportMissingPetFlowContext';
import { FlowStep } from '../../models/ReportMissingPetFlow';
import { AnimalSpecies, AnimalSex } from '../../types/animal';

describe('ReportMissingPetFlowContext', () => {
  it('provides initial flow state', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider
    });

    // then
    expect(result.current.flowState.currentStep).toBe(FlowStep.Empty);
    expect(result.current.flowState.microchipNumber).toBe('');
    expect(result.current.flowState.photo).toBe(null);
  });

  it('updates microchip number', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider
    });

    // when
    act(() => {
      result.current.updateFlowState({ microchipNumber: '123456789012345' });
    });

    // then
    expect(result.current.flowState.microchipNumber).toBe('123456789012345');
    expect(result.current.flowState.photo).toBe(null);
  });

  it('updates current step', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider
    });

    // when
    act(() => {
      result.current.updateFlowState({ currentStep: FlowStep.Photo });
    });

    // then
    expect(result.current.flowState.currentStep).toBe(FlowStep.Photo);
  });

  it('updates multiple fields at once', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider
    });

    // when
    act(() => {
      result.current.updateFlowState({
        microchipNumber: '123456789012345',
        currentStep: FlowStep.Photo
      });
    });

    // then
    expect(result.current.flowState.microchipNumber).toBe('123456789012345');
    expect(result.current.flowState.currentStep).toBe(FlowStep.Photo);
  });

  it('updates photo state', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider
    });
    const mockPhoto = {
      file: new File(['test'], 'test.jpg', { type: 'image/jpeg' }),
      filename: 'test.jpg',
      size: 1024,
      mimeType: 'image/jpeg',
      previewUrl: 'blob:mock-url'
    };

    // when
    act(() => {
      result.current.updateFlowState({ photo: mockPhoto });
    });

    // then
    expect(result.current.flowState.photo).toEqual(mockPhoto);
  });

  it('clears flow state to initial values', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider
    });
    const mockPhoto = {
      file: new File(['test'], 'test.jpg', { type: 'image/jpeg' }),
      filename: 'test.jpg',
      size: 1024,
      mimeType: 'image/jpeg',
      previewUrl: 'blob:mock-url'
    };

    act(() => {
      result.current.updateFlowState({
        microchipNumber: '123456789012345',
        currentStep: FlowStep.Photo,
        photo: mockPhoto
      });
    });

    // when
    act(() => {
      result.current.clearFlowState();
    });

    // then
    expect(result.current.flowState.currentStep).toBe(FlowStep.Empty);
    expect(result.current.flowState.microchipNumber).toBe('');
    expect(result.current.flowState.photo).toBe(null);
  });

  it('throws error when hook used outside provider', () => {
    // given
    const consoleError = console.error;
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    console.error = () => {};

    // when / then
    expect(() => {
      renderHook(() => useReportMissingPetFlow());
    }).toThrow('useReportMissingPetFlow must be used within ReportMissingPetFlowProvider');

    console.error = consoleError;
  });

  describe('Step 3 fields (Animal Description)', () => {
    it('should include lastSeenDate, species, breed, sex, age, description, latitude, longitude in flow state', () => {
      // given
      const { result } = renderHook(() => useReportMissingPetFlow(), {
        wrapper: ReportMissingPetFlowProvider
      });

      // then
      expect(result.current.flowState).toHaveProperty('lastSeenDate');
      expect(result.current.flowState).toHaveProperty('species');
      expect(result.current.flowState).toHaveProperty('breed');
      expect(result.current.flowState).toHaveProperty('sex');
      expect(result.current.flowState).toHaveProperty('age');
      expect(result.current.flowState).toHaveProperty('description');
      expect(result.current.flowState).toHaveProperty('latitude');
      expect(result.current.flowState).toHaveProperty('longitude');
    });

    it('should initialize Step 3 fields with correct defaults', () => {
      // given
      const { result } = renderHook(() => useReportMissingPetFlow(), {
        wrapper: ReportMissingPetFlowProvider
      });

      // then
      const today = new Date().toISOString().split('T')[0];
      expect(result.current.flowState.lastSeenDate).toBe(today);
      expect(result.current.flowState.species).toBeNull();
      expect(result.current.flowState.breed).toBe('');
      expect(result.current.flowState.sex).toBeNull();
      expect(result.current.flowState.age).toBeNull();
      expect(result.current.flowState.description).toBe('');
      expect(result.current.flowState.latitude).toBeNull();
      expect(result.current.flowState.longitude).toBeNull();
    });

    it.each([
      ['lastSeenDate', '2025-12-01', '2025-12-01'],
      ['species', 'DOG' as AnimalSpecies, 'DOG'],
      ['breed', 'Golden Retriever', 'Golden Retriever'],
      ['sex', 'MALE' as AnimalSex, 'MALE'],
      ['age', 5, 5],
      ['description', 'Friendly dog', 'Friendly dog'],
      ['latitude', 52.52, 52.52],
      ['longitude', 13.405, 13.405]
    ])('should update %s field', (field, updateValue, expectedValue) => {
      // given
      const { result } = renderHook(() => useReportMissingPetFlow(), {
        wrapper: ReportMissingPetFlowProvider
      });

      // when
      act(() => result.current.updateFlowState({ [field]: updateValue }));

      // then
      expect(result.current.flowState[field as keyof typeof result.current.flowState]).toBe(expectedValue);
    });

    it('should update multiple Step 3 fields at once', () => {
      // given
      const { result } = renderHook(() => useReportMissingPetFlow(), {
        wrapper: ReportMissingPetFlowProvider
      });

      // when
      act(() => {
        result.current.updateFlowState({
          lastSeenDate: '2025-12-01',
          species: 'DOG' as AnimalSpecies,
          breed: 'Golden Retriever',
          sex: 'MALE' as AnimalSex,
          age: 5,
          description: 'Friendly dog',
          latitude: 52.52,
          longitude: 13.405,
          currentStep: FlowStep.Contact
        });
      });

      // then
      expect(result.current.flowState.lastSeenDate).toBe('2025-12-01');
      expect(result.current.flowState.species).toBe('DOG');
      expect(result.current.flowState.breed).toBe('Golden Retriever');
      expect(result.current.flowState.sex).toBe('MALE');
      expect(result.current.flowState.age).toBe(5);
      expect(result.current.flowState.description).toBe('Friendly dog');
      expect(result.current.flowState.latitude).toBe(52.52);
      expect(result.current.flowState.longitude).toBe(13.405);
      expect(result.current.flowState.currentStep).toBe(FlowStep.Contact);
    });
  });
});
