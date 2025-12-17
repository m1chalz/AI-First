import { useNavigate, useParams } from 'react-router-dom';
import { AppRoutes } from '../pages/routes';

export interface UseModalResult {
  isOpen: boolean;
  selectedAnnouncementId: string | null;
  openModal: (announcementId: string) => void;
  closeModal: () => void;
}

export function useModal(): UseModalResult {
  const navigate = useNavigate();
  const params = useParams();
  const announcementId = params.announcementId;

  const isOpen = !!announcementId;
  const selectedAnnouncementId = announcementId || null;

  const openModal = (announcementId: string) => {
    navigate(AppRoutes.lostPetDetails(announcementId));
  };

  const closeModal = () => {
    navigate(AppRoutes.lostPets);
  };

  return {
    isOpen,
    selectedAnnouncementId,
    openModal,
    closeModal
  };
}
