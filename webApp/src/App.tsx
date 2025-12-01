import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AnimalList } from './components/AnimalList/AnimalList';
import { MicrochipNumberScreen } from './components/ReportMissingPet/MicrochipNumberScreen';
import { PhotoScreen } from './components/ReportMissingPet/PhotoScreen';
import { ReportMissingPetFlowProvider } from './contexts/ReportMissingPetFlowContext';

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AnimalList />} />
        
        <Route path="/report-missing" element={<ReportMissingPetFlowProvider><Navigate to="microchip" replace /></ReportMissingPetFlowProvider>} />
        <Route
          path="/report-missing/microchip"
          element={
            <ReportMissingPetFlowProvider>
              <MicrochipNumberScreen />
            </ReportMissingPetFlowProvider>
          }
        />
        <Route
          path="/report-missing/photo"
          element={
            <ReportMissingPetFlowProvider>
              <PhotoScreen />
            </ReportMissingPetFlowProvider>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

