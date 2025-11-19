import React from 'react';
import { useAnimalList } from '../../hooks/useAnimalList';
import { AnimalCard } from './AnimalCard';
import { EmptyState } from './EmptyState';
import styles from './AnimalList.module.css';

/**
 * Main component for displaying list of animals.
 * Uses useAnimalList hook for state management.
 * 
 * Features:
 * - Scrollable list of animal cards
 * - Loading indicator
 * - Error message display
 * - Empty state message
 * - Two buttons at top-right: "Report a Missing Animal" + "Report Found Animal" (per web design)
 * - Reserved space for future search component
 * 
 * Layout per FR-010: This is the primary entry point screen.
 */
export const AnimalList: React.FC = () => {
    const {
        animals,
        isLoading,
        error,
        isEmpty,
        selectAnimal,
        reportMissing,
        reportFound
    } = useAnimalList();
    
    return (
        <div className={styles.container}>
            {/* Header with title and action buttons */}
            <header className={styles.header}>
                <h1 className={styles.title}>Missing animals list</h1>
                
                {/* Two buttons at top-right per web design (Figma 71:9154) */}
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
            
            {/* Reserved space for search component (FR-004) */}
            <div className={styles.searchPlaceholder} data-testid="animalList.searchPlaceholder">
                {/* 64px height per web design, 582px search bar width will go here */}
            </div>
            
            {/* Content area */}
            <div className={styles.content}>
                {isLoading ? (
                    // Loading indicator
                    <div className={styles.loading}>
                        <div className={styles.spinner}></div>
                        <p>Loading animals...</p>
                    </div>
                ) : error ? (
                    // Error message
                    <div className={styles.error}>
                        <p>Error: {error}</p>
                    </div>
                ) : isEmpty ? (
                    // Empty state
                    <EmptyState />
                ) : (
                    // Animal list
                    <div className={styles.animalList} data-testid="animalList.list">
                        {animals.map((animal) => (
                            <AnimalCard
                                key={animal.id}
                                animal={animal}
                                onClick={() => selectAnimal(animal.id)}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

