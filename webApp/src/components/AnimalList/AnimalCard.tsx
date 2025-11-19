import React from 'react';
import type { Animal } from '../../../../shared/build/js/packages/shared/kotlin/shared';
import styles from './AnimalList.module.css';

/**
 * Props for AnimalCard component.
 */
interface AnimalCardProps {
    animal: Animal;
    onClick: () => void;
}

/**
 * Component for displaying a single animal card in the list.
 * Shows animal photo placeholder, species, breed, location, status badge, and date.
 * 
 * Design matches Figma specifications:
 * - Card border radius: 4px
 * - Card shadow: 0px 1px 4px 0px rgba(0,0,0,0.05)
 * - Padding: 16px horizontal
 * - Image placeholder: 63px circular
 * - Status badge radius: 10px
 * 
 * @param props - Component props
 */
export const AnimalCard: React.FC<AnimalCardProps> = ({ animal, onClick }) => {
    // Get status badge color from Kotlin enum
    const statusColor = animal.status.badgeColor || '#FF0000';
    
    return (
        <div
            className={styles.animalCard}
            data-testid={`animalList.item.${animal.id}`}
            onClick={onClick}
        >
            {/* Photo placeholder (63px circular) */}
            <div className={styles.photoPlaceholder}>
                {animal.species.displayName.charAt(0)}
            </div>
            
            {/* Animal info column */}
            <div className={styles.animalInfo}>
                {/* Species | Breed */}
                <div className={styles.animalName}>
                    {animal.species.displayName} | {animal.breed}
                </div>
                
                {/* Location */}
                <div className={styles.animalLocation}>
                    {animal.location.city}, +{animal.location.radiusKm}km
                </div>
                
                {/* Status badge */}
                <div
                    className={styles.statusBadge}
                    style={{ backgroundColor: statusColor }}
                >
                    {animal.status.displayName}
                </div>
                
                {/* Last seen date */}
                <div className={styles.animalDate}>
                    Last seen: {animal.lastSeenDate}
                </div>
            </div>
        </div>
    );
};

