# PetSpot Backend Constitution

> **Platform**: Backend (`/server`) | **Language**: TypeScript | **Framework**: Express.js | **Architecture**: Clean Code + TDD

This document contains all Backend-specific architectural rules and standards. Read this file for Backend-only tasks.

## Build & Test Commands

```bash
# Install dependencies
npm install  # run from server/

# Run dev server
npm run dev  # from server/, active on http://localhost:3000

# Run production server
npm start  # from server/

# Run backend tests
npm test  # from server/

# Run backend tests with coverage
npm test --coverage  # from server/
# View report at: server/coverage/index.html

# Run linter
npm run lint  # from server/

# Format code
npm run format  # from server/

# Create migration
npm run knex:add-migration <name>  # from server/
```

## Technology Stack

- **Runtime**: Node.js v24 (LTS)
- **Framework**: Express.js
- **Language**: TypeScript (strict mode)
- **Database**: Knex query builder + SQLite (designed for PostgreSQL migration)
- **Linting**: ESLint with TypeScript plugin
- **Testing**: Vitest (unit + integration) + SuperTest (API integration)

## Core Principles

### Platform Role

Backend (`/server`) is a standalone REST API:

- NOT consumed by platform code directly (only via HTTP)
- Serves Android, iOS, and Web platforms via REST API
- MUST be independently buildable, testable, and deployable

### 80% Unit Test Coverage (NON-NEGOTIABLE)

Backend MUST maintain minimum 80% unit test coverage:

- **Framework**: Vitest (unit) + SuperTest (integration)
- **Run commands**:
  - Unit tests: `npm test` (from server/)
  - With coverage: `npm test --coverage`
- **Report**: `server/coverage/index.html`
- **Scope**: Business logic (`/src/services`), utility functions (`/src/lib`), REST API endpoints (`/src/__test__/`)
- **Coverage target**: 80% line + branch coverage for both unit and integration tests

**Testing Requirements**:
- MUST test happy path, error cases, and edge cases
- MUST follow Given-When-Then structure
- MUST use descriptive test names
- MUST test behavior, not implementation details
- MUST use test doubles (fakes, mocks) for dependencies

### Interface-Based Design (NON-NEGOTIABLE)

**Interface definition** (`/server/src/database/repositories/`):
```typescript
interface IPetRepository {
    findAll(filter?: PetFilter): Promise<Pet[]>;
    findById(id: number): Promise<Pet | null>;
}
```

**Implementation** (`/server/src/database/repositories/`):
```typescript
class PetRepository implements IPetRepository {
    constructor(private db: Knex) {}
    
    async findAll(filter?: PetFilter): Promise<Pet[]> {
        let query = this.db<Pet>('pet');
        
        if (filter?.species) {
            query = query.where('species', filter.species);
        }
        
        return query.select('*');
    }
    
    async findById(id: number): Promise<Pet | null> {
        return this.db<Pet>('pet').where('id', id).first();
    }
}
```

### Dependency Injection (NON-NEGOTIABLE)

Backend MUST use manual dependency injection:

- Manual dependency injection (constructor injection)
- NO DI framework required for backend (simplicity preferred)

**Example** (`/server/src/`):
```typescript
// services/petService.ts
export function createPetService(repository: IPetRepository): PetService {
    return {
        async getAllPets(filter?: PetFilter): Promise<Pet[]> {
            return repository.findAll(filter);
        }
    };
}

// app.ts - wire dependencies
const db = setupDatabase();
const petRepository = new PetRepository(db);
const petService = createPetService(petRepository);
```

### Asynchronous Programming (NON-NEGOTIABLE)

Backend MUST use native async/await:

- MUST use native **async/await**
- Express handlers MUST be async functions

**Example**:
```typescript
router.get('/pets', async (req, res, next) => {
    try {
        const pets = await petService.getAllPets();
        res.status(200).json(pets);
    } catch (error) {
        next(error); // Delegate to error middleware
    }
});
```

**Prohibited Patterns**:
- ❌ Callbacks (except platform APIs that require them)
- ❌ Promise chains (`.then()`)
- ❌ RxJS or reactive patterns

### Public API Documentation

**Documentation Policy (MANDATORY)**:
- Backend code MUST NOT be documented or commented unless it is really unclear and hard to understand
- Code MUST be self-documenting through clear, descriptive naming
- Documentation/comments MUST only be added when code is genuinely difficult to understand
- Prefer refactoring unclear code to adding documentation/comments
- Inline comments explaining "what" code does are PROHIBITED
- Inline comments explaining "why" complex business logic exists MAY be acceptable if the reason is non-obvious

### Given-When-Then Test Convention (NON-NEGOTIABLE)

All unit tests MUST follow Given-When-Then structure with section comments:

```typescript
describe('petService', () => {
    it('should return all pets when repository has data', async () => {
        // given
        const mockPets = [
            { id: 1, name: 'Max', species: 'dog' },
            { id: 2, name: 'Luna', species: 'cat' }
        ];
        const fakeRepository = new FakePetRepository(mockPets);

        // when
        const result = await getAllPets(fakeRepository);

        // then
        expect(result).toHaveLength(mockPets.length);
        expect(result[0].name).toBe(mockPets[0].name);
    });
});
```

**Comment Format Requirements**:
- Comments MUST be lowercase: `// given`, `// when`, `// then`
- Comments MUST NOT include additional text
- MUST try to reuse variables from `// given` phase in `// then` phase

**Parameterized Tests**:
```typescript
describe('createPet', () => {
    it.each([
        ['Max', 'dog'],
        ['Luna', 'cat'],
        ['Buddy', 'dog']
    ])('should create pet with name=%s and species=%s', async (name, species) => {
        // given
        const pet = { name, species, ownerId: 1 };
        
        // when
        const result = await createPet(pet);
        
        // then
        expect(result.name).toBe(name);
        expect(result.species).toBe(species);
    });
});

// With description parameter when purpose is unclear
describe('validateEmail', () => {
    it.each([
        { email: 'user@example.com', expected: true, description: 'valid email with common domain' },
        { email: 'user+tag@example.co.uk', expected: true, description: 'valid email with plus and subdomain' },
        { email: 'invalid', expected: false, description: 'missing @ symbol' }
    ])('should return $expected for $description', ({ email, expected }) => {
        // given
        // when
        const result = validateEmail(email);
        // then
        expect(result).toBe(expected);
    });
});
```

## Backend Architecture & Quality Standards (NON-NEGOTIABLE)

### Code Quality Requirements

- MUST enable ESLint with TypeScript plugin (`@typescript-eslint/eslint-plugin`)
- MUST follow Clean Code principles:
  - Functions should be small, focused, and do one thing
  - Descriptive naming (no abbreviations except well-known ones like `id`, `db`, `api`)
  - Avoid deep nesting (max 3 levels)
  - Prefer composition over inheritance
  - DRY (Don't Repeat Yourself) - extract reusable logic
  - Self-documenting code (clear naming, no unnecessary documentation/comments)
- MUST reuse existing code, logic, and styles whenever possible
  - Check for existing utilities, helpers, and patterns before creating new ones
  - Extract reusable logic to `/src/lib/` for shared use
  - Avoid duplicating functionality that already exists
- MUST keep code simple
  - Prefer simple, straightforward solutions over complex ones
  - Avoid over-engineering and premature optimization
- MUST NOT produce any summary files during the implementation phase
- MUST minimize dependencies in `package.json`:
  - Only add dependencies that provide significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")
  - Document rationale for each dependency in comments
  - Regularly audit dependencies with `npm audit`

### Filename Conventions

- MUST use kebab-case convention for filenames
  - Examples: `pet-service.ts`, `announcement-repository.ts`, `error-handler.ts`
  - Test files: `pet-service.test.ts`, `announcement-repository.test.ts`
  - Exception: Configuration files may use other conventions if required by tooling

### Separation of Concerns

- **`/src/middlewares`**: Request/response processing (authentication, logging, validation, error handling)
  - MUST be testable in isolation
  - MUST NOT contain business logic
- **`/src/routes`**: Endpoint definitions and request routing ONLY
  - MUST be thin - delegate to services for business logic
  - MUST handle HTTP concerns (status codes, request/response mapping)
- **`/src/services`**: Business logic layer (pure functions, testable in isolation)
  - MUST contain all business rules, validation, calculations
  - MUST be framework-agnostic (no Express-specific code)
  - MUST be covered by unit tests in `/src/services/__test__/`
- **`/src/database`**: Database access layer (Knex queries, migrations, repositories)
  - MUST use Knex query builder (no raw SQL strings except complex queries)
  - MUST separate repository interfaces from implementations
  - Migration files MUST be versioned and reversible
- **`/src/lib`**: Utility functions and helpers (pure, reusable, no side effects)
  - MUST be framework-agnostic
  - MUST be covered by unit tests in `/src/lib/__test__/`

### TDD Workflow (MANDATORY)

Backend development MUST follow TDD (Red-Green-Refactor):

1. **RED**: Write a failing test first
2. **GREEN**: Write minimal code to make test pass
3. **REFACTOR**: Improve code quality without changing behavior

### Task Implementation Requirements

- MUST use TDD workflow for all tasks
- Each task MUST be atomic (complete and testable before moving to the next task)
- Task workflow MUST follow this sequence:
  1. **Start with tests**: Write failing tests first (RED phase)
  2. **Implement the logic**: Write minimal code to make tests pass (GREEN phase)
  3. **Finish with verification**: Run tests (they MUST succeed) and run linting (no issues expected)
  4. **Optimize the code and the tests**: Verify if the code or tests may be improved/simplified (e.g. by converting the tests to parameterized ones). If the code/tests were modified, make sure the tests and linting pass
  5. **Format the code**: Always format the code using `npm run format` at the end of each task
- MUST NOT create any files with summaries during task implementation
- Each task MUST be completed (tests passing, linting clean, code formatted) before starting the next task

### Database Layer Standards

- MUST use singular table names (e.g., `pet`, `user`)
- MUST use `IF EXISTS` / `IF NOT EXISTS` in all DDL statements for idempotent migrations
- MUST NOT use database-level enum types or CHECK constraints (store enums as strings, validate at application layer)
- SHOULD design for easy migration from SQLite to PostgreSQL

## Module Structure

**`/server/src/`** (Backend - Node.js/Express):

```
/server/src/
├── middlewares/       - Express middlewares (auth, logging, error handling)
├── routes/           - REST API endpoint definitions (Express routers)
├── services/         - Business logic layer (testable, pure functions)
│   └── __test__/     - Unit tests for services (MUST achieve 80% coverage)
├── database/         - Database configuration, migrations, query repositories
├── lib/              - Utility functions, helpers (pure, reusable)
│   └── __test__/     - Unit tests for lib functions (MUST achieve 80% coverage)
├── __test__/         - Integration tests for REST API endpoints
├── conf/             - Configuration files
├── types/            - TypeScript type definitions
├── app.ts            - Express app configuration (middleware setup, route registration)
└── index.ts          - Server entry point (port binding, startup)
```

## Testing Standards

### Unit Tests (MANDATORY)

- **Location**: `/src/services/__test__/`, `/src/lib/__test__/`
- **Framework**: Vitest
- **Scope**: Business logic and utilities in isolation
- **Coverage target**: 80% line + branch coverage
- MUST minimize number of test cases - cover all edge cases and happy paths, but don't duplicate similar cases
- MUST use parameterized tests when possible and worthwhile
- MUST add description parameter to parameterized tests ONLY if it's unclear why it's worth testing the given set of arguments
- MUST try to reuse variables from `// given` phase in `// then` phase

### Integration Tests (MANDATORY)

- **Location**: `/src/__test__/`
- **Framework**: Vitest + SuperTest
- **Scope**: REST API endpoints end-to-end (request → response)
- **Coverage target**: 80% coverage

## Compliance Checklist

All Backend pull requests MUST:

- [ ] Run tests: `npm test --coverage` (from server/)
- [ ] Verify 80%+ test coverage in `server/coverage/index.html`
- [ ] Run linter: `npm run lint` (from server/)
- [ ] Format code: `npm run format` (from server/)
- [ ] Verify Clean Code principles applied:
  - [ ] Small functions, max 3 nesting levels
  - [ ] DRY - no duplicate code
  - [ ] Self-documenting code (no unnecessary comments)
- [ ] Verify TDD workflow was followed (tests written before implementation)
- [ ] Verify dependencies minimized in `package.json`
- [ ] Verify all new tests follow Given-When-Then structure with `// given`, `// when`, `// then` comments
- [ ] Verify filename conventions (kebab-case)
- [ ] Verify separation of concerns:
  - [ ] Business logic in `/src/services/`
  - [ ] Utilities in `/src/lib/`
  - [ ] Routes are thin (delegate to services)
  - [ ] Middlewares don't contain business logic

---

**Version**: 1.0.0 | **Based on Constitution**: v2.5.10

