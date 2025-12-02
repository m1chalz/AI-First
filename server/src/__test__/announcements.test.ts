import { describe, it, expect, beforeEach } from 'vitest';
import request from 'supertest';
import server from '../server.ts';
import { db } from '../database/db-utils.ts';
import type { Announcement } from '../types/announcement.ts';

const TEST_ANNOUNCEMENT_1 = {
  id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  pet_name: 'Azor',
  species: 'DOG',
  breed: 'Owczarek niemiecki',
  sex: 'MALE',
  age: 5,
  description: 'Duży owczarek niemiecki, bardzo przyjazny. Nosi czerwoną obrożę.',
  microchip_number: null,
  location_latitude: 54.48,
  location_longitude: 18.53,
  last_seen_date: '2025-11-19',
  email: 'test@example.pl',
  phone: '+48 600 700 800',
  photo_url: 'https://example.com/azor.jpg',
  status: 'MISSING',
  reward: null,
  management_password_hash: 'test_hash_1',
  created_at: '2025-11-19T10:00:00.000Z',
  updated_at: '2025-11-19T10:00:00.000Z',
};

const TEST_ANNOUNCEMENT_2 = {
  id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
  pet_name: 'Filemon',
  species: 'CAT',
  breed: null,
  sex: 'MALE',
  age: 3,
  description: 'Rudy kot, bardzo nieśmiały.',
  microchip_number: null,
  location_latitude: 54.43,
  location_longitude: 18.57,
  last_seen_date: '2025-11-18',
  email: null,
  phone: '555-123-456',
  photo_url: 'https://example.com/filemon.jpg',
  status: 'FOUND',
  reward: null,
  management_password_hash: 'test_hash_2',
  created_at: '2025-11-18T12:00:00.000Z',
  updated_at: '2025-11-18T12:00:00.000Z',
};

describe('GET /api/v1/announcements', () => {
  beforeEach(async () => {
    await db('announcement').del();
  });

  it('should return 200 with announcements array when database has data', async () => {
    // Given: Database has 2 test announcements
    await db('announcement').insert([TEST_ANNOUNCEMENT_1, TEST_ANNOUNCEMENT_2]);
    
    // When: Client requests all announcements
    const response = await request(server).get('/api/v1/announcements');
    
    // Then: Returns HTTP 200 with JSON array containing announcements
    expect(response.status).toBe(200);
    expect(response.body).toHaveProperty('data');
    expect(Array.isArray(response.body.data)).toBe(true);
    expect(response.body.data.length).toBe(2);
    
    // Find TEST_ANNOUNCEMENT_1 in response by ID
    const announcement1 = response.body.data.find((a: Announcement) => a.id === TEST_ANNOUNCEMENT_1.id);
    expect(announcement1).toBeDefined();
    
    // Verify announcement has correct values
    expect(announcement1).toEqual({
      id: TEST_ANNOUNCEMENT_1.id,
      petName: TEST_ANNOUNCEMENT_1.pet_name,
      species: TEST_ANNOUNCEMENT_1.species,
      breed: TEST_ANNOUNCEMENT_1.breed,
      sex: TEST_ANNOUNCEMENT_1.sex,
      age: TEST_ANNOUNCEMENT_1.age,
      description: TEST_ANNOUNCEMENT_1.description,
      microchipNumber: TEST_ANNOUNCEMENT_1.microchip_number,
      locationLatitude: TEST_ANNOUNCEMENT_1.location_latitude,
      locationLongitude: TEST_ANNOUNCEMENT_1.location_longitude,
      lastSeenDate: TEST_ANNOUNCEMENT_1.last_seen_date,
      email: TEST_ANNOUNCEMENT_1.email,
      phone: TEST_ANNOUNCEMENT_1.phone,
      photoUrl: TEST_ANNOUNCEMENT_1.photo_url,
      status: TEST_ANNOUNCEMENT_1.status,
      reward: TEST_ANNOUNCEMENT_1.reward,
      createdAt: expect.any(String),
      updatedAt: expect.any(String),
    });
  });

  it('should filter out the announcements without photoUrl', async () => {
    // Given: Database has 2 test announcements
    await db('announcement').insert({
      ...TEST_ANNOUNCEMENT_1,
      photo_url: null,
    });
    
    // When: Client requests all announcements
    const response = await request(server).get('/api/v1/announcements');
    
    // Then: Returns HTTP 200 with JSON array containing announcements
    expect(response.status).toBe(200);
    expect(response.body.data).toHaveLength(0);
  });

  it('should return 200 with empty array when database is empty', async () => {
    // Given: Database has no announcements (cleaned in beforeEach)
    
    // When: Client requests all announcements
    const response = await request(server).get('/api/v1/announcements');
    
    // Then: Returns HTTP 200 with empty data array
    expect(response.status).toBe(200);
    expect(response.body).toEqual({ data: [] });
  });
});

describe('GET /api/v1/announcements/:id', () => {
  beforeEach(async () => {
    await db('announcement').del();
  });

  it('should return 200 and announcement when ID exists', async () => {
    // Given: Database seeded with test announcement
    await db('announcement').insert(TEST_ANNOUNCEMENT_1);
    
    // When: Client requests announcement by ID
    const response = await request(server)
      .get(`/api/v1/announcements/${TEST_ANNOUNCEMENT_1.id}`)
      .expect('Content-Type', /json/)
      .expect(200);
    
    // Then: Response contains announcement
    expect(response.body).toEqual({
      id: TEST_ANNOUNCEMENT_1.id,
      petName: TEST_ANNOUNCEMENT_1.pet_name,
      species: TEST_ANNOUNCEMENT_1.species,
      breed: TEST_ANNOUNCEMENT_1.breed,
      sex: TEST_ANNOUNCEMENT_1.sex,
      age: TEST_ANNOUNCEMENT_1.age,
      description: TEST_ANNOUNCEMENT_1.description,
      microchipNumber: TEST_ANNOUNCEMENT_1.microchip_number,
      locationLatitude: TEST_ANNOUNCEMENT_1.location_latitude,
      locationLongitude: TEST_ANNOUNCEMENT_1.location_longitude,
      lastSeenDate: TEST_ANNOUNCEMENT_1.last_seen_date,
      email: TEST_ANNOUNCEMENT_1.email,
      phone: TEST_ANNOUNCEMENT_1.phone,
      photoUrl: TEST_ANNOUNCEMENT_1.photo_url,
      status: TEST_ANNOUNCEMENT_1.status,
      reward: TEST_ANNOUNCEMENT_1.reward,
      createdAt: expect.any(String),
      updatedAt: expect.any(String),
    });
  });

  it.each([
    { id: '123e4567-e89b-12d3-a456-426614174000', description: 'non-existent ID' },
    { id: 'abc-123', description: 'malformed UUID' },
  ])('should return 404 when $description', async ({ id }) => {
    // Given: Empty database (cleared by beforeEach)
    
    // When: Client requests with invalid ID
    const response = await request(server)
      .get(`/api/v1/announcements/${id}`)
      .expect('Content-Type', /json/)
      .expect(404);
    
    // Then: Error response returned
    expect(response.body).toEqual({
      error: {
        requestId: expect.any(String),
        code: 'NOT_FOUND',
        message: 'Resource not found'
      }
    });
  });

  it('should include optional fields with null values', async () => {
    // Given: Announcement with null optional fields
    await db('announcement').insert(TEST_ANNOUNCEMENT_2);
    
    // When: Client requests announcement
    const response = await request(server)
      .get(`/api/v1/announcements/${TEST_ANNOUNCEMENT_2.id}`)
      .expect(200);
    
    // Then: Response includes null optional fields
    expect(response.body.breed).toBeNull();
    expect(response.body.email).toBeNull();
    expect(response.body.microchipNumber).toBeNull();
  });
});

describe('POST /api/v1/announcements', () => {
  beforeEach(async () => {
    await db('announcement').del();
  });

  it('should create announcement with email contact and return 201', async () => {
    // given
    const data = {
      species: 'Golden Retriever',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      status: 'MISSING' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com'
    };
    
    // when
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(data)
      .expect(201);
    
    // then
    expect(response.body).toHaveProperty('id');
    expect(response.body).toHaveProperty('managementPassword');
    expect(response.body).not.toHaveProperty('managementPasswordHash');
    expect(response.body.managementPassword).toMatch(/^\d{6}$/);
    expect(response.body.species).toBe(data.species);
    expect(response.body.email).toBe(data.email);
    expect(response.body.status).toBe(data.status);
  });

  it('should create announcement with all fields and return 201', async () => {
    // given
    const data = {
      petName: 'Buddy',
      species: 'Golden Retriever',
      breed: 'Purebred',
      sex: 'MALE',
      age: 5,
      description: 'Friendly dog with brown fur',
      microchipNumber: '123456789012345',
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      lastSeenDate: '2025-11-20',
      status: 'MISSING' as const,
      email: 'john@example.com',
      phone: '+1 555 123 4567',
      reward: '500 USD'
    };
    
    // when
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(data)
      .expect(201);
    
    // then
    expect(response.body).toHaveProperty('id');
    expect(response.body).toHaveProperty('managementPassword');
    expect(response.body).not.toHaveProperty('managementPasswordHash');
    expect(response.body.managementPassword).toMatch(/^\d{6}$/);
    expect(response.body.petName).toBe(data.petName);
    expect(response.body.species).toBe(data.species);
    expect(response.body.breed).toBe(data.breed);
    expect(response.body.sex).toBe(data.sex);
    expect(response.body.age).toBe(data.age);
    expect(response.body.description).toBe(data.description);
    expect(response.body.microchipNumber).toBe(data.microchipNumber);
    expect(response.body.locationLatitude).toBe(data.locationLatitude);
    expect(response.body.locationLongitude).toBe(data.locationLongitude);
    expect(response.body.lastSeenDate).toBe(data.lastSeenDate);
    expect(response.body.photoUrl).toBeNull();
    expect(response.body.status).toBe(data.status);
    expect(response.body.email).toBe(data.email);
    expect(response.body.phone).toBe(data.phone);
    expect(response.body.reward).toBe(data.reward);
    expect(response.body).toHaveProperty('createdAt');
    expect(response.body).toHaveProperty('updatedAt');
  });

  it('should create announcement with phone contact and return 201', async () => {
    // given
    const data = {
      species: 'Siamese Cat',
      sex: 'FEMALE',
      lastSeenDate: '2025-11-19',
      status: 'FOUND' as const,
      locationLatitude: 51.5074,
      locationLongitude: -0.1278,
      phone: '+44 20 7946 0958'
    };
    
    // when
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(data)
      .expect(201);
    
    // then
    expect(response.body.id).toBeDefined();
    expect(response.body.managementPassword).toMatch(/^\d{6}$/);
    expect(response.body.phone).toBe(data.phone);
    expect(response.body.status).toBe(data.status);
  });

  it('should return 400 when missing required species field', async () => {
    // given
    const data = {
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      status: 'MISSING',
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com'
    };
    
    // when
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(data)
      .expect(400);
    
    // then
    expect(response.body.error).toMatchObject({
      requestId: expect.any(String),
      code: 'MISSING_VALUE',
      field: 'species'
    });
  });

  it('should return 400 when no contact method provided', async () => {
    // given
    const data = {
      species: 'Dog',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      status: 'MISSING',
      locationLatitude: 40.785091,
      locationLongitude: -73.968285
    };
    
    // when
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(data)
      .expect(400);
    
    // then
    expect(response.body.error).toMatchObject({
      requestId: expect.any(String),
      code: 'MISSING_CONTACT',
      field: 'contact'
    });
  });

  it('should sanitize text fields removing HTML tags', async () => {
    // given
    const data = {
      species: 'Golden<b>Retriever</b>',
      sex: 'MALE',
      description: '<img src=x onerror=alert(1)>Test<div>content</div>',
      petName: '<div>Buddy</div>',
      lastSeenDate: '2025-11-20',
      status: 'MISSING' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com'
    };
    
    // when
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(data)
      .expect(201);
    
    // then
    expect(response.body.species).toBe('GoldenRetriever');
    expect(response.body.petName).toBe('Buddy');
    expect(response.body.description).toBe('Testcontent');
    expect(response.body.email).toBe(data.email);
    expect(response.body.status).toBe(data.status);
  });

  it('should return 400 when unknown fields present', async () => {
    // given
    const data = {
      species: 'Dog',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      status: 'MISSING',
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com',
      unknownField: 'value' // Unknown field
    };
    
    // when
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(data)
      .expect(400);
    
    // then
    expect(response.body.error).toMatchObject({
      requestId: expect.any(String),
      code: 'INVALID_FIELD',
      field: 'unknownField'
    });
  });

  it('should return 409 when duplicate microchip number', async () => {
    // given: First announcement with microchip number
    const firstAnnouncement = {
      species: 'Golden Retriever',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      status: 'MISSING' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com',
      microchipNumber: '123456789012345'
    };
    
    await request(server)
      .post('/api/v1/announcements')
      .send(firstAnnouncement)
      .expect(201);
    
    // when: Attempt to create second announcement with same microchip number
    const duplicateAnnouncement = {
      species: 'Labrador',
      sex: 'FEMALE',
      lastSeenDate: '2025-11-21',
      status: 'FOUND' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'jane@example.com',
      microchipNumber: '123456789012345' // Same microchip number
    };
    
    const response = await request(server)
      .post('/api/v1/announcements')
      .send(duplicateAnnouncement)
      .expect(409);
    
    // then: Returns HTTP 409 with CONFLICT error
    expect(response.body.error).toMatchObject({
      requestId: expect.any(String),
      code: 'CONFLICT',
      message: 'An entity with this value already exists',
      field: 'microchipNumber'
    });
  });
});

describe('GET /api/v1/announcements - Location Filtering (User Story 1)', () => {
  const KRAKOW = { lat: 50.0614, lng: 19.9383 };
  const NEARBY_2KM = { lat: 50.0700, lng: 19.9500 };
  const WARSAW = { lat: 52.2297, lng: 21.0122 };

  const ANNOUNCEMENT_KRAKOW = {
    id: '11111111-1111-1111-1111-111111111111',
    pet_name: 'Max',
    species: 'DOG',
    breed: 'Golden Retriever',
    sex: 'MALE',
    age: 3,
    description: 'Friendly dog in Krakow',
    microchip_number: null,
    location_latitude: KRAKOW.lat,
    location_longitude: KRAKOW.lng,
    last_seen_date: '2025-11-20',
    email: 'krakow@example.com',
    phone: null,
    photo_url: 'url',
    status: 'MISSING',
    reward: null,
    management_password_hash: 'hash1',
    created_at: '2025-11-20T10:00:00.000Z',
    updated_at: '2025-11-20T10:00:00.000Z',
  };

  const ANNOUNCEMENT_NEARBY = {
    id: '22222222-2222-2222-2222-222222222222',
    pet_name: 'Luna',
    species: 'CAT',
    breed: null,
    sex: 'FEMALE',
    age: 2,
    description: 'Cat near Krakow',
    microchip_number: null,
    location_latitude: NEARBY_2KM.lat,
    location_longitude: NEARBY_2KM.lng,
    last_seen_date: '2025-11-21',
    email: 'nearby@example.com',
    phone: null,
    photo_url: 'url',
    status: 'MISSING',
    reward: null,
    management_password_hash: 'hash2',
    created_at: '2025-11-21T10:00:00.000Z',
    updated_at: '2025-11-21T10:00:00.000Z',
  };

  const ANNOUNCEMENT_WARSAW = {
    id: '33333333-3333-3333-3333-333333333333',
    pet_name: 'Buddy',
    species: 'DOG',
    breed: 'Beagle',
    sex: 'MALE',
    age: 5,
    description: 'Dog in Warsaw',
    microchip_number: null,
    location_latitude: WARSAW.lat,
    location_longitude: WARSAW.lng,
    last_seen_date: '2025-11-22',
    email: 'warsaw@example.com',
    phone: null,
    photo_url: 'url',
    status: 'MISSING',
    reward: null,
    management_password_hash: 'hash3',
    created_at: '2025-11-22T10:00:00.000Z',
    updated_at: '2025-11-22T10:00:00.000Z',
  };

  beforeEach(async () => {
    await db('announcement').del();
    await db('announcement').insert([
      ANNOUNCEMENT_KRAKOW,
      ANNOUNCEMENT_NEARBY,
      ANNOUNCEMENT_WARSAW,
    ]);
  });

  it('should filter announcements within custom radius (10km)', async () => {
    // given
    const searchLat = KRAKOW.lat;
    const searchLng = KRAKOW.lng;
    const range = 10;

    // when
    const response = await request(server)
      .get('/api/v1/announcements')
      .query({ lat: searchLat, lng: searchLng, range: range })
      .expect(200);

    // then
    expect(response.body.data).toHaveLength(2);
    const ids = response.body.data.map((a: { id: string }) => a.id);
    expect(ids).toContain(ANNOUNCEMENT_KRAKOW.id);
    expect(ids).toContain(ANNOUNCEMENT_NEARBY.id);
    expect(ids).not.toContain(ANNOUNCEMENT_WARSAW.id);
  });

  it('should return empty array when no announcements in radius', async () => {
    // given
    const searchLat = 0.0;
    const searchLng = 0.0;
    const range = 10;

    // when
    const response = await request(server)
      .get('/api/v1/announcements')
      .query({ lat: searchLat, lng: searchLng, range: range })
      .expect(200);

    // then
    expect(response.body.data).toEqual([]);
  });

  it('should return all announcements within large radius (300km)', async () => {
    // given
    const searchLat = KRAKOW.lat;
    const searchLng = KRAKOW.lng;
    const range = 300;

    // when
    const response = await request(server)
      .get('/api/v1/announcements')
      .query({ lat: searchLat, lng: searchLng, range: range })
      .expect(200);

    // then
    expect(response.body.data).toHaveLength(3);
  });

  it('should verify distance calculation accuracy (known coordinates)', async () => {
    // given
    const searchLat = KRAKOW.lat;
    const searchLng = KRAKOW.lng;
    const range = 5;

    // when
    const response = await request(server)
      .get('/api/v1/announcements')
      .query({ lat: searchLat, lng: searchLng, range: range })
      .expect(200);

    // then
    expect(response.body.data).toHaveLength(2);
    const ids = response.body.data.map((a: { id: string }) => a.id);
    expect(ids).toContain(ANNOUNCEMENT_KRAKOW.id);
    expect(ids).toContain(ANNOUNCEMENT_NEARBY.id);
    expect(ids).not.toContain(ANNOUNCEMENT_WARSAW.id);
  });

  it('should filter with 5km default when lat/lng provided without range', async () => {
    // given
    const searchLat = KRAKOW.lat;
    const searchLng = KRAKOW.lng;

    // when
    const response = await request(server)
      .get('/api/v1/announcements')
      .query({ lat: searchLat, lng: searchLng })
      .expect(200);

    // then
    expect(response.body.data).toHaveLength(2);
    const ids = response.body.data.map((a: { id: string }) => a.id);
    expect(ids).toContain(ANNOUNCEMENT_KRAKOW.id);
    expect(ids).toContain(ANNOUNCEMENT_NEARBY.id);
    expect(ids).not.toContain(ANNOUNCEMENT_WARSAW.id);
  });

  it('should return HTTP 400 when only lat provided', async () => {
    // when
    const response = await request(server)
      .get('/api/v1/announcements?lat=50.0614')
      .expect(400);

    // then
    expect(response.body.error).toMatchObject({
      code: 'INVALID_PARAMETER',
      message: 'Parameter \'lng\' is required when \'lat\' is provided',
    });
  });

  it('should return HTTP 400 when only lng provided', async () => {
    // when
    const response = await request(server)
      .get('/api/v1/announcements?lng=19.9383')
      .expect(400);

    // then
    expect(response.body.error).toMatchObject({
      code: 'INVALID_PARAMETER',
      message: 'Parameter \'lat\' is required when \'lng\' is provided',
    });
  });

  it('should return all announcements when neither lat nor lng provided', async () => {
    // when
    const response = await request(server)
      .get('/api/v1/announcements')
      .expect(200);

    // then
    expect(response.body.data).toHaveLength(3);
  });

  it('should ignore range parameter when lat/lng not provided', async () => {
    // when
    const response = await request(server)
      .get('/api/v1/announcements')
      .query({ range: 10 })
      .expect(200);

    // then
    expect(response.body.data).toHaveLength(3);
  });

  it.each([
    { lat: 91, lng: 19.9383, expectedMessage: 'Parameter \'lat\' must be between -90 and 90' },
    { lat: -91, lng: 19.9383, expectedMessage: 'Parameter \'lat\' must be between -90 and 90' },
  ])('should return HTTP 400 when lat out of range: $lat', async ({ lat, lng, expectedMessage }) => {
    // when
    const response = await request(server)
      .get(`/api/v1/announcements?lat=${lat}&lng=${lng}`)
      .expect(400);

    // then
    expect(response.body.error).toMatchObject({
      code: 'INVALID_PARAMETER',
      message: expectedMessage,
    });
  });

  it.each([
    { lat: 50.0614, lng: 181, expectedMessage: 'Parameter \'lng\' must be between -180 and 180' },
    { lat: 50.0614, lng: -181, expectedMessage: 'Parameter \'lng\' must be between -180 and 180' },
  ])('should return HTTP 400 when lng out of range: $lng', async ({ lat, lng, expectedMessage }) => {
    // when
    const response = await request(server)
      .get(`/api/v1/announcements?lat=${lat}&lng=${lng}`)
      .expect(400);

    // then
    expect(response.body.error).toMatchObject({
      code: 'INVALID_PARAMETER',
      message: expectedMessage,
    });
  });

  it.each([
    { lat: 'abc', lng: 19.9383, expectedMessage: 'Parameter \'lat\' must be a valid number' },
    { lat: 50.0614, lng: 'xyz', expectedMessage: 'Parameter \'lng\' must be a valid number' },
  ])('should return HTTP 400 when coordinates are not numbers', async ({ lat, lng, expectedMessage }) => {
    // when
    const response = await request(server)
      .get(`/api/v1/announcements?lat=${lat}&lng=${lng}`)
      .expect(400);

    // then
    expect(response.body.error).toMatchObject({
      code: 'INVALID_PARAMETER',
      message: expectedMessage,
    });
  });

  it.each([
    { lat: 50.0614, lng: 19.9383, range: 0.5, expectedMessage: 'Parameter \'range\' must be an integer' },
    { lat: 50.0614, lng: 19.9383, range: 10.5, expectedMessage: 'Parameter \'range\' must be an integer' },
  ])('should return HTTP 400 when range is not an integer: $range', async ({ lat, lng, range, expectedMessage }) => {
    // when
    const response = await request(server)
      .get(`/api/v1/announcements?lat=${lat}&lng=${lng}&range=${range}`)
      .expect(400);

    // then
    expect(response.body.error).toMatchObject({
      code: 'INVALID_PARAMETER',
      message: expectedMessage,
    });
  });
});
