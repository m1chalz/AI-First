import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AnnouncementList } from './components/AnnouncementList/AnnouncementList';
import { MicrochipNumberScreen } from './components/ReportMissingPet/MicrochipNumberScreen';
import { PhotoScreen } from './components/ReportMissingPet/PhotoScreen';
import { DetailsScreen } from './components/ReportMissingPet/DetailsScreen';
import { ContactScreen } from './components/ReportMissingPet/ContactScreen';
import { SummaryScreen } from './components/ReportMissingPet/SummaryScreen';
import { NewAnnouncementFlowProvider } from './contexts/NewAnnouncementFlowContext';
import { GeolocationProvider } from './contexts/GeolocationContext';

export function App() {
  return (
    <GeolocationProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<AnnouncementList />} />
          <Route path="/announcement/:announcementId" element={<AnnouncementList />} />
          <Route
            path="/report-missing"
            element={
              <NewAnnouncementFlowProvider>
                <Navigate to="microchip" replace />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path="/report-missing/microchip"
            element={
              <NewAnnouncementFlowProvider>
                <MicrochipNumberScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path="/report-missing/photo"
            element={
              <NewAnnouncementFlowProvider>
                <PhotoScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path="/report-missing/details"
            element={
              <NewAnnouncementFlowProvider>
                <DetailsScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path="/report-missing/contact"
            element={
              <NewAnnouncementFlowProvider>
                <ContactScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path="/report-missing/summary"
            element={
              <NewAnnouncementFlowProvider>
                <SummaryScreen />
              </NewAnnouncementFlowProvider>
            }
          />
        </Routes>
      </BrowserRouter>
    </GeolocationProvider>
  );
}
