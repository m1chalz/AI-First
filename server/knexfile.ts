import type { Knex } from 'knex'

const config: Knex.Config = {
  client: 'better-sqlite3',
  connection: {
    filename: './pets.db'
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
}

export default config
