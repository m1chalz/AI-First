# Research: Koin Dependency Injection for KMP

**Feature**: Koin DI for Kotlin Multiplatform  
**Date**: 2025-11-17  
**Phase**: 0 - Research & Best Practices

## Overview

This document consolidates research findings for implementing Koin dependency injection across all KMP target platforms (Android, iOS, Web). It resolves all "NEEDS CLARIFICATION" items from Technical Context and provides best practices for each technology choice.

## Technology Decisions

### 1. Koin Framework Choice

**Decision**: Use Koin for dependency injection across all platforms

**Rationale**:
- Native Kotlin Multiplatform support (commonMain, androidMain, iosMain, jsMain)
- Lightweight and performant (no reflection, no code generation in KMP context)
- Simple DSL (`module { }`, `single { }`, `factory { }`)
- Active community and well-maintained (Insert Koin organization)
- Seamless integration with Android (Jetpack Compose ViewModel injection)
- Works with Swift via Kotlin/Native (iOS)
- Works with TypeScript via Kotlin/JS (Web)

**Alternatives Considered**:
- **Kodein-DI**: Similar to Koin but less popular in KMP ecosystem (smaller community)
- **Manual DI**: Too verbose and error-prone for large applications
- **Platform-specific DI** (Hilt for Android, native Swift DI for iOS, TS DI for Web): Would duplicate dependency definitions across platforms, violating DRY principle

**Sources**:
- Koin official documentation: https://insert-koin.io/docs/reference/koin-mp/kmp
- KMP DI comparison: https://kotlinlang.org/docs/multiplatform-mobile-dependencies.html

---

### 2. Koin Version & Dependencies

**Decision**: Use Koin 3.5+ for KMP

**Dependencies** (to be added to `libs.versions.toml`):
```toml
[versions]
koin = "3.5.3"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
koin-androidx-compose = { module = "io.insert-koin:koin-androidx-compose", version.ref = "koin" }
koin-test = { module = "io.insert-koin:koin-test", version.ref = "koin" }
```

**Rationale**:
- Koin 3.5+ has stable KMP support for all target platforms
- `koin-core`: Shared module (commonMain)
- `koin-android`: Android ViewModels and Compose integration
- `koin-androidx-compose`: Jetpack Compose specific extensions (`koinViewModel()`)
- `koin-test`: Unit testing utilities (`KoinTest`, `startKoin` in tests)

**Gradle Configuration** (`shared/build.gradle.kts`):
```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
            }
        }
        // iOS and JS use koin-core from commonMain (no platform-specific artifacts needed)
    }
}
```

---

### 3. Module Organization Strategy

**Decision**: Single centralized module per spec clarifications

**Module Structure**:
```kotlin
// /shared/src/commonMain/.../di/DomainModule.kt
val domainModule = module {
    // Future: Use cases
    // single { GetPetsUseCase(get()) }
    
    // Future: Domain services
    // single { PetValidator() }
}

// /composeApp/src/androidMain/.../di/DataModule.kt
val androidDataModule = module {
    // Future: Repository implementations
    // single<PetRepository> { PetRepositoryImpl(get(), get()) }
    
    // Future: Data sources
    // single { PetApi(get()) }
    // single { PetDatabase.getInstance(androidContext()) }
}

// /composeApp/src/androidMain/.../di/ViewModelModule.kt
val androidViewModelModule = module {
    // Future: ViewModels
    // viewModel { PetListViewModel(get()) }
}
```

**Rationale**:
- **Single conceptual module** with logical groupings (domain, data, viewmodel)
- Domain module in `shared` contains platform-agnostic dependencies
- Platform modules (`androidDataModule`, `androidViewModelModule`) provide platform-specific implementations
- Clear separation of concerns: domain logic vs data access vs presentation
- Easy to understand for new developers (one place to look for dependencies)

**Benefits**:
- Simpler mental model (no complex module composition)
- Easier debugging (one initialization point)
- Matches spec requirement: "Single module - wszystkie dependencies w jednym module"

---

### 4. Dependency Scopes

**Decision**: Use singleton and factory scopes only in MVP (per spec clarifications)

**Scope Definitions**:
- **`single { }`**: Singleton instance (created once, shared across app)
  - Use for: Repositories, use cases, API clients, databases
  - Example: `single<PetRepository> { PetRepositoryImpl(get()) }`

- **`factory { }`**: New instance created each time requested
  - Use for: Lightweight objects, validators, formatters
  - Example: `factory { PetValidator() }`

- **`viewModel { }`** (Android only): Scoped to ViewModel lifecycle
  - Use for: Android ViewModels (automatically cleared when ViewModel destroyed)
  - Example: `viewModel { PetListViewModel(get()) }`

**Out of Scope** (for MVP):
- `scope { }`: Custom scopes (e.g., screen-scoped, session-scoped)
- `scoped { }`: Scoped to a specific scope ID

**Rationale**:
- Singleton and factory cover 90% of use cases
- Advanced scopes add complexity without immediate value
- Can be added later if needed (e.g., for screen-scoped state)

**Best Practices**:
- Default to `single { }` for stateless services
- Use `factory { }` only when state isolation required
- Use `viewModel { }` for all Android ViewModels (handles lifecycle automatically)

---

### 5. Platform Initialization Patterns

#### Android Initialization

**Location**: `Application.onCreate()`

**Implementation**:
```kotlin
// /composeApp/src/androidMain/.../PetSpotApplication.kt
class PetSpotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger() // Koin logging (remove in production)
            androidContext(this@PetSpotApplication)
            modules(
                domainModule,           // Shared module
                androidDataModule,      // Android data layer
                androidViewModelModule  // Android ViewModels
            )
        }
    }
}
```

**AndroidManifest.xml**:
```xml
<application
    android:name=".PetSpotApplication"
    ...>
</application>
```

**Benefits**:
- Koin initialized before any Activity/Fragment creation
- `androidContext()` provides Android Context to modules
- Clear error messages if DI fails (app crashes on startup, not during user interaction)

---

#### iOS Initialization

**Location**: `@main` app entry point

**Implementation**:
```swift
// /iosApp/iosApp/PetSpotApp.swift
import shared

@main
struct PetSpotApp: App {
    init() {
        KoinInitializer().initialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// /iosApp/iosApp/DI/KoinInitializer.swift
import shared

class KoinInitializer {
    func initialize() {
        // Start Koin from Kotlin/Native shared module
        KoinKt.doInitKoin()
    }
}

// /shared/src/iosMain/kotlin/.../di/KoinIos.kt
fun initKoin() {
    startKoin {
        modules(domainModule)
        // iOS-specific modules can be added here
    }
}
```

**Benefits**:
- Koin initialized before any SwiftUI views appear
- Shared Koin instance accessible from Swift
- Repositories and use cases from shared module available to iOS ViewModels

**iOS ViewModel Pattern** (using Koin from Swift):
```swift
@MainActor
class PetListViewModel: ObservableObject {
    private let getPetsUseCase: GetPetsUseCase
    
    init(getPetsUseCase: GetPetsUseCase = KoinKt.get()) {
        self.getPetsUseCase = getPetsUseCase
    }
    
    // ViewModel logic using injected use case
}
```

---

#### Web Initialization

**Location**: App bootstrap (`index.tsx`)

**Implementation**:
```typescript
// /webApp/src/di/koinSetup.ts
import { startKoin } from 'shared'; // Kotlin/JS export

export function initializeKoin() {
    startKoin({
        modules: [domainModule] // Shared module from Kotlin/JS
    });
}

// /webApp/src/index.tsx
import { initializeKoin } from './di/koinSetup';

// Initialize DI before rendering app
initializeKoin();

const root = ReactDOM.createRoot(document.getElementById('root')!);
root.render(<App />);
```

**Benefits**:
- Koin initialized before React app renders
- Shared dependencies available to React components via Kotlin/JS

**Web Component Pattern** (using Koin from TypeScript):
```typescript
import { get } from 'shared'; // Kotlin/JS Koin helper

export function usePets() {
    const getPetsUseCase = get<GetPetsUseCase>(); // Inject from Koin
    
    const [pets, setPets] = useState<Pet[]>([]);
    
    useEffect(() => {
        getPetsUseCase.invoke().then(setPets);
    }, []);
    
    return { pets };
}
```

---

### 6. Testing Strategy

**Decision**: No dedicated DI infrastructure tests (per spec clarifications). DI correctness validated indirectly through component tests.

**Test Approaches**:

#### Unit Tests with Koin Test

```kotlin
// /shared/src/commonTest/.../GetPetsUseCaseTest.kt
class GetPetsUseCaseTest : KoinTest {
    private val useCase: GetPetsUseCase by inject()
    
    @Before
    fun setup() {
        startKoin {
            modules(module {
                single<PetRepository> { FakePetRepository() }
                single { GetPetsUseCase(get()) }
            })
        }
    }
    
    @After
    fun tearDown() {
        stopKoin()
    }
    
    @Test
    fun `should return pets when repository succeeds`() = runTest {
        // Given - repository configured in setup
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.isSuccess)
    }
}
```

**Benefits**:
- Tests verify that dependencies can be resolved
- Tests verify component behavior with injected dependencies
- No need for dedicated "can Koin start?" tests

---

#### E2E Smoke Tests

**Purpose**: Verify DI initialization doesn't crash app startup

```typescript
// /e2e-tests/web/specs/koin-initialization.spec.ts
test('should initialize app without DI errors', async ({ page }) => {
    // Given - clean app state
    
    // When - navigate to app
    await page.goto('/');
    
    // Then - app loads successfully (no DI errors in console)
    await expect(page.locator('body')).toBeVisible();
    // No console errors expected
});
```

```typescript
// /e2e-tests/mobile/specs/koin-initialization.spec.ts
it('should launch Android app without DI errors', async () => {
    // Given - app installed
    
    // When - app launches
    const appElement = await driver.$('~app.root');
    
    // Then - app renders successfully (no crash)
    await expect(appElement).toBeDisplayed();
});
```

---

### 7. Error Handling & Debugging

**Best Practices**:

1. **Fail Fast**: DI errors should crash app at startup, not during user interaction
   - Use `startKoin` in Application/App entry point
   - Don't catch exceptions from `startKoin` (let app crash with clear error)

2. **Clear Error Messages**: Koin provides descriptive errors
   - Missing dependency: `No definition found for type 'PetRepository'`
   - Circular dependency: `Circular dependency detected: A -> B -> A`

3. **Logging** (Development only):
   ```kotlin
   startKoin {
       androidLogger(Level.ERROR) // Android: Log errors only
       printLogger(Level.ERROR)   // iOS/Web: Log errors only
       modules(...)
   }
   ```

4. **Definition Validation**: Use `checkModules()` in tests to validate module definitions
   ```kotlin
   @Test
   fun `verify Koin module definitions`() {
       koinApplication {
           modules(domainModule, androidDataModule)
           checkModules()
       }
   }
   ```

---

### 8. Documentation Strategy

**Decision**: Architecture Decision Record (ADR) format per spec clarifications

**ADR Location**: `/docs/adr/001-koin-dependency-injection.md`

**ADR Contents**:
1. Context: Why we need DI in KMP project
2. Decision: Koin chosen for multiplatform DI
3. Consequences: Benefits and trade-offs
4. Implementation: Module organization and initialization points
5. Examples: Code snippets for adding new dependencies

**Additional Documentation**:
- **Quickstart Guide** (`quickstart.md`): 15-minute guide for new developers to add their first dependency
- **KDoc**: All Koin modules documented with `/** ... */` comments explaining purpose and usage

---

## Migration Path

**Phase 1**: Infrastructure Setup (This Feature)
- Add Koin dependencies to `build.gradle.kts` and `libs.versions.toml`
- Create empty domain module in `shared/src/commonMain/.../di/DomainModule.kt`
- Initialize Koin on all platforms (Android, iOS, Web)
- Create E2E smoke tests verifying initialization
- Document in ADR and quickstart guide

**Phase 2**: First Dependencies (Future Features)
- Add repository interfaces to shared module
- Implement repositories in platform modules
- Register in Koin modules (`single<PetRepository> { ... }`)
- Add use cases to domain module
- Test with real dependencies

**Phase 3**: ViewModel Integration (Future Features)
- Add Android ViewModels to `androidViewModelModule`
- Use `koinViewModel()` in Compose screens
- Add iOS ViewModels consuming Koin from Swift
- Add Web hooks consuming Koin from TypeScript

---

## Risk Mitigation

### Risk 1: Learning Curve
**Mitigation**: 
- Comprehensive quickstart guide (15-minute goal)
- Code examples in ADR
- Pair programming sessions for first dependency additions

### Risk 2: Initialization Timing Issues
**Mitigation**:
- Initialize Koin in Application/App entry point (before any component creation)
- E2E smoke tests catch initialization failures early

### Risk 3: Circular Dependencies
**Mitigation**:
- Use `checkModules()` in tests to detect circular dependencies
- Follow repository pattern (interfaces in shared, implementations in platforms)

### Risk 4: Platform-Specific Initialization Differences
**Mitigation**:
- Document platform-specific initialization in ADR
- Provide code templates for each platform in quickstart guide

---

## Success Metrics

From spec.md Success Criteria:
- ✅ SC-001: All three platforms initialize DI container at startup without errors
- ✅ SC-002: Developers can add shared dependency and use it across platforms
- ✅ SC-003: Build succeeds on all platforms with DI configured
- ✅ SC-004: Unit tests can replace production dependencies with test doubles
- ✅ SC-005: Adding new dependency requires only constructor + module definition
- ✅ SC-006: Dependency resolution errors caught at startup with clear messages
- ✅ SC-007: ADR + quickstart guide enable 15-minute onboarding

---

## References

- Koin KMP Documentation: https://insert-koin.io/docs/reference/koin-mp/kmp
- Koin Android Integration: https://insert-koin.io/docs/reference/koin-android/start
- Kotlin Multiplatform Best Practices: https://kotlinlang.org/docs/multiplatform-mobile-dependencies.html
- Repository Pattern in KMP: https://kotlinlang.org/docs/multiplatform-mobile-dependencies.html#repository-pattern

---

**Next Phase**: Phase 1 - Design & Contracts (generate data-model.md, contracts/, quickstart.md)

