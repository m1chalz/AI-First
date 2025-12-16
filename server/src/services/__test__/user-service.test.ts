import { describe, it, expect, beforeEach, vi } from 'vitest';
import { UserService } from '../user-service.ts';
import { ConflictError, InvalidCredentialsError, ValidationError } from '../../lib/errors.ts';
import { IUserRepository } from '../../database/repositories/user-repository.ts';
import { hashPassword } from '../../lib/password-management.ts';

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

  describe('loginUser - successful authentication', () => {
    it('should return userId and accessToken for valid credentials', async () => {
      // given
      const email = 'user@example.com';
      const password = 'password123';
      const userId = 'user-123';
      const passwordHash = await hashPassword(password);
      mockRepository.findByEmail.mockResolvedValue({ id: userId, passwordHash });

      // when
      const result = await underTest.loginUser(email, password);

      // then
      expect(result.userId).toBe(userId);
      expect(result.accessToken).toBeDefined();
      expect(typeof result.accessToken).toBe('string');
      expect(result.accessToken.split('.')).toHaveLength(3);
    });
  });

  describe('loginUser - authentication failures', () => {
    it('should throw InvalidCredentialsError for non-existent email', async () => {
      // given
      const email = 'nonexistent@example.com';
      const password = 'password123';
      mockRepository.findByEmail.mockResolvedValue(null);

      // when & then
      await expect(underTest.loginUser(email, password)).rejects.toThrow(InvalidCredentialsError);
    });

    it('should throw InvalidCredentialsError for incorrect password', async () => {
      // given
      const email = 'user@example.com';
      const correctPassword = 'correctPassword123';
      const wrongPassword = 'wrongPassword456';
      const userId = 'user-123';
      const passwordHash = await hashPassword(correctPassword);
      mockRepository.findByEmail.mockResolvedValue({ id: userId, passwordHash });

      // when & then
      await expect(underTest.loginUser(email, wrongPassword)).rejects.toThrow(InvalidCredentialsError);
    });

    it('should return identical error messages for invalid email and wrong password', async () => {
      // given
      const email = 'user@example.com';
      const password = 'password123';
      const passwordHash = await hashPassword('different-password');

      mockRepository.findByEmail.mockResolvedValueOnce(null);
      let errorForMissingEmail: Error | undefined;
      try {
        await underTest.loginUser(email, password);
      } catch (e) {
        errorForMissingEmail = e as Error;
      }

      mockRepository.findByEmail.mockResolvedValueOnce({ id: 'user-123', passwordHash });
      let errorForWrongPassword: Error | undefined;
      try {
        await underTest.loginUser(email, password);
      } catch (e) {
        errorForWrongPassword = e as Error;
      }

      // then
      expect(errorForMissingEmail?.message).toBe(errorForWrongPassword?.message);
      expect(errorForMissingEmail?.message).toBe('Invalid email or password');
    });
  });

  describe('loginUser - validation errors', () => {
    it('should throw ValidationError', async () => {
      // given
      const invalidEmail = 'not-an-email';
      const password = 'password123';
      mockValidator.mockImplementation(() => {
        throw new ValidationError('INVALID_FORMAT', 'email format is invalid', 'email');
      });

      // when & then
      await expect(underTest.loginUser(invalidEmail, password)).rejects.toThrow(ValidationError);
    });
  });
});
