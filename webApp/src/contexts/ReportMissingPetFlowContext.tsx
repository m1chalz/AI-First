import { createContext, useContext, useState, ReactNode } from 'react';
import { ReportMissingPetFlowState, ReportMissingPetFlowContextValue, initialFlowState } from '../models/ReportMissingPetFlow';

const ReportMissingPetFlowContext = createContext<ReportMissingPetFlowContextValue | null>(null);

export function ReportMissingPetFlowProvider({ children }: { children: ReactNode }) {
  const [flowState, setFlowState] = useState<ReportMissingPetFlowState>(initialFlowState);

  const updateFlowState = (updates: Partial<ReportMissingPetFlowState>) => {
    setFlowState((prev) => ({ ...prev, ...updates }));
  };

  const clearFlowState = () => {
    setFlowState(initialFlowState);
  };

  return (
    <ReportMissingPetFlowContext.Provider value={{ flowState, updateFlowState, clearFlowState }}>
      {children}
    </ReportMissingPetFlowContext.Provider>
  );
}

export function useReportMissingPetFlow(): ReportMissingPetFlowContextValue {
  const context = useContext(ReportMissingPetFlowContext);

  if (!context) {
    throw new Error('useReportMissingPetFlow must be used within ReportMissingPetFlowProvider');
  }

  return context;
}
