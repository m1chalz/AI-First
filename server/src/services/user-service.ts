import { hashPassword, verifyPassword } from '../lib/password-management.ts';
import { ConflictError, InvalidCredentialsError } from '../lib/errors.ts';
import type { IUserRepository } from '../database/repositories/user-repository.ts';
import { generateToken } from '../lib/jwt-utils.ts';
import type { AuthRequest, AuthResponse } from '../types/auth.js';

const DUMMY_HASH = '0'.repeat(32) + ':' + '0'.repeat(128);

export class UserService {
  constructor(
    private repository: IUserRepository,
    private validator: (data: AuthRequest) => void
  ) { }

  async registerUser(data: AuthRequest): Promise<AuthResponse> {
    this.validator(data);
    const { email, password } = data as { email: string; password: string };

    const normalizedEmail = email.toLowerCase();
    const existingUser = await this.repository.findByEmail(normalizedEmail);

    if (existingUser) {
      throw new ConflictError('Email already exists');
    }

    const passwordHash = await hashPassword(password);
    await this.repository.create(normalizedEmail, passwordHash);
    return this.loginUser(data);
  }

  async loginUser(data: AuthRequest): Promise<AuthResponse> {
    this.validator(data);
    const { email, password } = data as { email: string; password: string };

    const normalizedEmail = email.toLowerCase();
    const user = await this.repository.findByEmail(normalizedEmail);

    // Always verify password to prevent timing attacks (user enumeration prevention)
    const hashToVerify = user?.passwordHash ?? DUMMY_HASH;
    const isValidPassword = await verifyPassword(password, hashToVerify);

    if (!user || !isValidPassword) {
      throw new InvalidCredentialsError();
    }

    const accessToken = generateToken(user.id);
    return { userId: user.id, accessToken };
  }
}
