import { describe, it, expect } from 'vitest';
import { calculateDistanceKm, formatDistance } from '../distance-utils';

describe('calculateDistanceKm', () => {
  it.each([
    // same point
    { from: { lat: 52.2297, lng: 21.0122 }, to: { lat: 52.2297, lng: 21.0122 }, expectedApprox: 0 },
    // Warsaw to Krakow (~252 km)
    { from: { lat: 52.2297, lng: 21.0122 }, to: { lat: 50.0647, lng: 19.945 }, expectedApprox: 252 },
    // short distance (~1 km)
    { from: { lat: 52.2297, lng: 21.0122 }, to: { lat: 52.2387, lng: 21.0122 }, expectedApprox: 1 }
  ])('calculateDistanceKm($from, $to) should be approximately $expectedApprox km', ({ from, to, expectedApprox }) => {
    // when
    const result = calculateDistanceKm(from, to);

    // then
    expect(result).toBeCloseTo(expectedApprox, 0);
  });
});

describe('formatDistance', () => {
  it('returns formatted distance in km when distance >= 1 km', () => {
    // given
    const userCoords = { lat: 52.2297, lng: 21.0122 };
    const announcementLat = 50.0647; // Krakow
    const announcementLng = 19.945;

    // when
    const result = formatDistance(userCoords, announcementLat, announcementLng);

    // then
    expect(result).toMatch(/^\d+\.\d km away$/);
  });

  it('returns formatted distance in meters when distance < 1 km', () => {
    // given
    const userCoords = { lat: 52.2297, lng: 21.0122 };
    const announcementLat = 52.23;
    const announcementLng = 21.0125;

    // when
    const result = formatDistance(userCoords, announcementLat, announcementLng);

    // then
    expect(result).toMatch(/^\d+ m away$/);
  });

  it('returns "0 m away" for same coordinates', () => {
    // given
    const userCoords = { lat: 52.2297, lng: 21.0122 };

    // when
    const result = formatDistance(userCoords, 52.2297, 21.0122);

    // then
    expect(result).toBe('0 m away');
  });
});
