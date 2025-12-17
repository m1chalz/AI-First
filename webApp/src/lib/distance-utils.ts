import type { Coordinates } from '../types/location';

export function calculateDistanceKm(from: Coordinates, to: Coordinates): number {
  const R = 6371; // Earth's radius in km
  const dLat = toRad(to.lat - from.lat);
  const dLng = toRad(to.lng - from.lng);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(from.lat)) * Math.cos(toRad(to.lat)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function toRad(deg: number): number {
  return deg * (Math.PI / 180);
}

export function formatLocationOrDistance(
  userCoords: Coordinates | null,
  announcementLat: number | null,
  announcementLng: number | null,
  formatCoordinatesFn: (lat: number, lng: number) => string
): string {
  if (announcementLat === null || announcementLng === null) {
    return 'Location unknown';
  }

  if (userCoords) {
    const distance = calculateDistanceKm(userCoords, { lat: announcementLat, lng: announcementLng });
    return formatDistance(distance);
  }

  return formatCoordinatesFn(announcementLat, announcementLng);
}

function formatDistance(distanceKm: number | undefined): string {
  if (distanceKm === undefined) {
    return 'Location unknown';
  }

  if (distanceKm >= 1) {
    return `${distanceKm.toFixed(1)} km away`;
  }

  const meters = Math.round(distanceKm * 1000);
  return `${meters} m away`;
}
