import knex from 'knex';
import knexConfig from '../../knexfile.ts'
import log from '../lib/logger.ts';

export const db = knex(knexConfig)

export async function runDbMigrations() {
  try {
    await db.migrate.latest();
    await db.seed.run()
    log.info('DB migrations ran successfully');
  } catch (error) {
    log.error(error, 'DB migration failed');
    process.exit(1);
  }
}