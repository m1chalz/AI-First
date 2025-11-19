import { describe, it, expect } from 'vitest';
import request from 'supertest';
import { app } from '../app.js';

describe('GET /api/v1/announcements', () => {

  it('should return 200 with announcements array when database has data', async () => {
    // Given: Database has seed data (8 announcements)
    
    // When: Client requests all announcements
    const response = await request(app).get('/api/v1/announcements');
    
    // Then: Returns HTTP 200 with JSON array containing announcements
    expect(response.status).toBe(200);
    expect(response.body).toHaveProperty('data');
    expect(Array.isArray(response.body.data)).toBe(true);
    expect(response.body.data.length).toBeGreaterThan(0);
    
    // Verify first announcement has all required fields
    const firstAnnouncement = response.body.data[0];
    expect(firstAnnouncement).toHaveProperty('id');
    expect(firstAnnouncement).toHaveProperty('petName');
    expect(firstAnnouncement).toHaveProperty('species');
    expect(firstAnnouncement).toHaveProperty('breed');
    expect(firstAnnouncement).toHaveProperty('gender');
    expect(firstAnnouncement).toHaveProperty('description');
    expect(firstAnnouncement).toHaveProperty('location');
    expect(firstAnnouncement).toHaveProperty('locationRadius');
    expect(firstAnnouncement).toHaveProperty('lastSeenDate');
    expect(firstAnnouncement).toHaveProperty('email');
    expect(firstAnnouncement).toHaveProperty('phone');
    expect(firstAnnouncement).toHaveProperty('photoUrl');
    expect(firstAnnouncement).toHaveProperty('status');
  });

});

