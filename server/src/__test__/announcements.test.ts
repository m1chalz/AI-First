import { describe, it, expect, beforeEach } from 'vitest';
import request from 'supertest';
import { app } from '../app.js';
import { db } from '../database/db-utils.js';
import type { Announcement } from '../types/announcement.js';

const TEST_ANNOUNCEMENT_1 = {
  id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  pet_name: 'Azor',
  species: 'DOG',
  breed: 'Owczarek niemiecki',
  gender: 'MALE',
  description: 'Duży owczarek niemiecki, bardzo przyjazny. Nosi czerwoną obrożę.',
  location: 'Gdynia',
  location_radius: 10,
  last_seen_date: '2025-11-19',
  email: 'test@example.pl',
  phone: '+48 600 700 800',
  photo_url: 'https://example.com/azor.jpg',
  status: 'ACTIVE',
};

const TEST_ANNOUNCEMENT_2 = {
  id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
  pet_name: 'Filemon',
  species: 'CAT',
  breed: null,
  gender: 'MALE',
  description: 'Rudy kot, bardzo nieśmiały.',
  location: 'Sopot',
  location_radius: null,
  last_seen_date: '2025-11-18',
  email: null,
  phone: '555-123-456',
  photo_url: null,
  status: 'ACTIVE',
};

describe('GET /api/v1/announcements', () => {
  beforeEach(async () => {
    await db('announcement').del();
  });

  it('should return 200 with announcements array when database has data', async () => {
    // Given: Database has 2 test announcements
    await db('announcement').insert([TEST_ANNOUNCEMENT_1, TEST_ANNOUNCEMENT_2]);
    
    // When: Client requests all announcements
    const response = await request(app).get('/api/v1/announcements');
    
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
      gender: TEST_ANNOUNCEMENT_1.gender,
      description: TEST_ANNOUNCEMENT_1.description,
      location: TEST_ANNOUNCEMENT_1.location,
      locationRadius: TEST_ANNOUNCEMENT_1.location_radius,
      lastSeenDate: TEST_ANNOUNCEMENT_1.last_seen_date,
      email: TEST_ANNOUNCEMENT_1.email,
      phone: TEST_ANNOUNCEMENT_1.phone,
      photoUrl: TEST_ANNOUNCEMENT_1.photo_url,
      status: TEST_ANNOUNCEMENT_1.status,
      createdAt: expect.any(String),
      updatedAt: expect.any(String),
    });
  });

  it('should return 200 with empty array when database is empty', async () => {
    // Given: Database has no announcements (cleaned in beforeEach)
    
    // When: Client requests all announcements
    const response = await request(app).get('/api/v1/announcements');
    
    // Then: Returns HTTP 200 with empty data array
    expect(response.status).toBe(200);
    expect(response.body).toEqual({ data: [] });
  });
});
