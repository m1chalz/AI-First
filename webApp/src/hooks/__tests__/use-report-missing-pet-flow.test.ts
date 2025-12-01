import { describe, it, expect } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useReportMissingPetFlow } from '../use-report-missing-pet-flow';
import { ReportMissingPetFlowProvider } from '../../contexts/ReportMissingPetFlowContext';

describe('useReportMissingPetFlow', () => {
  it('returns context value when used within provider', () => {
    // given / when
    const { result } = renderHook(() => useReportMissingPetFlow(), {
      wrapper: ReportMissingPetFlowProvider,
    });

    // then
    expect(result.current.flowState).toBeDefined();
    expect(result.current.updateFlowState).toBeDefined();
    expect(result.current.clearFlowState).toBeDefined();
  });

  it('throws error when used outside provider', () => {
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

