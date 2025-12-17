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
          <Route path={AppRoutes.home} element={<Home />} />
          <Route path={AppRoutes.lostPets} element={<LostPets />} />
          <Route path={`${AppRoutes.lostPets}/:announcementId`} element={<LostPets />} />
          <Route path={AppRoutes.foundPets} element={<FoundPets />} />
          <Route path={AppRoutes.contact} element={<Contact />} />
          <Route path={AppRoutes.account} element={<Account />} />

          <Route
            path={AppRoutes.reportMissing.base}
            element={
              <NewAnnouncementFlowProvider>
                <Navigate to={AppRoutes.reportMissing.microchip} replace />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.reportMissing.microchip}
            element={
              <NewAnnouncementFlowProvider>
                <MicrochipNumberScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.reportMissing.photo}
            element={
              <NewAnnouncementFlowProvider>
                <PhotoScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.reportMissing.details}
            element={
              <NewAnnouncementFlowProvider>
                <DetailsScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.reportMissing.contact}
            element={
              <NewAnnouncementFlowProvider>
                <ContactScreen />
              </NewAnnouncementFlowProvider>
            }
          />
          <Route
            path={AppRoutes.reportMissing.summary}
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
