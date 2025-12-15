import { hashPassword } from '../lib/password-management.ts';
import { ConflictError } from '../lib/errors.ts';
import type { IUserRepository } from '../database/repositories/user-repository.ts';
import { CreateUserRequest } from '../lib/user-validation.ts';
import { User } from '../types/user.js';

export class UserService {
  constructor(
    private repository: IUserRepository,
    private validator: (data: CreateUserRequest) => void
  ) { }

  async registerUser(email: string, password: string): Promise<User> {
    this.validator({ email, password });

    const normalizedEmail = email.toLowerCase();
    const existingUser = await this.repository.findByEmail(normalizedEmail);

    if (existingUser) {
      throw new ConflictError('Email already exists');
    }

    const passwordHash = await hashPassword(password);

    return this.repository.create(normalizedEmail, passwordHash);
  }
}
