import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAnimalList } from '../../hooks/use-animal-list';
import { useModal } from '../../hooks/use-modal';
import { AnimalCard } from './AnimalCard';
import { EmptyState } from './EmptyState';
import { LocationBanner } from '../LocationBanner/LocationBanner';
import { PetDetailsModal } from '../PetDetailsModal/PetDetailsModal';
import styles from './AnimalList.module.css';

export const AnimalList: React.FC = () => {
    const navigate = useNavigate();
    const {
        animals,
        isLoading,
        error,
        isEmpty,
        geolocationError
    } = useAnimalList();
    
    const { isOpen, selectedPetId, openModal, closeModal } = useModal();
    const [isBannerDismissed, setIsBannerDismissed] = useState(false);
    
    const showLocationBanner = geolocationError?.code === 1 && !isBannerDismissed;
    
    return (
        <div className={styles.container}>
            <div className={styles.mainContent}>
                <header className={styles.header}>
                    <h1 className={styles.title}>PetSpot</h1>
                    
                    <div className={styles.headerButtons}>
                        <button
                            className={styles.primaryButton}
                            onClick={() => navigate('/report-missing/microchip')}
                            data-testid="animalList.reportMissingButton"
                        >
                            Report a Missing Animal
                        </button>
                    </div>
                </header>
                
                <div className={styles.content}>
                
                    {showLocationBanner && (
                        <LocationBanner onClose={() => setIsBannerDismissed(true)} />
                    )}
                    {isLoading ? (
                        <div className={styles.loading}>
                            <div className={styles.spinner}></div>
                            <p>Loading animals...</p>
                        </div>
                    ) : error ? (
                        <div className={styles.error}>
                            <p>Error: {error}</p>
                        </div>
                    ) : isEmpty ? (
                        <EmptyState />
                    ) : (
                        <div className={styles.animalList} data-testid="animalList.list">
                            {animals.map((animal) => (
                                <AnimalCard
                                    key={animal.id}
                                    animal={animal}
                                    onDetailsClick={() => openModal(animal.id)}
                                />
                            ))}
                        </div>
                    )}
                </div>
            </div>
            
            <PetDetailsModal
                isOpen={isOpen}
                selectedPetId={selectedPetId}
                onClose={closeModal}
            />
        </div>
    );
};

