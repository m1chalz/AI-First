import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { MicrochipNumberScreen } from './components/NewAnnouncement/MicrochipNumberScreen';
import { PhotoScreen } from './components/NewAnnouncement/PhotoScreen';
import { DetailsScreen } from './components/NewAnnouncement/DetailsScreen';
import { ContactScreen } from './components/NewAnnouncement/ContactScreen';
import { SummaryScreen } from './components/NewAnnouncement/SummaryScreen';
import { NavigationBar } from './components/NavigationBar';
import { NewAnnouncementFlowProvider } from './contexts/NewAnnouncementFlowContext';
import { GeolocationProvider } from './contexts/GeolocationContext';
import { Home } from './pages/Home';
import { LostPets } from './pages/LostPets';
import { FoundPets } from './pages/FoundPets';
import { Contact } from './pages/Contact';
import { Account } from './pages/Account';
import { AppRoutes } from './pages/routes';

export function App() {
  return (
    <GeolocationProvider>
      <BrowserRouter>
        <NavigationBar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/lost-pets" element={<LostPets />} />
          <Route path="/lost-pets/:announcementId" element={<LostPets />} />
          <Route path="/found-pets" element={<FoundPets />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/account" element={<Account />} />
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
