import React from 'react';
import { MdCalendarToday, MdPhone, MdEmail, MdLocationOn, MdAttachMoney } from 'react-icons/md';
import type { Animal, AnimalStatus } from '../../types/animal';
import { ANIMAL_STATUS_BADGE_COLORS } from '../../types/animal';
import { formatDate } from '../../utils/date-formatter';
import { formatCoordinates } from '../../utils/coordinate-formatter';
import { formatMicrochip } from '../../utils/microchip-formatter';
import { formatSpecies, formatSex } from '../../utils/species-formatter';
import { buildGoogleMapsUrl } from '../../utils/map-url-builder';
import styles from './PetDetailsContent.module.css';

interface PetDetailsContentProps {
    pet: Animal;
}

const getGenderIcon = (sex: string): string => {
    if (sex === 'MALE') return '♂';
    if (sex === 'FEMALE') return '♀';
    return '';
};

const getStatusBadgeColor = (status: AnimalStatus): string => {
    return ANIMAL_STATUS_BADGE_COLORS[status] || '#93A2B4';
};

export const PetDetailsContent: React.FC<PetDetailsContentProps> = ({ pet }) => {
    const statusColor = getStatusBadgeColor(pet.status);
    const formattedDate = formatDate(pet.lastSeenDate);
    const hasLocation = pet.locationLatitude !== null && pet.locationLongitude !== null;
    const mapUrl = hasLocation && pet.locationLatitude !== null && pet.locationLongitude !== null
        ? buildGoogleMapsUrl(pet.locationLatitude, pet.locationLongitude)
        : null;

    return (
        <div className={styles.content}>
            {/* Hero Image Section */}
            {pet.photoUrl && (
                <div className={styles.heroSection}>
                    <img
                        src={pet.photoUrl}
                        alt={pet.petName ? `${pet.petName} photo` : 'Pet photo'}
                        className={styles.heroImage}
                    />
                    {/* Status Badge Overlay - Top Right */}
                    <div className={styles.statusBadge} style={{ backgroundColor: statusColor }}>
                        {pet.status}
                    </div>
                    {/* Reward Badge Overlay - Bottom Left */}
                    {pet.reward && (
                        <div className={styles.rewardBadge}>
                            <MdAttachMoney className={styles.rewardIcon} />
                            <span>Reward {pet.reward}</span>
                        </div>
                    )}
                </div>
            )}

            {/* Content Section */}
            <div className={styles.contentSection}>
                {/* Header Row - 3 columns: Date, Phone, Email */}
                <div className={styles.headerRow}>
                    <div className={styles.headerItem}>
                        <MdCalendarToday className={styles.headerIcon} />
                        <div className={styles.headerContent}>
                            <span className={styles.headerLabel}>Date of Disappearance</span>
                            <span className={styles.headerValue}>{formattedDate}</span>
                        </div>
                    </div>
                    <div className={styles.headerItem}>
                        <MdPhone className={styles.headerIcon} />
                        <div className={styles.headerContent}>
                            <span className={styles.headerLabel}>Contact owner</span>
                            <span className={styles.headerValue}>{pet.phone || '—'}</span>
                        </div>
                    </div>
                    <div className={styles.headerItem}>
                        <MdEmail className={styles.headerIcon} />
                        <div className={styles.headerContent}>
                            <span className={styles.headerLabel}>Contact owner</span>
                            <span className={styles.headerValue}>{pet.email || '—'}</span>
                        </div>
                    </div>
                </div>

                {/* Identification Information */}
                <div className={styles.section}>
                    <div className={styles.fieldRow}>
                        <span className={styles.fieldLabel}>Microchip number</span>
                        <span className={styles.fieldValue} data-testid="petDetails.microchip.value">
                            {pet.microchipNumber ? formatMicrochip(pet.microchipNumber) : '—'}
                        </span>
                    </div>
                </div>

                {/* Pet Details - 2-column grid */}
                <div className={styles.section}>
                    <div className={styles.gridRow}>
                        <div className={styles.gridItem}>
                            <span className={styles.fieldLabel}>Animal Species</span>
                            <span className={styles.fieldValue} data-testid="petDetails.species.value">{formatSpecies(pet.species)}</span>
                        </div>
                        <div className={styles.gridItem}>
                            <span className={styles.fieldLabel}>Animal Race</span>
                            <span className={styles.fieldValue} data-testid="petDetails.breed.value">{pet.breed || '—'}</span>
                        </div>
                    </div>
                    <div className={styles.gridRow}>
                        <div className={styles.gridItem}>
                            <span className={styles.fieldLabel}>Animal Sex</span>
                            <span className={styles.fieldValue} data-testid="petDetails.sex.value">
                                {pet.sex !== 'UNKNOWN' ? `${formatSex(pet.sex)} ${getGenderIcon(pet.sex)}` : '—'}
                            </span>
                        </div>
                        <div className={styles.gridItem}>
                            <span className={styles.fieldLabel}>Animal Approx. Age</span>
                            <span className={styles.fieldValue} data-testid="petDetails.age.value">
                                {pet.age !== null ? `${pet.age} years` : '—'}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Location Information */}
                {hasLocation && (
                    <div className={styles.section}>
                        <div className={styles.locationRow}>
                            <MdLocationOn className={styles.locationIcon} />
                            <div className={styles.locationContent}>
                                <span className={styles.fieldLabel}>Lat / Long</span>
                                <span className={styles.fieldValue}>
                                    {pet.locationLatitude !== null && pet.locationLongitude !== null
                                        ? formatCoordinates(pet.locationLatitude, pet.locationLongitude)
                                        : '—'}
                                </span>
                            </div>
                            {mapUrl && (
                                <a
                                    href={mapUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className={styles.mapButton}
                                    data-testid="petDetails.mapButton.click"
                                >
                                    Show on the map
                                </a>
                            )}
                        </div>
                    </div>
                )}

                {/* Descriptive Information */}
                <div className={styles.section}>
                    <div className={styles.fieldRow}>
                        <span className={styles.fieldLabel}>Animal Additional Description</span>
                        <span className={styles.fieldValue}>{pet.description || '—'}</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

