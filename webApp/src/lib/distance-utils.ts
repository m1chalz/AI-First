export function formatDistance(distanceKm: number | undefined): string {
  // TODO: Implement in Phase 3 (US1)
  if (distanceKm === undefined) {
    return 'Location unknown';
  }
  return `${distanceKm} km away`;
}

