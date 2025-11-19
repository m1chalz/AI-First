# Quickstart: Lost Pets API Endpoint

**Feature**: 006-pets-api  
**Date**: 2025-11-19  
**Prerequisites**: Node.js v24 (LTS), npm, Git

## Overview

This quickstart guide provides setup instructions for developing and testing the Lost Pets API endpoint. Follow these steps to get the backend running locally and verify the implementation.

## Setup Instructions

### 1. Environment Setup

**Check Node.js version** (must be v24 LTS):

```bash
node --version
# Expected: v24.x.x
```

If not using Node.js v24, install it via [nvm](https://github.com/nvm-sh/nvm) (recommended):

```bash
nvm install 24
nvm use 24
```

### 2. Install Dependencies

Navigate to the server directory and install packages:

```bash
cd /Users/pawelkedra/code/AI-First/server
npm install
```

**Note**: No new dependencies required for this feature. Uses existing packages:
- Express (web framework)
- Knex (query builder)
- SQLite3 (database driver)
- Vitest (test runner)
- SuperTest (API testing)
- ESLint with TypeScript plugin (code quality)

### 3. Database Setup

#### Create Migration

Generate a new Knex migration for the `announcement` table:

```bash
cd /Users/pawelkedra/code/AI-First/server
npm run knex:add-migration create_announcement_table
```

This creates a timestamped migration file in `src/database/migrations/`. 

**Important**: The existing empty migration `20251115231122_pets.ts` has been removed.

Implement the migration based on the schema in [data-model.md](./data-model.md):
- Use `CREATE TABLE IF NOT EXISTS` for safety
- No CHECK constraints (all validation in application layer)
- No database-level enum types (store as VARCHAR)

#### Run Migration

Apply the migration to create the `announcement` table:

```bash
npm run knex:migrate
```

**Verify Migration**:

```bash
npm run knex:migrate:status
```

Expected output: "1 migration completed" (or similar)

#### Create Seed File

Create a seed file in `src/database/seeds/` for test data:

```bash
mkdir -p src/database/seeds
touch src/database/seeds/001_announcements.ts
```

Implement the seed file with 5-10 example announcements (see [data-model.md](./data-model.md) for examples).

**Seed file structure**:
```typescript
import type { Knex } from 'knex';
import { randomUUID } from 'crypto';

export async function seed(knex: Knex): Promise<void> {
  // Clear existing data
  await knex('announcement').del();

  // Insert seed data
  await knex('announcement').insert([
    {
      id: randomUUID(),
      pet_name: 'Max',
      // ... more fields
    },
    // ... more announcements
  ]);
}
```

#### Run Seed

Populate the database with test announcements:

```bash
npm run knex:seed
```

**Verify Seed**:

```bash
sqlite3 pets.db "SELECT COUNT(*) FROM announcement;"
# Expected: 5-10 (number of seed announcements)
```

### 4. Development Workflow (TDD)

Follow Test-Driven Development (Red-Green-Refactor):

#### Step 1: RED - Write Failing Tests

**Integration Test** (`src/__test__/announcements.test.ts`):

```bash
# Create test file
touch src/__test__/announcements.test.ts
```

Write tests for:
- GET /api/v1/announcements returns 200 with data
- GET /api/v1/announcements returns 200 with empty array

**Note**: HTTP 500 tests and unexpected parameter tests are skipped for this phase (focus on happy path)

**Unit Tests**:

```bash
# Create service test file
mkdir -p src/services/__test__
touch src/services/__test__/announcement-service.test.ts

# Create validator test file
mkdir -p src/lib/__test__
touch src/lib/__test__/validators.test.ts
```

Write unit tests for service and validation functions.

**Run Tests (should fail)**:

```bash
npm test
# Expected: All tests fail (implementation not done yet)
```

#### Step 2: GREEN - Implement Minimal Code

**Create Files**:

```bash
# Types
mkdir -p src/types
touch src/types/announcement.d.ts

# Repository
mkdir -p src/database/repositories
touch src/database/repositories/announcement-repository.ts

# Service
touch src/services/announcement-service.ts

# Validators
touch src/lib/validators.ts

# Route
touch src/routes/announcements.ts
```

**Implement Code**:
1. Define TypeScript types in `src/types/announcement.d.ts`
2. Implement repository with Knex queries
3. Implement service layer calling repository
4. Implement validators (email, phone)
5. Implement route handler
6. Register router in `src/routes/index.ts`

**Run Tests (should pass)**:

```bash
npm test
# Expected: All tests pass
```

#### Step 3: REFACTOR - Improve Code Quality

- Apply Clean Code principles:
  - Extract reusable functions
  - Improve naming
  - Reduce nesting (max 3 levels)
  - Add JSDoc documentation
- Run ESLint and fix issues:

```bash
npm run lint
npm run lint -- --fix
```

- Verify test coverage (80% target):

```bash
npm test -- --coverage
```

**Coverage reports**:
- HTML report: `server/coverage/index.html`
- Terminal summary shows coverage percentages

### 5. Running the Server

#### Development Mode (with auto-reload)

```bash
cd /Users/pawelkedra/code/AI-First/server
npm run dev
```

Server starts on http://localhost:3000 (or port specified in environment variable).

#### Production Mode

```bash
npm start
```

### 6. Manual Testing

#### Test GET /api/v1/announcements

**Using curl**:

```bash
# Get all announcements (should return seed data)
curl -i http://localhost:3000/api/v1/announcements

# Expected response:
# HTTP/1.1 200 OK
# Content-Type: application/json
# { "data": [ ... announcements ... ] }
```

**Using HTTPie** (if installed):

```bash
http GET localhost:3000/api/v1/announcements
```

**Using browser**:

Navigate to: http://localhost:3000/api/v1/announcements

#### Verify Response Structure

Response should match OpenAPI schema:

```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "petName": "Max",
      "species": "DOG",
      "breed": "Golden Retriever",
      "gender": "MALE",
      "description": "Friendly golden retriever with red collar.",
      "location": "Central Park, New York, NY",
      "locationRadius": 5,
      "lastSeenDate": "2025-11-18",
      "email": "john@example.com",
      "phone": "+1-555-0101",
      "photoUrl": "https://example.com/photos/max.jpg",
      "status": "ACTIVE"
    }
  ]
}
```

#### Test Edge Cases

**Empty database**:

```bash
# Clear announcements
sqlite3 pets.db "DELETE FROM announcement;"

# Request should return empty array
curl http://localhost:3000/api/v1/announcements
# Expected: { "data": [] }

# Re-seed database
npm run knex:seed
```

### 7. Automated Testing

#### Run All Tests

```bash
cd /Users/pawelkedra/code/AI-First/server
npm test
```

Expected output:
- All tests pass (green checkmarks)
- Unit tests: service functions, validators
- Integration tests: API endpoints

#### Run Tests with Coverage

```bash
npm test -- --coverage
```

Expected output:
- Coverage summary in terminal
- HTML report at `server/coverage/index.html`
- **Target**: 80% line + branch coverage

Open HTML report:

```bash
# macOS
open coverage/index.html

# Linux
xdg-open coverage/index.html
```

#### Run Specific Test File

```bash
# Integration tests only
npm test -- src/__test__/announcements.test.ts

# Service unit tests only
npm test -- src/services/__test__/announcement-service.test.ts

# Validator unit tests only
npm test -- src/lib/__test__/validators.test.ts
```

#### Watch Mode (auto-rerun on file changes)

```bash
npm test -- --watch
```

Press keys in watch mode:
- `a` - Run all tests
- `f` - Run only failed tests
- `q` - Quit watch mode

### 8. Code Quality Checks

#### Run ESLint

```bash
npm run lint
```

Expected: No errors or warnings

#### Auto-fix ESLint Issues

```bash
npm run lint -- --fix
```

#### TypeScript Type Check

```bash
npx tsc --noEmit
```

Expected: No type errors

### 9. Database Inspection

#### Using SQLite CLI

```bash
# Open database
sqlite3 pets.db

# List tables
.tables
# Expected: announcement, knex_migrations, knex_migrations_lock

# Describe announcement table schema
.schema announcement

# Query announcements
SELECT * FROM announcement;

# Count announcements
SELECT COUNT(*) FROM announcement;

# Query by status
SELECT pet_name, species, status FROM announcement WHERE status = 'ACTIVE';

# Exit
.quit
```

#### Using Database GUI Tools

**DBeaver** (cross-platform):
1. Download from https://dbeaver.io/
2. Create new SQLite connection
3. Browse to `pets.db` file
4. Explore tables and data

**DB Browser for SQLite** (cross-platform):
1. Download from https://sqlitebrowser.org/
2. Open `pets.db`
3. Browse Data tab → Select `announcement` table

### 10. API Documentation

#### View OpenAPI Spec

```bash
cat specs/006-pets-api/contracts/announcements-api.yaml
```

#### Swagger UI (optional)

Install Swagger UI Express (optional dependency):

```bash
npm install --save-dev swagger-ui-express
```

Add route in `src/app.ts`:

```typescript
import swaggerUi from 'swagger-ui-express';
import YAML from 'yamljs';

const swaggerDocument = YAML.load('./specs/006-pets-api/contracts/announcements-api.yaml');
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
```

Access Swagger UI: http://localhost:3000/api-docs

### 11. Troubleshooting

#### Port Already in Use

```bash
# Error: EADDRINUSE: address already in use :::3000

# Solution: Find and kill process using port 3000
lsof -ti:3000 | xargs kill -9

# Or change port via environment variable
PORT=3001 npm run dev
```

#### Database Locked Error

```bash
# Error: SQLITE_BUSY: database is locked

# Solution: Close all connections to database
# - Stop all running server instances
# - Close database GUI tools (DBeaver, DB Browser)
# - Restart server
```

#### Migration Issues

```bash
# Rollback last migration
npm run knex:migrate:rollback

# Rollback all migrations
npm run knex:migrate:rollback --all

# Re-run migrations
npm run knex:migrate
```

#### Test Database Conflicts

If tests interfere with each other:
1. Ensure tests use isolated database setup (in-memory SQLite or separate test DB)
2. Use `beforeEach` to reset database state
3. Clean up test data in `afterEach`

Example test setup:

```typescript
beforeEach(async () => {
  await knex.migrate.latest();
  await knex.seed.run();
});

afterEach(async () => {
  await knex('announcement').del();
});
```

### 12. Next Steps

After completing this quickstart:

1. ✅ Verify all tests pass (`npm test`)
2. ✅ Verify 80% coverage (`npm test -- --coverage`)
3. ✅ Verify ESLint passes (`npm run lint`)
4. ✅ Manual API testing (curl/browser)
5. ✅ Inspect database (SQLite CLI or GUI)
6. 📝 Review implementation against spec ([spec.md](./spec.md))
7. 📝 Review constitution compliance ([plan.md](./plan.md))
8. 🚀 Ready for code review and merge

### 13. Related Documentation

- **Feature Specification**: [spec.md](./spec.md) - Requirements and acceptance criteria
- **Implementation Plan**: [plan.md](./plan.md) - Technical context and constitution checks
- **Research Document**: [research.md](./research.md) - Technology decisions and rationale
- **Data Model**: [data-model.md](./data-model.md) - Database schema and entity definitions
- **API Contract**: [contracts/announcements-api.yaml](./contracts/announcements-api.yaml) - OpenAPI specification

### 14. Common Commands Reference

```bash
# Development
npm run dev              # Start dev server with auto-reload
npm start                # Start production server
npm test                 # Run all tests
npm test -- --coverage   # Run tests with coverage report
npm run lint             # Run ESLint
npm run lint -- --fix    # Auto-fix ESLint issues

# Database
npm run knex:migrate            # Run migrations
npm run knex:migrate:rollback   # Rollback last migration
npm run knex:migrate:status     # Check migration status
npm run knex:seed               # Run seed files
npm run knex:add-migration <name>  # Create new migration

# Testing
npm test -- --watch                           # Watch mode
npm test -- src/__test__/announcements.test.ts  # Run specific test file
npm test -- --coverage --reporter=html        # HTML coverage report

# Database Inspection
sqlite3 pets.db                 # Open SQLite CLI
sqlite3 pets.db "SELECT * FROM announcement;"  # Query from command line
```

## Success Criteria Checklist

Verify implementation meets all success criteria from [spec.md](./spec.md):

- [ ] **SC-001**: API responds in <2 seconds for datasets up to 1000 records
- [ ] **SC-002**: Returns HTTP 200 for 100% of successful requests
- [ ] **SC-003**: Returns valid JSON structure in 100% of requests
- [ ] **SC-004**: Empty database returns empty array with HTTP 200
- [ ] **SC-005**: All announcements include required fields with valid data types
- [ ] **SC-006**: Seed data successfully populates 5-10 example announcements

## Performance Benchmarking (Optional)

Test response time for large datasets:

```bash
# Using Apache Bench (if installed)
ab -n 1000 -c 10 http://localhost:3000/api/v1/announcements

# Using curl with timing
time curl -s http://localhost:3000/api/v1/announcements > /dev/null
```

Target: <2 seconds for datasets up to 1000 records (per SC-001).

## Support

For questions or issues:
1. Review [spec.md](./spec.md) for requirements
2. Check [research.md](./research.md) for design decisions
3. Inspect [data-model.md](./data-model.md) for schema details
4. Consult OpenAPI spec: [contracts/announcements-api.yaml](./contracts/announcements-api.yaml)
5. Review project constitution: `.specify/memory/constitution.md`

