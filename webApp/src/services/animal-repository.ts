import config from '../config/config';
import type { Animal } from '../types/animal';

interface BackendAnnouncementsResponse {
    data: Animal[];
}

export class AnimalRepository {
    
    async getAnimals(): Promise<Animal[]> {
        const response = await fetch(`${config.apiBaseUrl}/api/v1/announcements`);
        
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
}

export const animalRepository = new AnimalRepository();

