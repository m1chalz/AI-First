import type { Knex } from 'knex';
import knex from 'knex';
import knexConfig from '../../knexfile.ts';
import log from '../conf/logger.ts';

export const db = knex(knexConfig);

export async function runDbMigrations() {
  try {
    await db.migrate.latest();
    await db.seed.run();
    log.info('DB migrations ran successfully');
  } catch (error) {
    log.error(error, 'DB migration failed');
    process.exit(1);
  }
}

export type TransactionalWrapper = <T>(callback: (trx: Knex.Transaction) => Promise<T>) => Promise<T>;

export async function withTransaction<T>(callback: (trx: Knex.Transaction) => Promise<T>): Promise<T> {
  return db.transaction<T>(callback);
}
