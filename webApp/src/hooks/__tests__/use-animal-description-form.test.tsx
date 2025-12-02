import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { ReactNode } from 'react';
import { useAnimalDescriptionForm } from '../use-animal-description-form';
import { ReportMissingPetFlowProvider } from '../../contexts/ReportMissingPetFlowContext';

const wrapper = ({ children }: { children: ReactNode }) => (
  <ReportMissingPetFlowProvider>{children}</ReportMissingPetFlowProvider>
);

describe('useAnimalDescriptionForm', () => {
  describe('initialization', () => {
    it('should initialize with default values', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      const today = new Date().toISOString().split('T')[0];
      expect(result.current.formData.lastSeenDate).toBe(today);
      expect(result.current.formData.species).toBe('');
      expect(result.current.formData.breed).toBe('');
      expect(result.current.formData.sex).toBe('');
      expect(result.current.formData.age).toBe('');
      expect(result.current.formData.description).toBe('');
      expect(result.current.formData.latitude).toBe('');
      expect(result.current.formData.longitude).toBe('');
    });

    it('should initialize validationErrors as empty object', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      expect(result.current.formData.validationErrors).toEqual({});
    });
  });

  describe('field updates', () => {
    it('should update lastSeenDate', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('lastSeenDate', '2025-12-01');
      });
      
      expect(result.current.formData.lastSeenDate).toBe('2025-12-01');
    });

    it('should update species', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('species', 'DOG');
      });
      
      expect(result.current.formData.species).toBe('DOG');
    });

    it('should update breed', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('breed', 'Golden Retriever');
      });
      
      expect(result.current.formData.breed).toBe('Golden Retriever');
    });

    it('should update sex', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('sex', 'MALE');
      });
      
      expect(result.current.formData.sex).toBe('MALE');
    });

    it('should update age', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('age', '5');
      });
      
      expect(result.current.formData.age).toBe('5');
    });

    it('should update description', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('description', 'Friendly dog');
      });
      
      expect(result.current.formData.description).toBe('Friendly dog');
    });

    it('should update latitude', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('latitude', '52.5200');
      });
      
      expect(result.current.formData.latitude).toBe('52.5200');
    });

    it('should update longitude', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('longitude', '13.4050');
      });
      
      expect(result.current.formData.longitude).toBe('13.4050');
    });
  });

  describe('breed field clearing', () => {
    it('should clear breed when species changes', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('species', 'DOG');
      });
      
      act(() => {
        result.current.updateField('breed', 'Golden Retriever');
      });
      
      expect(result.current.formData.breed).toBe('Golden Retriever');
      
      act(() => {
        result.current.updateField('species', 'CAT');
      });
      
      expect(result.current.formData.breed).toBe('');
    });

    it('should not clear breed when updating other fields', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('species', 'DOG');
      });
      
      act(() => {
        result.current.updateField('breed', 'Golden Retriever');
      });
      
      act(() => {
        result.current.updateField('sex', 'MALE');
      });
      
      expect(result.current.formData.breed).toBe('Golden Retriever');
    });
  });

  describe('handleSubmit', () => {
    it('should return false when required fields are missing', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      let submitResult: boolean = true;
      act(() => {
        submitResult = result.current.handleSubmit();
      });
      
      expect(submitResult).toBe(false);
    });

    it('should set validation errors when form is invalid', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.handleSubmit();
      });
      
      expect(Object.keys(result.current.formData.validationErrors).length).toBeGreaterThan(0);
    });

    it('should return true when all required fields are valid', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('lastSeenDate', '2025-12-01');
        result.current.updateField('species', 'DOG');
        result.current.updateField('breed', 'Golden Retriever');
        result.current.updateField('sex', 'MALE');
      });
      
      let submitResult: boolean = false;
      act(() => {
        submitResult = result.current.handleSubmit();
      });
      
      expect(submitResult).toBe(true);
    });

    it('should clear validation errors when form is valid', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.handleSubmit();
      });
      
      expect(Object.keys(result.current.formData.validationErrors).length).toBeGreaterThan(0);
      
      act(() => {
        result.current.updateField('lastSeenDate', '2025-12-01');
        result.current.updateField('species', 'DOG');
        result.current.updateField('breed', 'Golden Retriever');
        result.current.updateField('sex', 'MALE');
      });
      
      act(() => {
        result.current.handleSubmit();
      });
      
      expect(result.current.formData.validationErrors).toEqual({});
    });

    it('should save data to flow state on valid submission', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('lastSeenDate', '2025-12-01');
        result.current.updateField('species', 'DOG');
        result.current.updateField('breed', 'Golden Retriever');
        result.current.updateField('sex', 'MALE');
        result.current.updateField('age', '5');
        result.current.updateField('description', 'Friendly dog');
        result.current.updateField('latitude', '52.5200');
        result.current.updateField('longitude', '13.4050');
      });
      
      act(() => {
        result.current.handleSubmit();
      });
      
      expect(result.current.flowState.lastSeenDate).toBe('2025-12-01');
      expect(result.current.flowState.species).toBe('DOG');
      expect(result.current.flowState.breed).toBe('Golden Retriever');
      expect(result.current.flowState.sex).toBe('MALE');
      expect(result.current.flowState.age).toBe(5);
      expect(result.current.flowState.description).toBe('Friendly dog');
      expect(result.current.flowState.latitude).toBe(52.5200);
      expect(result.current.flowState.longitude).toBe(13.4050);
    });

    it('should save null latitude/longitude when empty strings', () => {
      const { result } = renderHook(() => useAnimalDescriptionForm(), { wrapper });
      
      act(() => {
        result.current.updateField('lastSeenDate', '2025-12-01');
        result.current.updateField('species', 'DOG');
        result.current.updateField('breed', 'Golden Retriever');
        result.current.updateField('sex', 'MALE');
        result.current.updateField('latitude', '');
        result.current.updateField('longitude', '');
      });
      
      act(() => {
        result.current.handleSubmit();
      });
      
      expect(result.current.flowState.latitude).toBeNull();
      expect(result.current.flowState.longitude).toBeNull();
    });
  });
});

