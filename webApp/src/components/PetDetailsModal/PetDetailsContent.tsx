import React from 'react';
import type { Animal } from '../../types/animal';
import styles from './PetDetailsContent.module.css';

interface PetDetailsContentProps {
    pet: Animal;
}

export const PetDetailsContent: React.FC<PetDetailsContentProps> = ({ pet }) => {
    return (
        <div className={styles.content}>
            <h2 id="pet-details-title" className={styles.title}>
                {pet.petName || 'Pet Details'}
            </h2>
            
            {pet.photoUrl && (
                <div className={styles.photoSection}>
                    <img
                        src={pet.photoUrl}
                        alt={pet.petName ? `${pet.petName} photo` : 'Pet photo'}
                        className={styles.photo}
                    />
                </div>
            )}
            
            <div className={styles.detailsSection}>
                <div className={styles.detailRow}>
                    <span className={styles.label}>Status:</span>
                    <span className={styles.value}>{pet.status}</span>
                </div>
                
                <div className={styles.detailRow}>
                    <span className={styles.label}>Species:</span>
                    <span className={styles.value}>{pet.species}</span>
                </div>
                
                {pet.breed && (
                    <div className={styles.detailRow}>
                        <span className={styles.label}>Breed:</span>
                        <span className={styles.value}>{pet.breed}</span>
                    </div>
                )}
                
                <div className={styles.detailRow}>
                    <span className={styles.label}>Sex:</span>
                    <span className={styles.value}>{pet.sex}</span>
                </div>
                
                {pet.age !== null && (
                    <div className={styles.detailRow}>
                        <span className={styles.label}>Age:</span>
                        <span className={styles.value}>{pet.age} years</span>
                    </div>
                )}
                
                <div className={styles.detailRow}>
                    <span className={styles.label}>Last Seen:</span>
                    <span className={styles.value}>{pet.lastSeenDate}</span>
                </div>
                
                {pet.description && (
                    <div className={styles.detailRow}>
                        <span className={styles.label}>Description:</span>
                        <span className={styles.value}>{pet.description}</span>
                    </div>
                )}
                
                {pet.phone && (
                    <div className={styles.detailRow}>
                        <span className={styles.label}>Phone:</span>
                        <span className={styles.value}>{pet.phone}</span>
                    </div>
                )}
                
                {pet.email && (
                    <div className={styles.detailRow}>
                        <span className={styles.label}>Email:</span>
                        <span className={styles.value}>{pet.email}</span>
                    </div>
                )}
            </div>
        </div>
    );
};

