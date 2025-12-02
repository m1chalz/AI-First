import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { 
  ReportMissingPetFlowProvider, 
  useReportMissingPetFlow 
} from '../ReportMissingPetFlowContext';
import { FlowStep } from '../../models/ReportMissingPetFlow';

describe('ReportMissingPetFlowContext', () => {
  it('provides initial flow state', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider,
    });

    // then
    expect(result.current.flowState.currentStep).toBe(FlowStep.Microchip);
    expect(result.current.flowState.microchipNumber).toBe('');
    expect(result.current.flowState.photo).toBe(null);
  });

  it('updates microchip number', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider,
    });

    // when
    act(() => {
      result.current.updateFlowState({ microchipNumber: '123456789012345' });
    });

    // then
    expect(result.current.flowState.microchipNumber).toBe('123456789012345');
    expect(result.current.flowState.currentStep).toBe(FlowStep.Microchip);
    expect(result.current.flowState.photo).toBe(null);
  });

  it('updates current step', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider,
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
      wrapper: ReportMissingPetFlowProvider,
    });

    // when
    act(() => {
      result.current.updateFlowState({
        microchipNumber: '123456789012345',
        currentStep: FlowStep.Photo,
      });
    });

    // then
    expect(result.current.flowState.microchipNumber).toBe('123456789012345');
    expect(result.current.flowState.currentStep).toBe(FlowStep.Photo);
  });

  it('updates photo state', () => {
    // given
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider,
    });
    const mockPhoto = {
      file: new File(['test'], 'test.jpg', { type: 'image/jpeg' }),
      filename: 'test.jpg',
      size: 1024,
      mimeType: 'image/jpeg',
      previewUrl: 'blob:mock-url',
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
      wrapper: ReportMissingPetFlowProvider,
    });
    const mockPhoto = {
      file: new File(['test'], 'test.jpg', { type: 'image/jpeg' }),
      filename: 'test.jpg',
      size: 1024,
      mimeType: 'image/jpeg',
      previewUrl: 'blob:mock-url',
    };

    act(() => {
      result.current.updateFlowState({
        microchipNumber: '123456789012345',
        currentStep: FlowStep.Photo,
        photo: mockPhoto,
      });
    });

    // when
    act(() => {
      result.current.clearFlowState();
    });

    // then
    expect(result.current.flowState.currentStep).toBe(FlowStep.Microchip);
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
});

