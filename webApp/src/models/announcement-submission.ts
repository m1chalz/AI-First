import type { ReportMissingPetFlowState } from './ReportMissingPetFlow';

export interface AnnouncementSubmissionDto {
  petName?: string;
  species: string;
  breed?: string;
  sex: string;
  age?: number;
  description?: string;
  microchipNumber?: string;
  locationLatitude: number;
  locationLongitude: number;
  email?: string;
  phone?: string;
  lastSeenDate: string;
  status: 'MISSING';
  reward?: string;
}

export interface AnnouncementResponse {
  id: string;
  managementPassword: string;
}

export function mapFlowStateToDto(flowState: ReportMissingPetFlowState): AnnouncementSubmissionDto {
  const dto: AnnouncementSubmissionDto = {
    species: flowState.species!,
    sex: flowState.sex!,
    locationLatitude: flowState.latitude!,
    locationLongitude: flowState.longitude!,
    lastSeenDate: flowState.lastSeenDate,
    status: 'MISSING'
  };

  if (flowState.breed) dto.breed = flowState.breed;
  if (flowState.age) dto.age = flowState.age;
  if (flowState.description) dto.description = flowState.description;
  if (flowState.microchipNumber) dto.microchipNumber = flowState.microchipNumber;
  if (flowState.email) dto.email = flowState.email;
  if (flowState.phone) dto.phone = flowState.phone;
  if (flowState.reward) dto.reward = flowState.reward;

  return dto;
}

