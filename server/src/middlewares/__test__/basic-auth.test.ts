import { describe, it, expect, beforeEach, vi } from 'vitest';
import type { Response, NextFunction } from 'express';
import basicAuthMiddleware, { type RequestWithBasicAuth } from '../basic-auth.ts';
import { UnauthenticatedError } from '../../lib/errors.ts';

describe('basicAuthMiddleware', () => {
  let req: Partial<RequestWithBasicAuth>;
  let res: Partial<Response>;
  let next: NextFunction;

  beforeEach(() => {
    req = {
      headers: {}
    };
    res = {};
    next = vi.fn();
  });

  describe('successful authentication', () => {
    it.each([
      ['Basic', 'admin', 'test-password'],
      ['basic', 'user', 'pass'],
      ['BASIC', 'john.doe', 'secure-pwd-123']
    ])('should parse credentials with scheme "%s" and attach to req', (scheme, username, password) => {
      // Given: valid Base64 encoded credentials
      const credentials = Buffer.from(`${username}:${password}`).toString('base64');
      req.headers = { authorization: `${scheme} ${credentials}` };

      // When: middleware is called
      basicAuthMiddleware(req as RequestWithBasicAuth, res as Response, next);

      // Then: credentials should be attached and next called
      expect(next).toHaveBeenCalledOnce();
      expect(req.basicAuth).toBeDefined();
      expect(req.basicAuth?.username).toBe(username);
      expect(req.basicAuth?.password).toBe(password);
    });
  });

  describe('authentication failures', () => {
    it.each<[string, () => void, string]>([
      [
        'missing Authorization header',
        () => {
          req.headers = {};
        },
        'header missing'
      ],
      [
        'invalid Base64 encoding',
        () => {
          req.headers = { authorization: 'Basic !!!invalid-base64!!!' };
        },
        'base64 invalid'
      ],
      [
        'no colon separator',
        () => {
          req.headers = { authorization: `Basic ${Buffer.from('invalidcredentials').toString('base64')}` };
        },
        'format invalid'
      ],
      [
        'empty username',
        () => {
          req.headers = { authorization: `Basic ${Buffer.from(':password').toString('base64')}` };
        },
        'username empty'
      ],
      [
        'empty password',
        () => {
          req.headers = { authorization: `Basic ${Buffer.from('username:').toString('base64')}` };
        },
        'password empty'
      ],
      [
        'Bearer scheme instead of Basic',
        () => {
          req.headers = { authorization: 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9' };
        },
        'scheme invalid'
      ]
    ])('should throw UnauthenticatedError when %s (%s)', (_scenario, setup, _detail) => {
      // Given: setup the invalid scenario
      setup();

      // When/Then: middleware should throw UnauthenticatedError
      expect(() => basicAuthMiddleware(req as RequestWithBasicAuth, res as Response, next)).toThrow(UnauthenticatedError);
    });
  });
});
