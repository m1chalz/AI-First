import React from 'react';
import { ANIMAL_STATUS_BADGE_COLORS, type Animal } from '../../types/animal';
import styles from './AnimalList.module.css';

interface AnimalCardProps {
    animal: Animal;
    onClick: () => void;
}

export const AnimalCard: React.FC<AnimalCardProps> = ({ animal, onClick }) => {
    const statusColor = ANIMAL_STATUS_BADGE_COLORS[animal.status] || '#FF0000';
    
    return (
        <div
            className={styles.animalCard}
            data-testid={`animalList.item.${animal.id}`}
            onClick={onClick}
        >
            <div className={styles.photoPlaceholder}>
                {animal.species.charAt(0)}
            </div>
            
            <div className={styles.animalInfo}>
                <div className={styles.animalName}>
                    {animal.species} | {animal.breed}
                </div>
                
                <div className={styles.animalLocation}>
                    {animal.location.city}, +{animal.location.radiusKm}km
                </div>
                
                <div
                    className={styles.statusBadge}
                    style={{ backgroundColor: statusColor }}
                >
                    {animal.status}
                </div>
                
                <div className={styles.animalDate}>
                    Last seen: {animal.lastSeenDate}
                </div>
            </div>
        </div>
    );
};

