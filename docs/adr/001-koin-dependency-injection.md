# ADR 001: Koin Dependency Injection for Kotlin Multiplatform

**Status**: Accepted  
**Date**: 2025-11-17  
**Decision Makers**: Development Team  
**Related**: Constitution Principle VIII

## Context

The PetSpot project uses Kotlin Multiplatform to share business logic across Android, iOS, and Web platforms. We need a dependency injection (DI) framework that:

1. Works across all three KMP target platforms (Android/JVM, iOS/Native, JS/Browser)
2. Enables testable code by allowing dependency replacement in tests
3. Simplifies dependency management without manual instantiation
4. Provides a declarative, easy-to-understand API
5. Has minimal performance overhead and small binary size impact

### Requirements

From the constitution (Principle VIII - Dependency Injection with Koin):
- **MUST** work on Android, iOS, and JavaScript targets
- **MUST** support constructor injection and property injection
- **MUST** enable test doubles for unit testing
- **MUST** integrate with platform-specific frameworks (Jetpack Compose ViewModels, SwiftUI, React)
- **SHOULD** have minimal boilerplate
- **SHOULD** provide clear error messages when dependencies are missing

## Decision

We will use **Koin 3.5.3** as our dependency injection framework across all platforms.

### Why Koin?

**Multiplatform Support**: Koin is one of the few DI frameworks with first-class Kotlin Multiplatform support, providing dedicated artifacts for all our target platforms:
- `koin-core` - Works on all KMP targets (commonMain)
- `koin-android` - Android-specific extensions
- `koin-androidx-compose` - Jetpack Compose integration
- Native support for Kotlin/Native (iOS) and Kotlin/JS (Web)

**Simplicity**: Koin uses a lightweight DSL without code generation or reflection (on Native), making it:
- Easy to learn and understand (15-minute onboarding per success criteria)
- Fast compilation (no annotation processing)
- Small binary size impact
- Transparent runtime behavior

**Testing Support**: Koin provides `koin-test` module enabling easy dependency replacement in tests without mocking frameworks.

**Active Community**: Koin is maintained by InsertKoin with regular updates, comprehensive documentation, and strong community support.

### Alternatives Considered

| Framework | Pros | Cons | Verdict |
|-----------|------|------|---------|
| **Kodein-DI** | KMP support, type-safe | More complex API, smaller community | ❌ Rejected - Steeper learning curve |
| **Kotlin-inject** | Compile-time safety, generated code | No JS support yet, requires KSP | ❌ Rejected - Missing JS target |
| **Manual DI** | No dependencies, full control | High maintenance, error-prone, no testing support | ❌ Rejected - Violates constitution |
| **Platform-specific** (Hilt, Swinject) | Best platform integration | Separate DI for each platform, duplication | ❌ Rejected - Duplicates DI logic |

## Implementation

### Module Structure

**Single Centralized Strategy**: All dependencies organized in one module per layer:

```kotlin
// Shared domain dependencies (commonMain)
val domainModule = module {
    single { GetPetsUseCase(get()) }
    single { SavePetUseCase(get()) }
}

// Android data layer (androidMain)
val dataModule = module {
    single<PetRepository> { PetRepositoryImpl(get()) }
}

// Android ViewModels (androidMain)
val viewModelModule = module {
    viewModel { PetListViewModel(get()) }
}
```

### Platform Initialization

**Android** (`PetSpotApplication.kt`):
```kotlin
class PetSpotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PetSpotApplication)
            modules(domainModule, dataModule, viewModelModule)
        }
    }
}
```

**iOS** (`KoinInitializer.swift`):
```swift
class KoinInitializer {
    func initialize() {
        KoinIosKt.initKoin()
    }
}
```

**Web** (`koinSetup.ts`):
```typescript
export function initializeKoin(): void {
    startKoinJs([domainModuleJs]);
}
```

### Usage Examples

**Constructor Injection** (Kotlin):
```kotlin
class GetPetsUseCase(
    private val repository: PetRepository
) {
    suspend operator fun invoke(): Result<List<Pet>> =
        repository.getPets()
}
```

**ViewModel Injection** (Android Compose):
```kotlin
@Composable
fun PetListScreen(viewModel: PetListViewModel = koinViewModel()) {
    // ViewModel automatically injected with dependencies
}
```

**Testing with Fakes**:
```kotlin
class GetPetsUseCaseTest : KoinTest {
    @Before
    fun setup() {
        startKoin {
            modules(module {
                single<PetRepository> { FakePetRepository() }
            })
        }
    }
}
```

## Consequences

### Positive

✅ **Consistent DI** across all platforms reduces cognitive load  
✅ **Easy onboarding** - developers learn one DI framework for all targets  
✅ **Testability** - simple test module replacement without mocking frameworks  
✅ **Documentation** - extensive Koin documentation and community resources  
✅ **Performance** - negligible overhead, no reflection on Native  
✅ **Compose integration** - `koinViewModel()` works seamlessly  
✅ **Future-proof** - Koin actively maintained with KMP as priority

### Negative

⚠️ **Runtime DI** - Dependency resolution errors caught at runtime, not compile-time (mitigated by initialization smoke tests)  
⚠️ **Learning curve** - Team must learn Koin DSL (acceptable: 15-minute quickstart)  
⚠️ **Single point of failure** - If Koin development stalls, migration would be costly (low risk: active community)

### Trade-offs

- **Chose runtime safety over compile-time safety**: Koin's runtime resolution is simpler and works across all platforms, while compile-time DI (kotlin-inject) lacks JS support
- **Chose single framework over platform-specific**: Unified DI reduces complexity despite platform-specific DI having better native integration
- **Chose lightweight DSL over annotation processing**: Faster builds and smaller binaries at cost of less IDE assistance

## Compliance

This implementation satisfies:
- ✅ Constitution Principle VIII (Koin DI)
- ✅ Constitution Principle VII (Interface-Based Design) - enables repository pattern
- ✅ Constitution Principle III (80% Test Coverage) - enables test doubles
- ✅ Success Criteria SC-002: Dependencies defined once, used everywhere
- ✅ Success Criteria SC-005: No manual wiring code required
- ✅ Success Criteria SC-007: 15-minute onboarding with quickstart guide

## References

- [Koin Official Documentation](https://insert-koin.io/)
- [Koin Multiplatform Guide](https://insert-koin.io/docs/reference/koin-mp/kmp)
- [Constitution Principle VIII](/specs/001-koin-kmp/spec.md)
- [Quickstart Guide](/specs/001-koin-kmp/quickstart.md)
- [Feature Specification](/specs/001-koin-kmp/spec.md)

## Revision History

| Date | Version | Changes | Author |
|------|---------|---------|--------|
| 2025-11-17 | 1.0 | Initial ADR | Development Team |

