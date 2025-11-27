import React from 'react';
import { ANIMAL_STATUS_BADGE_COLORS, type Animal, type AnimalSex } from '../../types/animal';
import { formatCoordinates } from '../../utils/coordinate-formatter';
import styles from './AnimalList.module.css';

interface AnimalCardProps {
    animal: Animal;
    onClick: () => void;
}

export const AnimalCard: React.FC<AnimalCardProps> = ({ animal, onClick }) => {
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
            onClick={onClick}
        >
            {/* Photo placeholder - circular with optional photo */}
            <div className={styles.photoPlaceholder}>
                {animal.photoUrl ? (
                    <img
                        src={animal.photoUrl}
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
                    <svg className={styles.locationIcon} width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z" fill="#616161"/>
                    </svg>
                    <span className={styles.locationText}>
                        {animal.locationLatitude !== null && animal.locationLongitude !== null
                            ? formatCoordinates(animal.locationLatitude, animal.locationLongitude)
                            : 'Location not available'}
                    </span>
                </div>
                
                <div className={styles.speciesRow}>
                    <span className={styles.speciesText}>{animal.species}</span>
                    <span className={styles.separator}>|</span>
                    <span className={styles.breedText}>{animal.breed || 'Unknown'}</span>
                    {animal.sex !== 'UNKNOWN' && (
                        <span className={styles.genderIcon}>
                            {getGenderIcon(animal.sex)}
                        </span>
                    )}
                </div>
            </div>
            
            {/* Center section: Description */}
            <div className={styles.animalDescription}>
                {animal.description || 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean luctus mattis nulla nec mollis.'}
            </div>
            
            {/* Right section: Status and date */}
            <div className={styles.animalStatusSection}>
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
        </div>
    );
};

