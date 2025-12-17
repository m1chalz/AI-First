export function formatDistance(distanceKm: number | undefined): string {
  if (distanceKm === undefined) {
    return 'Location unknown';
  }

  if (distanceKm >= 1) {
    return `${distanceKm.toFixed(1)} km away`;
  }

  const meters = Math.round(distanceKm * 1000);
  return `${meters} m away`;
}
