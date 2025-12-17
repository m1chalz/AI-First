import { describe, it, expect, vi } from 'vitest';
import { calculateDistanceKm, formatLocationOrDistance } from '../distance-utils';

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

describe('formatLocationOrDistance', () => {
  const mockFormatCoordinates = vi.fn((lat: number, lng: number) => `${lat.toFixed(4)}, ${lng.toFixed(4)}`);

  it.each([
    // no announcement location
    { userCoords: { lat: 52.0, lng: 21.0 }, announcementLat: null, announcementLng: null, expected: 'Location unknown' },
    { userCoords: null, announcementLat: null, announcementLng: 21.0, expected: 'Location unknown' },
    { userCoords: null, announcementLat: 52.0, announcementLng: null, expected: 'Location unknown' }
  ])(
    'returns "Location unknown" when announcement location is incomplete',
    ({ userCoords, announcementLat, announcementLng, expected }) => {
      // when
      const result = formatLocationOrDistance(userCoords, announcementLat, announcementLng, mockFormatCoordinates);

      // then
      expect(result).toBe(expected);
    }
  );

  it('returns formatted distance when user coordinates are available', () => {
    // given
    const userCoords = { lat: 52.2297, lng: 21.0122 };
    const announcementLat = 52.2387;
    const announcementLng = 21.0122;

    // when
    const result = formatLocationOrDistance(userCoords, announcementLat, announcementLng, mockFormatCoordinates);

    // then
    expect(result).toMatch(/^\d+(\.\d+)? (km|m) away$/);
    expect(mockFormatCoordinates).not.toHaveBeenCalled();
  });

  it('returns formatted coordinates when user coordinates are not available', () => {
    // given
    const announcementLat = 52.2297;
    const announcementLng = 21.0122;
    mockFormatCoordinates.mockClear();

    // when
    const result = formatLocationOrDistance(null, announcementLat, announcementLng, mockFormatCoordinates);

    // then
    expect(result).toBe('52.2297, 21.0122');
    expect(mockFormatCoordinates).toHaveBeenCalledWith(announcementLat, announcementLng);
  });
});
