import { describe, it, expect, beforeEach, afterAll } from 'vitest';
import request from 'supertest';
import server from '../server.ts';
import { db } from '../database/db-utils.ts';

describe('POST /api/v1/users', () => {
  beforeEach(async () => {
    await db('user').del();
  });

  afterAll(async () => {
    await db('user').del();
  });

  describe('successful registration', () => {
    it('should return HTTP 201 with user id on successful registration', async () => {
      // given
      const payload = { email: 'persist@example.com', password: 'password123' };

      // when
      const response = await request(server)
        .post('/api/v1/users')
        .send(payload);

      // then
      expect(response.status).toBe(201);
      expect(response.body).toHaveProperty('id');
      expect(typeof response.body.id).toBe('string');

      const user = await db('user').where({ email: payload.email }).first();
      expect(user).toBeDefined();
      expect(user.password_hash).not.toBe(payload.password);
      expect(user.password_hash).toContain(':');
    });

    it('should normalize email addresses', async () => {
      // given
      const payload = { email: 'MixedCase@Example.COM', password: 'password123' };

      // when
      const response = await request(server)
        .post('/api/v1/users')
        .send(payload);

      // then
      expect(response.status).toBe(201);
      const user = await db('user').where({ email: 'mixedcase@example.com' }).first();
      expect(user).toBeDefined();
    });
  });

  describe('validation errors', () => {
    it.each([
      { email: 'invalid-email', password: 'password123', field: 'email', code: 'INVALID_FORMAT' },
      { email: 'user@', password: 'password123', field: 'email', code: 'INVALID_FORMAT' },
      { email: '@example.com', password: 'password123', field: 'email', code: 'INVALID_FORMAT' },
      { email: 'a'.repeat(243) + '@example.com', password: 'password123', field: 'email', code: 'INVALID_FORMAT' },
      { email: '', password: 'password123', field: 'email', code: 'MISSING_VALUE' },
      { email: 'user@example.com', password: 'short', field: 'password', code: 'INVALID_FORMAT' },
      { email: 'user@example.com', password: '1234567', field: 'password', code: 'INVALID_FORMAT' },
      { email: 'user@example.com', password: 'a'.repeat(129), field: 'password', code: 'INVALID_FORMAT' },
      { email: 'user@example.com', password: '', field: 'password', code: 'MISSING_VALUE' }
    ])('should return HTTP 400 with validation error for $field', async ({ email, password, field, code }) => {
      // when
      const response = await request(server)
        .post('/api/v1/users')
        .send({ email, password });

      // then
      expect(response.status).toBe(400);
      expect(response.body.error).toHaveProperty('requestId');
      expect(response.body.error.code).toBe(code);
      expect(response.body.error.field).toBe(field);
    });
  });

  describe('duplicate email', () => {
    it('should return HTTP 409 for duplicate email', async () => {
      // given
      const email = 'duplicate@example.com';
      const payload = { email, password: 'password123' };
      await request(server).post('/api/v1/users').send(payload);

      // when
      const response = await request(server)
        .post('/api/v1/users')
        .send(payload);

      // then
      expect(response.status).toBe(409);
      expect(response.body.error.code).toBe('CONFLICT');
    });
  });

});
