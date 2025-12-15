import { describe, it, expect } from 'vitest';
import request from 'supertest';
import server from '../server.ts';

describe('Error responses', () => {
  it('should return 404 with structured error for non-existent routes', async () => {
    // Given

    // When
    const response = await request(server).get('/api/not/found').expect('Content-Type', /json/).expect(404);

    // Then
    expect(response.body).toEqual({
      error: {
        requestId: expect.any(String),
        code: 'NOT_FOUND',
        message: 'Resource not found'
      }
    });
  });

  it('should return 413 with structured error when payload exceeds size limit', async () => {
    // Given
    const largePayload = {
      data: 'x'.repeat(100 * 1024) // 100 KB string
    };

    // When
    const response = await request(server).post('/api/v1/announcements').send(largePayload).expect('Content-Type', /json/).expect(413);

    // Then
    expect(response.body).toEqual({
      error: {
        requestId: expect.any(String),
        code: 'PAYLOAD_TOO_LARGE',
        message: 'Request payload exceeds maximum size limit (100KB)'
      }
    });
  });
});
