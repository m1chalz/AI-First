import { describe, it, expect, beforeEach, afterAll } from 'vitest';
import request from 'supertest';
import { v4 as uuidv4 } from 'uuid';
import server from '../../server.ts';
import { db } from '../../database/db-utils.ts';
import { hashPassword } from '../../lib/password-management.ts';

describe('POST /api/v1/users/login', () => {
  const testUser = {
    email: 'login-test@example.com',
    password: 'password123',
    id: ''
  };

  afterAll(async () => {
    await db('user').del();
  });

  beforeEach(async () => {
    await db('user').del();
    testUser.id = uuidv4();
    const passwordHash = await hashPassword(testUser.password);
    await db('user').insert({
      id: testUser.id,
      email: testUser.email,
      password_hash: passwordHash
    });
  });

  describe('successful login (case-insensitive email)', () => {
    it.each([testUser.email, testUser.email.toUpperCase()])(
      'should return HTTP 200 with userId and accessToken for valid credentials',
      async () => {
        // given
        const payload = { email: testUser.email, password: testUser.password };

        // when
        const response = await request(server).post('/api/v1/users/login').send(payload);

        // then
        expect(response.status).toBe(200);
        expect(response.body.userId).toBe(testUser.id);
        expect(response.body).toHaveProperty('accessToken');

        const token = response.body.accessToken;
        expect(token.split('.')).toHaveLength(3);
        const [, payloadBase64] = token.split('.');
        const tokenPayload = JSON.parse(Buffer.from(payloadBase64, 'base64').toString());

        expect(tokenPayload.userId).toBe(testUser.id);
        expect(tokenPayload.iat).toBeDefined();
        expect(tokenPayload.exp).toBeDefined();
        expect(tokenPayload.exp - tokenPayload.iat).toBe(3600); // one hour in seconds
      }
    );
  });

  describe('authentication failures', () => {
    it('should return identical (HTTP 401) error responses for invalid email and wrong password', async () => {
      // given
      const invalidEmailPayload = { email: 'nonexistent@example.com', password: 'password123' };
      const wrongPasswordPayload = { email: testUser.email, password: 'wrongpassword' };

      // when
      const invalidEmailResponse = await request(server).post('/api/v1/users/login').send(invalidEmailPayload);
      const wrongPasswordResponse = await request(server).post('/api/v1/users/login').send(wrongPasswordPayload);

      // then
      expect(invalidEmailResponse.status).toBe(401);
      expect(invalidEmailResponse.body.error.message).toBe('Invalid email or password');
      expect(invalidEmailResponse.status).toBe(wrongPasswordResponse.status);
      expect(invalidEmailResponse.body.error.message).toBe(wrongPasswordResponse.body.error.message);
      expect(invalidEmailResponse.body.error.code).toBe(wrongPasswordResponse.body.error.code);
    });

    it('should have similar response times for invalid email vs wrong password (user enumeration prevention)', async () => {
      // given
      const invalidEmailPayload = { email: 'nonexistent@example.com', password: 'password123' };
      const wrongPasswordPayload = { email: testUser.email, password: 'wrongpassword' };

      // when
      const startInvalid = Date.now();
      await request(server).post('/api/v1/users/login').send(invalidEmailPayload);
      const invalidEmailTime = Date.now() - startInvalid;

      const startWrong = Date.now();
      await request(server).post('/api/v1/users/login').send(wrongPasswordPayload);
      const wrongPasswordTime = Date.now() - startWrong;

      // then
      const timeDifference = Math.abs(invalidEmailTime - wrongPasswordTime);
      expect(timeDifference).toBeLessThan(500);
    });
  });

  describe('validation errors', () => {
    it.each([
      { payload: { password: 'password123' }, field: 'email', scenario: 'missing email' },
      { payload: { email: 'user@example.com' }, field: 'password', scenario: 'missing password' },
      { payload: { email: 'not-an-email', password: 'password123' }, field: 'email', scenario: 'invalid email format' },
      { payload: { email: 'user@example.com', password: 'short' }, field: 'password', scenario: 'password too short' }
    ])('should return HTTP 400 for $scenario', async ({ payload, field }) => {
      // when
      const response = await request(server).post('/api/v1/users/login').send(payload);

      // then
      expect(response.status).toBe(400);
      expect(response.body.error.field).toBe(field);
    });
  });
});
