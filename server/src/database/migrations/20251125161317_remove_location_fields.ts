import type { Knex } from "knex";

export async function up(knex: Knex): Promise<void> {
  await knex.schema.alterTable('announcement', (table) => {
    table.dropColumn('location_city');
    table.dropColumn('location_radius');
  });
}

export async function down(knex: Knex): Promise<void> {
  await knex.schema.alterTable('announcement', (table) => {
    table.text('location_city').nullable();
    table.integer('location_radius').nullable();
  });
}

