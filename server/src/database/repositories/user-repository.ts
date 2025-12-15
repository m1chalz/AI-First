import { v4 as uuidv4 } from 'uuid';
import type { Knex } from 'knex';
import type { User } from '../../types/user.ts';

interface UserRow {
  id: string;
  email: string;
  password_hash: string;
  created_at: Date;
  updated_at: Date;
}

export interface IUserRepository {
  create(email: string, passwordHash: string): Promise<User>;
  findByEmail(email: string): Promise<User | null>;
}

export class UserRepository implements IUserRepository {
  constructor(private db: Knex) { }

  async create(email: string, passwordHash: string): Promise<User> {
    const now = new Date();
    const row = {
      id: uuidv4(),
      email,
      password_hash: passwordHash,
      created_at: now,
      updated_at: now
    };

    await this.db('user').insert(row).returning('*');
    return this.findByEmail(email) as Promise<User>;
  }

  async findByEmail(email: string): Promise<User | null> {
    const row = await this.db('user').where({ email }).first();
    return row ? this.rowToEntity(row as UserRow) : null;
  }

  private rowToEntity(row: UserRow): User {
    return {
      id: row.id,
      email: row.email,
      passwordHash: row.password_hash,
      createdAt: row.created_at,
      updatedAt: row.updated_at
    };
  }
}
