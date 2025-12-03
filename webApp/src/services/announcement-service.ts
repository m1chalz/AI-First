import config from '../config/config';
import type { Animal } from '../types/animal';
import type { Coordinates } from '../types/location';
import type { AnnouncementSubmissionDto, AnnouncementResponse } from '../models/announcement-submission';
import { ValidationError, DuplicateMicrochipError, NetworkError, ServerError } from '../models/api-error';

interface BackendAnnouncementsResponse {
    data: Animal[];
}

export class AnnouncementService {
    
    async getAnimals(coordinates: Coordinates | null = null): Promise<Animal[]> {
        let url = `${config.apiBaseUrl}/api/v1/announcements`;
        
        if (coordinates) {
            const params = new URLSearchParams();
            params.append('lat', coordinates.lat.toFixed(4));
            params.append('lng', coordinates.lng.toFixed(4));
            params.append('range', '15');
            url += `?${params.toString()}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`Failed to fetch animals: ${response.status} ${response.statusText}`);
        }
        
        const data: BackendAnnouncementsResponse = await response.json();
        return data.data;
    }
    
    async getPetById(id: string): Promise<Animal> {
        const response = await fetch(`${config.apiBaseUrl}/api/v1/announcements/${id}`);
        
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error(`Pet with ID ${id} not found`);
            }
            throw new Error(`Failed to fetch pet details: ${response.status} ${response.statusText}`);
        }
        
        return await response.json();
    }

    async createAnnouncement(dto: AnnouncementSubmissionDto): Promise<AnnouncementResponse> {
        try {
            const response = await fetch(`${config.apiBaseUrl}/api/v1/announcements`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dto)
            });

            if (!response.ok) {
                if (response.status === 400) {
                    throw this.createValidationError();
                } else if (response.status === 409) {
                    throw this.createDuplicateMicrochipError();
                } else if (response.status >= 500) {
                    throw this.createServerError(response.status);
                } else {
                    throw new Error(`Failed to create announcement: ${response.status}`);
                }
            }

            return await response.json();
        } catch (error) {
            if (error instanceof Error && error.message.includes('Failed')) {
                throw error;
            }
            throw this.createNetworkError();
        }
    }

    private createValidationError(): ValidationError {
        const error = new Error('Validation error');
        Object.assign(error, { type: 'validation' });
        throw error;
    }

    private createDuplicateMicrochipError(): DuplicateMicrochipError {
        const error = new Error('Duplicate microchip');
        Object.assign(error, { type: 'duplicate-microchip' });
        throw error;
    }

    private createServerError(status: number): ServerError {
        const error = new Error(`Server error: ${status}`);
        Object.assign(error, { type: 'server', statusCode: status });
        throw error;
    }

    private createNetworkError(): NetworkError {
        const err = new Error('Network error');
        Object.assign(err, { type: 'network' });
        throw err;
    }
}

export const announcementService = new AnnouncementService();
