import type { Knex } from "knex";

export async function up(knex: Knex): Promise<void> {
  await knex.schema.dropTableIfExists('announcement');
  
  await knex.schema.createTable('announcement', (table) => {
    table.text('id').primary();
    
    table.text('pet_name').nullable();
    table.text('species').notNullable();
    table.text('breed').nullable();
    table.text('sex').notNullable();
    table.integer('age').nullable();
    table.text('description').nullable();
    table.text('microchip_number').nullable().unique();
    
    table.text('location_city').nullable();
    table.float('location_latitude').notNullable();
    table.float('location_longitude').notNullable();
    table.integer('location_radius').nullable();
    
    table.text('email').nullable();
    table.text('phone').nullable();
    
    table.text('photo_url').notNullable();
    table.text('last_seen_date').notNullable();
    table.text('status').notNullable();
    table.text('reward').nullable();
    
    table.text('management_password_hash').notNullable();
    
    table.timestamp('created_at').notNullable().defaultTo(knex.fn.now());
    table.timestamp('updated_at').notNullable().defaultTo(knex.fn.now());
  });
}

export async function down(knex: Knex): Promise<void> {
  await knex.schema.dropTableIfExists('announcement');
}

