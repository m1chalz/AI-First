import { describe, it, expect } from 'vitest';
import request from 'supertest';
import server from '../server.ts';

describe('Not Found Middleware', () => {
  it('should return 404 with structured error for non-existent routes', async () => {
    // Given: Non-existent API route
    
    // When: Client requests invalid route
    const response = await request(server)
      .get('/api/not/found')
      .expect('Content-Type', /json/)
      .expect(404);
    
    // Then: Structured error response returned
    expect(response.body).toEqual({
      error: {
        code: 'NOT_FOUND',
        message: 'Resource not found'
      }
    });
  });
});

