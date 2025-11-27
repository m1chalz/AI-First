/**
 * Builds a Google Maps URL for the given coordinates.
 * @param lat - Latitude coordinate
 * @param lng - Longitude coordinate
 * @returns Google Maps URL with the coordinates
 */
export function buildMapUrl(lat: number, lng: number): string {
  return `https://www.google.com/maps?q=${lat},${lng}`;
}
