import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AnnouncementList } from './components/AnnouncementList/AnnouncementList';
import { MicrochipNumberScreen } from './components/NewAnnouncement/MicrochipNumberScreen';
import { PhotoScreen } from './components/NewAnnouncement/PhotoScreen';
import { DetailsScreen } from './components/NewAnnouncement/DetailsScreen';
import { ContactScreen } from './components/NewAnnouncement/ContactScreen';
import { SummaryScreen } from './components/NewAnnouncement/SummaryScreen';
import { NewAnnouncementFlowProvider } from './contexts/NewAnnouncementFlowContext';
import { GeolocationProvider } from './contexts/GeolocationContext';
import { AppRoutes } from './routes/routes';

export function App() {
  return (
    <GeolocationProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<AnnouncementList />} />
          <Route path="/announcement/:announcementId" element={<AnnouncementList />} />
          <Route
            path={AppRoutes.base}
            element={
              <NewAnnouncementFlowProvider>
                <Navigate to={AppRoutes.microchip} replace />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.microchip}
            element={
              <NewAnnouncementFlowProvider>
                <MicrochipNumberScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.photo}
            element={
              <NewAnnouncementFlowProvider>
                <PhotoScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.details}
            element={
              <NewAnnouncementFlowProvider>
                <DetailsScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.contact}
            element={
              <NewAnnouncementFlowProvider>
                <ContactScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.summary}
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
