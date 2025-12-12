import { promises as fs } from 'fs';
import path from 'path';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import request from 'supertest';
import server from '../server.ts';
import { db } from '../database/db-utils.ts';
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
  updated_at: '2025-11-19T10:00:00.000Z'
};

const CREDENTIALS = Buffer.from(`${TEST_ANNOUNCEMENT.id}:password`).toString('base64');

const JPEG_BUFFER = Buffer.from([
  0xff, 0xd8, 0xff, 0xe0, 0x00, 0x10, 0x4a, 0x46, 0x49, 0x46, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0xff, 0xdb, 0x00,
  0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0a, 0x0c, 0x14, 0x0d, 0x0c, 0x0b, 0x0b, 0x0c,
  0x19, 0x12, 0x13, 0x0f, 0x14, 0x1d, 0x1a, 0x1f, 0x1e, 0x1d, 0x1a, 0x1c, 0x1c, 0x20, 0x24, 0x2e, 0x27, 0x20, 0x22, 0x2c, 0x23, 0x1c, 0x1c,
  0x28, 0x37, 0x29, 0x2c, 0x30, 0x31, 0x34, 0x34, 0x34, 0x1f, 0x27, 0x39, 0x3d, 0x38, 0x32, 0x3c, 0x2e, 0x33, 0x34, 0x32, 0xff, 0xc0, 0x00,
  0x0b, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00, 0xff, 0xc4, 0x00, 0x1f, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01,
  0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0xff, 0xc4, 0x00,
  0xb5, 0x10, 0x00, 0x02, 0x01, 0x03, 0x03, 0x02, 0x04, 0x03, 0x05, 0x05, 0x04, 0x04, 0x00, 0x00, 0x01, 0x7d, 0x01, 0x02, 0x03, 0x00, 0x04,
  0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06, 0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08, 0x23, 0x42, 0xb1, 0xc1,
  0x15, 0x52, 0xd1, 0xf0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x34,
  0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63,
  0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a,
  0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6,
  0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1,
  0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xff, 0xda, 0x00, 0x08,
  0x01, 0x01, 0x00, 0x00, 0x3f, 0x00, 0xfb, 0xd4, 0xff, 0xd9
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
    const largeBuffer = Buffer.alloc(21 * 1024 * 1024, 0xff);

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
