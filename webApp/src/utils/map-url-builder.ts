export function buildGoogleMapsUrl(lat: number, lng: number): string {
  return `https://www.google.com/maps?q=${lat},${lng}`;
}

export function buildOpenStreetMapUrl(lat: number, lng: number): string {
  return `https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}&zoom=15`;
}

export function buildMapUrl(lat: number, lng: number, provider: 'google' | 'osm' = 'google'): string {
  return provider === 'google' ? buildGoogleMapsUrl(lat, lng) : buildOpenStreetMapUrl(lat, lng);
}
