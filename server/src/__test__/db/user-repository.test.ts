import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { db } from '../../database/db-utils.ts';
import { UserRepository } from '../../database/repositories/user-repository.ts';

describe('UserRepository', () => {
  let repository: UserRepository;

  beforeAll(async () => {
    await db.migrate.latest();
    repository = new UserRepository(db);
  });

  afterAll(async () => {
    await db('user').del();
    await db.destroy();
  });

  describe('create', () => {
    it('should create user with generated id, email, and passwordHash', async () => {
      // given
      const email = 'user@example.com';
      const passwordHash = 'hashed_password';

      // when
      const result = await repository.create(email, passwordHash);

      // then
      expect(result.id).toBeDefined();
      expect(result.id).toMatch(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
      expect(result.email).toBe(email);
      expect(result.passwordHash).toBe(passwordHash);
      expect(result.createdAt).toBeDefined();
      expect(result.updatedAt).toBeDefined();
    });

    it('should set timestamps on creation', async () => {
      // given
      const email = 'test@example.com';
      const passwordHash = 'hashed';

      // when
      const result = await repository.create(email, passwordHash);

      // then
      expect(result.createdAt).toBeDefined();
      expect(result.updatedAt).toBeDefined();
    });
  });

  describe('findByEmail', () => {
    it('should find user by email', async () => {
      // given
      const email = 'findme@example.com';
      const passwordHash = 'hashed';
      await repository.create(email, passwordHash);

      // when
      const result = await repository.findByEmail('findme@example.com');

      // then
      expect(result).not.toBeNull();
      expect(result?.email).toBe('findme@example.com');
    });

    it('should return null if user not found', async () => {
      // when
      const result = await repository.findByEmail('nonexistent@example.com');

      // then
      expect(result).toBeNull();
    });
  });
});
