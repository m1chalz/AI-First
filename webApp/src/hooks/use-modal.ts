import { useNavigate, useParams } from 'react-router-dom';

export interface UseModalResult {
  isOpen: boolean;
  selectedPetId: string | null;
  openModal: (petId: string) => void;
  closeModal: () => void;
}

export function useModal(): UseModalResult {
  const navigate = useNavigate();
  const params = useParams();
  const announcementId = params.announcementId;

  const isOpen = !!announcementId;
  const selectedPetId = announcementId || null;

  const openModal = (petId: string) => {
    navigate(`/announcement/${petId}`);
  };

  const closeModal = () => {
    navigate('/');
  };

  return {
    isOpen,
    selectedPetId,
    openModal,
    closeModal
  };
}
