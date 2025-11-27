import { useState } from 'react';

export interface UseModalResult {
    isOpen: boolean;
    selectedPetId: string | null;
    openModal: (petId: string) => void;
    closeModal: () => void;
}

export function useModal(): UseModalResult {
    const [isOpen, setIsOpen] = useState(false);
    const [selectedPetId, setSelectedPetId] = useState<string | null>(null);
    
    const openModal = (petId: string) => {
        setSelectedPetId(petId);
        setIsOpen(true);
    };
    
    const closeModal = () => {
        setIsOpen(false);
        setSelectedPetId(null);
    };
    
    return {
        isOpen,
        selectedPetId,
        openModal,
        closeModal
    };
}

