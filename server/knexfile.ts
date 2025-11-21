import type { Knex } from 'knex'

const DB_FILENAME = process.env.NODE_ENV === 'test' ? './pets-its.db' : './pets.db';

const config: Knex.Config = {
  client: 'better-sqlite3',
  connection: {
    filename: DB_FILENAME
  },
  migrations: {
    tableName: 'knex_migrations',
    directory: './src/database/migrations',
    extension: 'ts',
  },
  seeds: {
    directory: './src/database/seeds',
    extension: 'ts',
  },
  useNullAsDefault: true
}

export default config
