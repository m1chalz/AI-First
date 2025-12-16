import { describe, it, expect, beforeEach, afterAll } from 'vitest';
import request from 'supertest';
import server from '../../server.ts';
import { db } from '../../database/db-utils.ts';

describe('POST /api/v1/users', () => {
  beforeEach(async () => {
    await db('user').del();
  });

  afterAll(async () => {
    await db('user').del();
  });

  describe('successful registration with JWT', () => {
    it('should return HTTP 201 with userId and accessToken on successful registration', async () => {
      // given
      const payload = { email: 'persist@example.com', password: 'password123' };

      // when
      const response = await request(server).post('/api/v1/users').send(payload);

      // then
      expect(response.status).toBe(201);
      expect(response.body).toHaveProperty('userId');
      expect(typeof response.body.userId).toBe('string');
      expect(response.body).toHaveProperty('accessToken');
      expect(typeof response.body.accessToken).toBe('string');

      const user = await db('user').where({ email: payload.email }).first();
      expect(user).toBeDefined();
      expect(user.password_hash).not.toBe(payload.password);
      expect(user.password_hash).toContain(':');
    });

    it('should normalize email addresses', async () => {
      // given
      const payload = { email: 'MixedCase@Example.COM', password: 'password123' };

      // when
      const response = await request(server).post('/api/v1/users').send(payload);

      // then
      expect(response.status).toBe(201);
      const user = await db('user').where({ email: 'mixedcase@example.com' }).first();
      expect(user).toBeDefined();
    });
  });

  describe('validation errors', () => {
    it.each([
      { email: 'invalid-email', password: 'password123', field: 'email' },
      { email: 'user@', password: 'password123', field: 'email' },
      { email: '@example.com', password: 'password123', field: 'email' },
      { email: 'a'.repeat(243) + '@example.com', password: 'password123', field: 'email' },
      { email: '', password: 'password123', field: 'email' },
      { email: 'user@example.com', password: 'short', field: 'password' },
      { email: 'user@example.com', password: '1234567', field: 'password' },
      { email: 'user@example.com', password: 'a'.repeat(129), field: 'password' },
      { email: 'user@example.com', password: '', field: 'password' }
    ])('should return HTTP 400 with validation error for $field', async ({ email, password, field }) => {
      // when
      const response = await request(server).post('/api/v1/users').send({ email, password });

      // then
      expect(response.status).toBe(400);
      expect(response.body.error).toHaveProperty('requestId');
      expect(response.body.error.code).toBe('INVALID_FORMAT');
      expect(response.body.error.field).toBe(field);
    });

    it.each([{ email: 'user@example.com' }, { password: '12345678' }])(
      'should return HTTP 400 when one of the fields is missing',
      async (requestBody) => {
        // when
        const response = await request(server).post('/api/v1/users').send(requestBody);

        // then
        expect(response.status).toBe(400);
        expect(response.body.error).toHaveProperty('requestId');
        expect(response.body.error.code).toBe('MISSING_VALUE');
      }
    );

    it('should return HTTP 409 for duplicate email', async () => {
      // given
      const email = 'duplicate@example.com';
      const payload = { email, password: 'password123' };
      await request(server).post('/api/v1/users').send(payload);

      // when
      const response = await request(server).post('/api/v1/users').send(payload);

      // then
      expect(response.status).toBe(409);
      expect(response.body.error.code).toBe('CONFLICT');
    });
  });
});
