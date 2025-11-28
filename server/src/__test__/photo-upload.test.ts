import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import request from 'supertest';
import server from '../server.ts';
import { db } from '../database/db-utils.ts';
import { promises as fs } from 'fs';
import path from 'path';
import { hashPassword } from '../lib/password-management.ts';
import { AnnouncementRow } from '../types/announcement';

async function getPhotoUrl(id: string): Promise<string | null> {
  return (await db('announcement').where({ id }).first()).photo_url;
}

const TEST_PASSWORD = 'password';
const TEST_ANNOUNCEMENT: AnnouncementRow = {
  id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  pet_name: 'Azor',
  species: 'DOG',
  breed: 'Owczarek niemiecki',
  sex: 'MALE',
  age: 5,
  description: 'Duży owczarek niemiecki',
  microchip_number: null,
  location_latitude: 54.48,
  location_longitude: 18.53,
  last_seen_date: '2025-11-19',
  email: 'test@example.pl',
  phone: '+48 600 700 800',
  photo_url: null,
  status: 'MISSING',
  reward: null,
  management_password_hash: await hashPassword(TEST_PASSWORD),
  created_at: '2025-11-19T10:00:00.000Z',
  updated_at: '2025-11-19T10:00:00.000Z',
};

const CREDENTIALS = Buffer.from(`${TEST_ANNOUNCEMENT.id}:password`).toString('base64');

const JPEG_BUFFER = Buffer.from([
  0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
  0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0xFF, 0xDB, 0x00, 0x43,
  0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07, 0x09,
  0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D, 0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12,
  0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20,
  0x24, 0x2E, 0x27, 0x20, 0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29,
  0x2C, 0x30, 0x31, 0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32,
  0x3C, 0x2E, 0x33, 0x34, 0x32, 0xFF, 0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01,
  0x00, 0x01, 0x01, 0x01, 0x11, 0x00, 0xFF, 0xC4, 0x00, 0x1F, 0x00, 0x00,
  0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
  0x09, 0x0A, 0x0B, 0xFF, 0xC4, 0x00, 0xB5, 0x10, 0x00, 0x02, 0x01, 0x03,
  0x03, 0x02, 0x04, 0x03, 0x05, 0x05, 0x04, 0x04, 0x00, 0x00, 0x01, 0x7D,
  0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06,
  0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xA1, 0x08,
  0x23, 0x42, 0xB1, 0xC1, 0x15, 0x52, 0xD1, 0xF0, 0x24, 0x33, 0x62, 0x72,
  0x82, 0x09, 0x0A, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x25, 0x26, 0x27, 0x28,
  0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x43, 0x44, 0x45,
  0x46, 0x47, 0x48, 0x49, 0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59,
  0x5A, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75,
  0x76, 0x77, 0x78, 0x79, 0x7A, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89,
  0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9A, 0xA2, 0xA3,
  0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6,
  0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8, 0xC9,
  0xCA, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE1, 0xE2,
  0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF1, 0xF2, 0xF3, 0xF4,
  0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA, 0xFF, 0xDA, 0x00, 0x08, 0x01, 0x01,
  0x00, 0x00, 0x3F, 0x00, 0xFB, 0xD4, 0xFF, 0xD9,
]);

const IMAGES_DIR = path.join(process.cwd(), 'public', 'images');

describe('POST /api/v1/announcements/:id/photos', () => {
  beforeEach(async () => {
    const passwordHash = await hashPassword(TEST_PASSWORD);
    TEST_ANNOUNCEMENT.management_password_hash = passwordHash;

    await db('announcement').del();
    await db('announcement').insert(TEST_ANNOUNCEMENT);
  });

  afterEach(async () => {
    // Clean up uploaded files
    const files = await fs.readdir(IMAGES_DIR);
    for (const file of files) {
      if (file !== '.gitkeep' && !file.startsWith('seed-')) {
        await fs.unlink(path.join(IMAGES_DIR, file));
      }
    }
  });

  it('should return 201 when uploading valid photo with valid credentials', async () => {
    // Given / When
    const response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .set('Authorization', `Basic ${CREDENTIALS}`)
      .attach('photo', JPEG_BUFFER, 'test.jpg');

    // Then
    expect(response.status).toBe(201);
    expect(response.body).toEqual({});
    expect(await getPhotoUrl(TEST_ANNOUNCEMENT.id)).toBe(`/images/${TEST_ANNOUNCEMENT.id}.jpeg`);
  });

  it('should return 401 when Authorization header is missing', async () => {
    // Given / When
    const response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .attach('photo', JPEG_BUFFER, 'test.jpg');

    // Then
    expect(response.status).toBe(401);
    expect(response.body.error.code).toEqual('UNAUTHENTICATED');
    expect(await getPhotoUrl(TEST_ANNOUNCEMENT.id)).toBe(null);
  });

  it('should return 403 when Authorization passoword is invalid', async () => {
    // Given
    const credentials = Buffer.from(`${TEST_ANNOUNCEMENT.id}:wrongpassword`).toString('base64');

    // When
    const response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .set('Authorization', `Basic ${credentials}`)
      .attach('photo', JPEG_BUFFER, 'test.jpg');

    // Then
    expect(response.status).toBe(403);
    expect(response.body.error.code).toEqual('UNAUTHORIZED');
    expect(await getPhotoUrl(TEST_ANNOUNCEMENT.id)).toBe(null);
  });

  it('should return 404 when announcement does not exist', async () => {
    // Given
    const nonExistentId = 'ffffffff-ffff-ffff-ffff-ffffffffffff';
    const credentials = Buffer.from(`${nonExistentId}:${TEST_PASSWORD}`).toString('base64');

    // When
    const response = await request(server)
      .post(`/api/v1/announcements/${nonExistentId}/photos`)
      .set('Authorization', `Basic ${credentials}`)
      .attach('photo', JPEG_BUFFER, 'test.jpg');

    // Then
    expect(response.status).toBe(404);
    expect(response.body.error.code).toEqual('NOT_FOUND');
  });

  it('should return 400 when file format is invalid', async () => {
    // Given
    const invalidBuffer = Buffer.from([0x00, 0x01, 0x02, 0x03]);

    // When
    const response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .set('Authorization', `Basic ${CREDENTIALS}`)
      .attach('photo', invalidBuffer, 'test.txt');

    // Then
    expect(response.status).toBe(400);
    expect(response.body.error.code).toBe('INVALID_FILE_FORMAT');
    expect(await getPhotoUrl(TEST_ANNOUNCEMENT.id)).toBeNull();
  });

  it('should return 413 when file size exceeds 20MB limit', async () => {
    // Given
    const largeBuffer = Buffer.alloc(21 * 1024 * 1024, 0xFF);

    // When
    const response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .set('Authorization', `Basic ${CREDENTIALS}`)
      .attach('photo', largeBuffer, 'large.jpg');

    // Then
    expect(response.status).toBe(413);
    expect(response.body.error.code).toEqual('PAYLOAD_TOO_LARGE');
    expect(await getPhotoUrl(TEST_ANNOUNCEMENT.id)).toBeNull();
  });

  it('should return 400 when photo field is missing', async () => {
    // Given / When
    const response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .set('Authorization', `Basic ${CREDENTIALS}`)
      .field('name', 'test');

    // Then
    expect(response.status).toBe(400);
    expect(await getPhotoUrl(TEST_ANNOUNCEMENT.id)).toBeNull();
  });

  it('should replace existing photo when uploading new one', async () => {
    // Given / When
    let response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .set('Authorization', `Basic ${CREDENTIALS}`)
      .attach('photo', JPEG_BUFFER, 'test1.jpg');

    expect(response.status).toBe(201);
    const firstPhotoUrl = await getPhotoUrl(TEST_ANNOUNCEMENT.id);
    expect(firstPhotoUrl).toBeDefined();

    response = await request(server)
      .post(`/api/v1/announcements/${TEST_ANNOUNCEMENT.id}/photos`)
      .set('Authorization', `Basic ${CREDENTIALS}`)
      .attach('photo', JPEG_BUFFER, 'test2.png');

    // Then
    expect(response.status).toBe(201);
    const secondPhotoUrl = await getPhotoUrl(TEST_ANNOUNCEMENT.id);
    expect(secondPhotoUrl).toEqual(firstPhotoUrl);

    const photoFilename = secondPhotoUrl?.split('/').pop();
    expect(photoFilename).toBeDefined();
    if (photoFilename) {
      const photoPath = path.join(IMAGES_DIR, photoFilename);
      const stats = await fs.stat(photoPath);
      expect(stats.size).toBeGreaterThan(0);
    }
  });
});

