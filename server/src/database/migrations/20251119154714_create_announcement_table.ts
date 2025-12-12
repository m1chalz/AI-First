import type { Knex } from 'knex';

export async function up(knex: Knex): Promise<void> {
  await knex.schema.createTableIfNotExists('announcement', (table) => {
    table.text('id').primary().notNullable();
    table.string('pet_name', 100).notNullable();
    table.string('species', 20).notNullable();
    table.string('breed', 100).nullable();
    table.string('gender', 20).notNullable();
    table.string('description', 1000).notNullable();
    table.string('location', 255).notNullable();
    table.integer('location_radius').nullable();
    table.text('last_seen_date').notNullable();
    table.string('email', 255).nullable();
    table.string('phone', 50).nullable();
    table.string('photo_url', 500).nullable();
    table.string('status', 20).notNullable().defaultTo('ACTIVE');
    table.timestamp('created_at').notNullable().defaultTo(knex.fn.now());
    table.timestamp('updated_at').notNullable().defaultTo(knex.fn.now());
  });
}

export async function down(knex: Knex): Promise<void> {
  await knex.schema.dropTableIfExists('announcement');
}
