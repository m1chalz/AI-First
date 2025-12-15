import { createContext, useContext, useState, ReactNode } from 'react';
import { NewAnnouncementFlowState, NewAnnouncementFlowContextValue, initialFlowState } from '../models/NewAnnouncementFlow';

const NewAnnouncementFlowContext = createContext<NewAnnouncementFlowContextValue | null>(null);

export function NewAnnouncementFlowProvider({ children }: { children: ReactNode }) {
  const [flowState, setFlowState] = useState<NewAnnouncementFlowState>(initialFlowState);

  const updateFlowState = (updates: Partial<NewAnnouncementFlowState>) => {
    setFlowState((prev) => ({ ...prev, ...updates }));
  };

  const clearFlowState = () => {
    setFlowState(initialFlowState);
  };

  return (
    <NewAnnouncementFlowContext.Provider value={{ flowState, updateFlowState, clearFlowState }}>
      {children}
    </NewAnnouncementFlowContext.Provider>
  );
}

export function useNewAnnouncementFlow(): NewAnnouncementFlowContextValue {
  const context = useContext(NewAnnouncementFlowContext);

  if (!context) {
    throw new Error('useNewAnnouncementFlow must be used within NewAnnouncementFlowProvider');
  }

  return context;
}
