import knex from 'knex';
import knexConfig from '../../knexfile.ts'

export const db = knex(knexConfig)

export async function runDbMigrations() {
  try {
    await db.migrate.latest();
    await db.seed.run()
    console.log('DB migrations ran successfully');
  } catch (error) {
    console.error('DB migration failed:', error);
    process.exit(1);
  }
}