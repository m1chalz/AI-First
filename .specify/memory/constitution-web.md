# PetSpot Web Constitution

> **Platform**: Web (`/webApp`) | **Language**: TypeScript | **UI**: React 18 | **Architecture**: Clean Code + Hooks

This document contains all Web-specific architectural rules and standards. Read this file for Web-only tasks.

## Build & Test Commands

```bash
# Install dependencies
npm install  # run from webApp/

# Run dev server
npm run start  # from webApp/

# Build web app
npm run build  # from webApp/

# Run tests with coverage
npm test --coverage  # from webApp/
# View report at: webApp/coverage/index.html

# Run linter
npm run lint  # from webApp/

# Format code
npm run format  # from webApp/
```

## Technology Stack

- **Framework**: React 18
- **Language**: TypeScript (strict mode)
- **Build Tool**: Vite
- **Testing**: Vitest + React Testing Library
- **Linting**: ESLint with TypeScript plugin
- **Target**: ES2015

## Core Principles

### Platform Independence

Web (`/webApp`) implements its full technology stack independently:

- Domain models (TypeScript interfaces/types)
- Service layer / business logic (TypeScript)
- State management (React hooks, Context, or state libraries)
- UI layer (React + TypeScript)
- Own dependency injection setup (native TypeScript patterns, Context, or DI libraries)

**Architecture Rules**:
- MUST NOT share compiled code with other platforms
- MAY share design patterns and architectural conventions
- MUST consume backend APIs via HTTP/REST
- MUST be independently buildable, testable, and deployable
- Domain models MAY differ from other platforms based on Web-specific needs

### 80% Unit Test Coverage (NON-NEGOTIABLE)

Web MUST maintain minimum 80% unit test coverage:

- **Location**: `/webApp/src/hooks/__test__/`, `/webApp/src/lib/__test__/`
- **Framework**: Vitest + React Testing Library
- **Run command**: `npm test --coverage` (from webApp/)
- **Report**: `webApp/coverage/index.html`
- **Scope**: Business logic in hooks and lib functions (MUST achieve 80% coverage)
- **Coverage target**: 80% line + branch coverage

**Testing Requirements**:
- MUST test happy path, error cases, and edge cases
- MUST follow Given-When-Then structure
- MUST use descriptive test names
- MUST test behavior, not implementation details
- MUST use test doubles (mocks, fakes) for dependencies

### Interface-Based Design (NON-NEGOTIABLE)

**Interface definition** (`/webApp/src/services/`):
```typescript
interface PetService {
    getPets(): Promise<Pet[]>;
    getPetById(id: string): Promise<Pet>;
}
```

**Implementation** (`/webApp/src/services/`):
```typescript
class PetServiceImpl implements PetService {
    constructor(private httpClient: HttpClient) {}
    
    async getPets(): Promise<Pet[]> {
        return this.httpClient.get<Pet[]>('/api/pets');
    }
    
    async getPetById(id: string): Promise<Pet> {
        return this.httpClient.get<Pet>(`/api/pets/${id}`);
    }
}
```

### Dependency Injection (RECOMMENDED)

Web SHOULD use React Context for dependency injection:

- SHOULD use React Context for dependency injection
- MAY use native TypeScript patterns (factory functions, service locator)
- MAY use DI libraries (InversifyJS, TSyringe) if team prefers
- Rationale: Flexibility for web ecosystem, React Context is idiomatic for React apps

**Example** (`/webApp/src/contexts/`):
```typescript
export const ServiceContext = React.createContext<Services | null>(null);

export function ServiceProvider({ children }: { children: React.ReactNode }) {
    const services = {
        petService: new PetServiceImpl(httpClient),
        httpClient: new HttpClient()
    };
    
    return (
        <ServiceContext.Provider value={services}>
            {children}
        </ServiceContext.Provider>
    );
}

// Usage in component
export function usePetService() {
    const services = React.useContext(ServiceContext);
    return services.petService;
}
```

### Asynchronous Programming (NON-NEGOTIABLE)

Web MUST use native async/await:

- MUST use native **async/await** (ES2017+)
- MUST NOT use callbacks, Promises.then(), or RxJS for new code

**Example**:
```typescript
export const usePets = () => {
    const [pets, setPets] = useState<Pet[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const loadPets = async () => {
        setIsLoading(true);
        try {
            const result = await petService.getPets();
            setPets(result);
        } finally {
            setIsLoading(false);
        }
    };

    return { pets, isLoading, loadPets };
};
```

**Prohibited Patterns**:
- ❌ Callbacks (except event handlers)
- ❌ Promise chains (`.then()`)
- ❌ RxJS for new code

### Test Identifiers (NON-NEGOTIABLE)

All interactive UI elements MUST have stable test identifiers:

- Use `data-testid` attribute on all interactive elements
- Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)

**Example**:
```tsx
<button
    onClick={handleAdd}
    data-testid="petList.addButton.click"
>
    Add Pet
</button>

<ul data-testid="petList.list">
    {pets.map(pet => (
        <li key={pet.id} data-testid={`petList.item.${pet.id}`}>
            <PetItem pet={pet} />
        </li>
    ))}
</ul>
```

**Requirements**:
- MUST be unique within a screen/page
- MUST be stable (not change between test runs)
- MUST NOT use dynamic values EXCEPT for list item IDs
- Lists/collections MUST use stable IDs (database ID, not array index)

### Public API Documentation

**Documentation Policy (MANDATORY)**:
- WebApp code MUST NOT be documented or commented unless it is really unclear and hard to understand
- Code MUST be self-documenting through clear, descriptive naming
- Documentation/comments MUST only be added when code is genuinely difficult to understand
- Prefer refactoring unclear code to adding documentation/comments
- Inline comments explaining "what" code does are PROHIBITED

**When to Document**:
```typescript
// ✅ DOCUMENT - Hook manages complex state
/**
 * Manages pet list state with automatic refresh and error recovery.
 */
export function usePets() {
    // implementation
}

// ✅ NO DOCUMENTATION - Function name clearly explains behavior
export async function fetchPets(): Promise<Pet[]> {
    // implementation
}

// ✅ NO DOCUMENTATION - Constants are self-explanatory
export const MAX_RETRIES = 3;
export const DEFAULT_TIMEOUT = 5000;
export const API_BASE_URL = 'https://api.petspot.com';
```

### Given-When-Then Test Convention (NON-NEGOTIABLE)

All unit tests MUST follow Given-When-Then structure with section comments:

```typescript
describe('usePets', () => {
    it('should load pets successfully when service returns data', async () => {
        // given
        const mockPets = [
            { id: '1', name: 'Max', species: 'dog' },
            { id: '2', name: 'Luna', species: 'cat' }
        ];
        vi.mocked(petService.getPets).mockResolvedValue(mockPets);

        const { result } = renderHook(() => usePets());

        // when
        await act(async () => {
            await result.current.loadPets();
        });

        // then
        expect(result.current.pets).toHaveLength(mockPets.length);
        expect(result.current.pets[0].name).toBe(mockPets[0].name);
        expect(result.current.isLoading).toBe(false);
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
```

## Web Architecture & Quality Standards (NON-NEGOTIABLE)

### Code Quality Requirements

- MUST follow Clean Code principles:
  - Functions should be small, focused, and do one thing (single responsibility)
  - Descriptive naming (no abbreviations except well-known ones like `id`, `api`, `http`)
  - Avoid deep nesting (max 3 levels)
  - Prefer composition over inheritance
  - DRY (Don't Repeat Yourself) - extract reusable logic
  - Self-documenting code (clear naming, no unnecessary documentation/comments)
- MUST reuse existing code, logic, and styles whenever possible
  - Check for existing utilities, helpers, hooks, and patterns before creating new ones
  - Extract reusable logic to `/src/lib/` or `/src/hooks/` for shared use
  - Reuse existing styles, components, and patterns from the codebase
  - Avoid duplicating functionality that already exists
- MUST keep code simple
  - Prefer simple, straightforward solutions over complex ones
  - Avoid over-engineering and premature optimization
- MUST NOT produce any summary files during the implementation phase
- MUST minimize dependencies in `package.json`
  - Only add dependencies that provide significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")

### Filename Conventions

- **Non-component files**: `use-pets.ts`, `pet-service.ts`, `form-validation.ts` (kebab-case)
- **React components**: `PetList.tsx`, `AnnouncementCard.tsx`, `ContactForm.tsx` (PascalCase)
- **Test files**: `use-pets.test.tsx`, `pet-service.test.ts`, `PetList.test.tsx`
- **Exception**: Configuration files may use other conventions if required by tooling

### Layout and CSS Requirements

- MUST plan application layout to maximize code reuse
  - Design layouts to reuse as much code as possible across screens and components
  - Extract common layout patterns into reusable components
  - Share layout components and structures across features
- MUST NOT duplicate CSS styles in different files/components
  - Extract shared styles to common CSS modules or global stylesheets
  - Reuse CSS classes and styles across components
  - Avoid copying the same CSS rules to multiple component files
  - Prefer shared style utilities and CSS modules over component-specific duplicated styles

### Business Logic Extraction (MANDATORY)

All business logic MUST be extracted to separate, testable functions:

- **`/src/hooks/`**: Custom React hooks for state management and business logic
  - Hooks encapsulate complex stateful logic
  - Hooks are pure functions (deterministic outputs for given inputs)
  - Hooks MUST be covered by unit tests in `/src/hooks/__test__/`
- **`/src/lib/`**: Pure utility functions and business logic helpers
  - Framework-agnostic functions (no React dependencies)
  - Reusable across components and hooks
  - Pure functions (no side effects, testable in isolation)
  - MUST be covered by unit tests in `/src/lib/__test__/`
- **`/src/components/`**: React components (presentation layer ONLY)
  - Components SHOULD be thin and delegate to hooks/lib for logic
  - Components focus on rendering and user interaction handling
  - Complex logic extracted to hooks or lib functions

### TDD Workflow (MANDATORY)

Web development MUST follow TDD (Red-Green-Refactor):

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
  4. **Optimize the code and the tests**: Verify if the code or tests may be improved/simplified
  5. **Format the code**: Always format the code using `npm run format` at the end of each task
- MUST NOT create any files with summaries during task implementation

## Module Structure

**`/webApp/src/`** (Web - Full Stack):

```
/webApp/src/
├── models/          - TypeScript domain models (interfaces/types)
├── services/        - HTTP services consuming backend REST API
├── hooks/           - Custom React hooks (state management, business logic)
│   └── __test__/    - Unit tests for hooks (MUST achieve 80% coverage)
├── lib/             - Pure utility functions and business logic helpers
│   └── __test__/    - Unit tests for lib functions (MUST achieve 80% coverage)
├── components/      - React components (presentation layer)
│   └── __tests__/   - Component tests (recommended)
├── contexts/        - React Context providers (DI, global state)
├── routes/          - Route definitions
├── config/          - Configuration files
└── types/           - TypeScript type definitions
```

## Testing Standards

### Unit Tests (MANDATORY)

- **Location**: `/src/hooks/__test__/`, `/src/lib/__test__/`
- **Framework**: Vitest + React Testing Library
- **Scope**: All business logic in hooks and lib functions
- **Coverage target**: 80% line + branch coverage
- MUST minimize number of test cases - cover all edge cases and happy paths, but don't duplicate similar cases
- MUST use parameterized tests when possible and worthwhile
- MUST try to reuse variables from `// given` phase in `// then` phase

### Component Tests (RECOMMENDED)

- **Location**: `/src/components/.../__tests__/`
- **Framework**: Vitest + React Testing Library
- **Scope**: Component rendering and user interactions
- Focus on behavior, not implementation details

### E2E Tests (MANDATORY)

- **Framework**: Selenium WebDriver (Java + Cucumber)
- **Test Scenarios**: `/e2e-tests/src/test/resources/features/web/*.feature` (Gherkin with `@web` tag)
- **Page Object Model**: `/e2e-tests/src/test/java/.../pages/` (XPath locators)
  - Uses `@FindBy(xpath = "...")` annotations
- **Step Definitions**: `/e2e-tests/src/test/java/.../steps-web/`
- **Run command**: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
- **Report**: `/e2e-tests/target/cucumber-reports/web/index.html`

## Compliance Checklist

All Web pull requests MUST:

- [ ] Run tests: `npm test --coverage` (from webApp/)
- [ ] Verify 80%+ test coverage in `webApp/coverage/index.html`
- [ ] Run linter: `npm run lint` (from webApp/)
- [ ] Format code: `npm run format` (from webApp/)
- [ ] Verify new interactive UI elements have `data-testid` attribute
- [ ] Verify business logic is extracted to `/src/hooks/` or `/src/lib/` (not in components)
- [ ] Verify all hooks and lib functions are covered by unit tests
- [ ] Verify TDD workflow was followed (tests written before implementation)
- [ ] Verify Clean Code principles applied:
  - [ ] Small functions, max 3 nesting levels
  - [ ] DRY - no duplicate code or CSS
  - [ ] Self-documenting code (no unnecessary comments)
- [ ] Verify dependencies minimized in `package.json`
- [ ] Verify all new tests follow Given-When-Then structure with `// given`, `// when`, `// then` comments
- [ ] Verify filename conventions (kebab-case for files, PascalCase for React components)

---

**Version**: 1.0.0 | **Based on Constitution**: v2.5.10

