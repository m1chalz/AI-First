import React from 'react';
import { MdLocationOn } from 'react-icons/md';
import { ANIMAL_STATUS_BADGE_COLORS, type Animal, type AnimalSex } from '../../types/animal';
import { formatCoordinates } from '../../utils/coordinate-formatter';
import { formatSpecies } from '../../utils/species-formatter';
import styles from './AnimalList.module.css';
import config from '../../config/config';

interface AnimalCardProps {
    animal: Animal;
    onDetailsClick: (animalId: string) => void;
}

export const AnimalCard: React.FC<AnimalCardProps> = ({ animal, onDetailsClick }) => {
    const statusColor = ANIMAL_STATUS_BADGE_COLORS[animal.status] || '#FF0000';
    
    const getGenderIcon = (sex: AnimalSex): string => {
        if (sex === 'MALE') return '♂';
        if (sex === 'FEMALE') return '♀';
        return '';
    };
    
    return (
        <div
            className={styles.animalCard}
            data-testid={`animalList.item.${animal.id}`}
        >
            {/* Photo placeholder - circular with optional photo */}
            <div className={styles.photoPlaceholder}>
                {animal.photoUrl ? (
                    <img
                        src={`${config.apiBaseUrl}${animal.photoUrl}`}
                        alt={`${animal.petName || 'Pet'} photo`}
                        className={styles.photoImage}
                        loading="lazy"
                    />
                ) : (
                    <span className={styles.photoIcon} aria-hidden="true">🐾</span>
                )}
            </div>
            
            {/* Left section: Location and species/breed info */}
            <div className={styles.animalBasicInfo}>
                <div className={styles.locationRow}>
                    <MdLocationOn className={styles.locationIcon} />
                    <span className={styles.locationText}>
                        {animal.locationLatitude !== null && animal.locationLongitude !== null
                            ? formatCoordinates(animal.locationLatitude, animal.locationLongitude)
                            : 'Location not available'}
                    </span>
                </div>
                
                <div className={styles.speciesRow}>
                    <span className={styles.speciesText}>{formatSpecies(animal.species)}</span>
                    <span className={styles.separator}>|</span>
                    {animal.breed && <span className={styles.breedText}>{animal.breed}</span>}
                    {animal.sex !== 'UNKNOWN' && (
                        <span className={styles.genderIcon}>
                            {getGenderIcon(animal.sex)}
                        </span>
                    )}
                </div>
            </div>
            
            {/* Center section: Description */}
            <div className={styles.animalDescription}>
                {animal.description}
            </div>
            
            {/* Right section: Status and date (left column) + Details button (right column) */}
            <div className={styles.animalStatusSection}>
                <div className={styles.statusDateGroup}>
                    <div
                        className={styles.statusBadge}
                        style={{ backgroundColor: statusColor }}
                    >
                        {animal.status}
                    </div>
                    
                    <div className={styles.animalDate}>
                        {animal.lastSeenDate}
                    </div>
                </div>
                
                <button
                    className={styles.detailsButton}
                    onClick={() => onDetailsClick(animal.id)}
                    data-testid="animalList.card.detailsButton.click"
                >
                    Details
                </button>
            </div>
        </div>
    );
};

