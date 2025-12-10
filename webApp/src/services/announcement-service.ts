import config from '../config/config';
import type { Announcement } from '../types/animal';
import type { Coordinates } from '../types/location';
import type { AnnouncementSubmissionDto, AnnouncementResponse } from '../models/announcement-submission';
import type { ApiError } from '../models/api-error';
import { ValidationError, DuplicateMicrochipError, NetworkError, ServerError } from '../models/api-error';

interface BackendAnnouncementsResponse {
  data: Announcement[];
}

export class AnnouncementService {
  async getAnnouncements(coordinates: Coordinates | null = null): Promise<Announcement[]> {
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
      throw new Error(`Failed to fetch announcements: ${response.status} ${response.statusText}`);
    }

    const data: BackendAnnouncementsResponse = await response.json();
    return data.data;
  }

  async getAnnouncementById(id: string): Promise<Announcement> {
    const response = await fetch(`${config.apiBaseUrl}/api/v1/announcements/${id}`);

    if (!response.ok) {
      if (response.status === 404) {
        throw new Error(`Announcement with ID ${id} not found`);
      }
      throw new Error(`Failed to fetch announcement details: ${response.status} ${response.statusText}`);
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
    } catch (error: unknown) {
      if (typeof error === 'object' && error !== null && 'type' in error) {
        throw error as ApiError;
      }
      throw this.createNetworkError();
    }
  }

  private createValidationError(): never {
    const error: ValidationError = {
      type: 'validation',
      message: 'Validation error: Please check your input'
    };
    throw error;
  }

  private createDuplicateMicrochipError(): never {
    const error: DuplicateMicrochipError = {
      type: 'duplicate-microchip',
      message: 'This microchip already exists. If this is your announcement, use your management password to update it.'
    };
    throw error;
  }

  private createServerError(status: number): never {
    const error: ServerError = {
      type: 'server',
      message: 'Server error. Please try again later.',
      statusCode: status
    };
    throw error;
  }

  private createNetworkError(): never {
    const error: NetworkError = {
      type: 'network',
      message: 'Network error. Please check your connection and try again.'
    };
    throw error;
  }

  async uploadPhoto(announcementId: string, managementPassword: string, file: File): Promise<void> {
    try {
      const formData = new FormData();
      formData.append('photo', file);

      const authHeader = `Basic ${btoa(`${announcementId}:${managementPassword}`)}`;

      const response = await fetch(`${config.apiBaseUrl}/api/v1/announcements/${announcementId}/photos`, {
        method: 'POST',
        headers: { Authorization: authHeader },
        body: formData
      });

      if (!response.ok) {
        if (response.status === 401) {
          throw this.createNetworkError();
        } else if (response.status === 404) {
          throw this.createNetworkError();
        } else {
          throw this.createServerError(response.status);
        }
      }
    } catch (error) {
      if (error instanceof Error && (error.message.includes('Network') || error.message.includes('Server'))) {
        throw error;
      }
      throw this.createNetworkError();
    }
  }
}

export const announcementService = new AnnouncementService();
