import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AnimalList } from './components/AnimalList/AnimalList';
import { MicrochipNumberScreen } from './components/ReportMissingPet/MicrochipNumberScreen';
import { PhotoScreen } from './components/ReportMissingPet/PhotoScreen';
import { DetailsScreen } from './components/ReportMissingPet/DetailsScreen';
import { ContactScreen } from './components/ReportMissingPet/ContactScreen';
import { SummaryScreen } from './components/ReportMissingPet/SummaryScreen';
import { ReportMissingPetFlowProvider } from './contexts/ReportMissingPetFlowContext';
import { GeolocationProvider } from './contexts/GeolocationContext';

export function App() {
  return (
    <GeolocationProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<AnimalList />} />
          <Route path="/announcement/:announcementId" element={<AnimalList />} />
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
        <Route
          path="/report-missing/details"
          element={
            <ReportMissingPetFlowProvider>
              <DetailsScreen />
            </ReportMissingPetFlowProvider>
          }
        />
        <Route
          path="/report-missing/contact"
          element={
            <ReportMissingPetFlowProvider>
              <ContactScreen />
            </ReportMissingPetFlowProvider>
          }
        />
        <Route
          path="/report-missing/summary"
          element={
            <ReportMissingPetFlowProvider>
              <SummaryScreen />
            </ReportMissingPetFlowProvider>
          }
        />
        </Routes>
      </BrowserRouter>
    </GeolocationProvider>
  );
}

