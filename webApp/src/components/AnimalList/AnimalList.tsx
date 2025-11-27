import React from 'react';
import { useAnimalList } from '../../hooks/use-animal-list';
import { useModal } from '../../hooks/use-modal';
import { AnimalCard } from './AnimalCard';
import { EmptyState } from './EmptyState';
import { PetDetailsModal } from '../PetDetailsModal/PetDetailsModal';
import styles from './AnimalList.module.css';

export const AnimalList: React.FC = () => {
    const {
        animals,
        isLoading,
        error,
        isEmpty,
        reportMissing,
        reportFound
    } = useAnimalList();
    
    const { isOpen, selectedPetId, openModal, closeModal } = useModal();
    
    return (
        <div className={styles.container}>
            <div className={styles.mainContent}>
                <header className={styles.header}>
                    <h1 className={styles.title}>Missing animals list</h1>
                    
                    <div className={styles.headerButtons}>
                        <button
                            className={styles.primaryButton}
                            onClick={reportMissing}
                            data-testid="animalList.reportMissingButton"
                        >
                            Report a Missing Animal
                        </button>
                        <button
                            className={styles.secondaryButton}
                            onClick={reportFound}
                            data-testid="animalList.reportFoundButton"
                        >
                            Report Found Animal
                        </button>
                    </div>
                </header>
                
                <div className={styles.content}>
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

