import { describe, it, expect, beforeEach, vi } from 'vitest';
import { UserService } from '../user-service.ts';
import { ConflictError } from '../../lib/errors.ts';
import { IUserRepository } from '../../database/repositories/user-repository.ts';

const mockRepository = {
  create: vi.fn(),
  findByEmail: vi.fn()
};

const mockValidator = vi.fn();

describe('UserService', () => {
  let underTest: UserService;

  beforeEach(() => {
    underTest = new UserService(mockRepository as IUserRepository, mockValidator);
    vi.clearAllMocks();
  });

  describe('registerUser - successful registration', () => {
    it.each([
      { email: 'user@example.com', password: 'password123' },
      { email: 'test.user+tag@domain.co.uk', password: 'MyP@ss123' },
      { email: 'USER@EXAMPLE.COM', password: '12345678' }
    ])('should create user with email normalized when valid', async ({ email, password }) => {
      // given
      const expectedId = '550e8400-e29b-41d4-a716-446655440000';
      mockRepository.create.mockResolvedValue({ id: expectedId });
      mockRepository.findByEmail.mockResolvedValue(null);

      // when
      const result = await underTest.registerUser(email, password);

      // then
      expect(result.id).toBe(expectedId);
      expect(mockRepository.findByEmail).toHaveBeenCalledWith(email.toLowerCase());
      expect(mockRepository.create).toHaveBeenCalled();
    });
  });

  describe('registerUser - duplicate email', () => {
    it('should reject duplicate email', async () => {
      // given
      const email = 'existing@example.com';
      mockRepository.findByEmail.mockResolvedValue({ id: 'existing-user' });

      // when & then
      await expect(underTest.registerUser(email, 'password123')).rejects.toThrow(ConflictError);
    });
  });
});
