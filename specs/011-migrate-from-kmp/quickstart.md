# Quickstart: KMP to Platform-Independent Migration

**Feature**: Complete KMP to Platform-Independent Migration  
**Branch**: `011-migrate-from-kmp`  
**Date**: 2025-11-24

## Prerequisites

Before starting the migration, ensure the following are in place:

### Development Environment

**Required Tools**:
- [ ] Git (version control)
- [ ] Gradle 8.x (Android builds)
- [ ] JDK 17 (Android compilation)
- [ ] Android Studio (recommended for Android development)
- [ ] Xcode 15+ (iOS builds and tests)
- [ ] Swift 5.9+ (bundled with Xcode)

**Verification Commands**:
```bash
# Verify Gradle
./gradlew --version
# Expected: Gradle 8.x

# Verify JDK
java -version
# Expected: openjdk version "17.x.x"

# Verify Xcode
xcodebuild -version
# Expected: Xcode 15.x

# Verify Swift
swift --version
# Expected: swift-driver version 5.9.x
```

### Repository State

**Branch Setup**:
```bash
# Ensure you're on the migration branch
git checkout 011-migrate-from-kmp

# Ensure branch is up to date
git pull origin 011-migrate-from-kmp

# Verify clean working directory
git status
# Expected: "nothing to commit, working tree clean"
```

### Pre-Migration Baseline

**Capture baseline metrics** (used for post-migration validation):

```bash
# Android build time baseline
time ./gradlew :composeApp:assembleDebug
# Document: Build time = X minutes Y seconds

# Android test coverage baseline
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# Open: composeApp/build/reports/kover/html/index.html
# Document: Coverage = X% lines, Y% branches

# iOS build time baseline (from command line)
time xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
# Document: Build time = X minutes Y seconds

# iOS test coverage baseline
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
# Open Xcode → Product → Test → Show Code Coverage
# Document: Coverage = X%

# Verify E2E tests pass (baseline)
npx playwright test
# Document: All tests passing
```

**Document Baseline**:
Create a file `specs/011-migrate-from-kmp/baseline-metrics.txt`:
```text
Android Build Time: X min Y sec
Android Test Coverage: X%
iOS Build Time: X min Y sec
iOS Test Coverage: X%
E2E Tests: All passing
Date: 2025-11-24
```

---

## Quick Start: 5-Minute Setup

### 1. Validate Current State (1 min)

```bash
# Verify shared module exists
ls shared/src/commonMain/
# Expected: domain/ directory visible

# Verify Android imports from shared
grep -r "import com.intive.aifirst.petspot.domain" composeApp/src/androidMain/ | head -n 5
# Expected: Some imports found

# Verify iOS imports Shared framework
grep -r "import Shared" iosApp/ | head -n 5
# Expected: Some imports found
```

### 2. Understand Migration Phases (1 min)

**Read**: `/specs/011-migrate-from-kmp/spec.md` - Section "Migration Strategy"

**Key Phases**:
1. **Phase 1-2**: iOS migration (Kotlin → Swift translation)
2. **Phase 3-4**: Android migration (Kotlin copy)
3. **Phase 5-6**: Remove build configuration
4. **Phase 7**: Delete shared module
5. **Phase 8-9**: CI/CD + documentation

### 3. Review Translation Patterns (2 min)

**Read**: `/specs/011-migrate-from-kmp/contracts/kotlin-swift-mapping.md`

**Quick Reference**:
- Kotlin `data class` → Swift `struct`
- Kotlin `interface` → Swift `protocol`
- Kotlin `suspend fun` → Swift `func ... async throws`
- Enum cases: `SCREAMING_SNAKE_CASE` → `camelCase`

### 4. Verify Test Suites (1 min)

```bash
# Android tests
./gradlew :composeApp:testDebugUnitTest
# Expected: All tests passing

# iOS tests
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
# Expected: All tests passing
```

---

## Phase-by-Phase Execution

### Phase 1: Migrate iOS Domain Models (Kotlin → Swift)

**Estimated Time**: 2-3 hours

**Steps**:

1. **Create Domain Models directory**:
   ```bash
   mkdir -p iosApp/iosApp/Domain/Models
   ```

2. **Translate Kotlin models to Swift structs**:

   **Animal.swift**:
   ```swift
   // File: iosApp/iosApp/Domain/Models/Animal.swift
   import Foundation
   
   struct Animal {
       let id: String
       let name: String
       let species: AnimalSpecies
       let gender: AnimalGender
       let status: AnimalStatus
       let location: Location?
       let imageUrl: String?
   }
   ```

   **Location.swift**:
   ```swift
   // File: iosApp/iosApp/Domain/Models/Location.swift
   import Foundation
   
   struct Location {
       let address: String
       let city: String
       let country: String
       let latitude: Double?
       let longitude: Double?
   }
   ```

   **AnimalSpecies.swift**:
   ```swift
   // File: iosApp/iosApp/Domain/Models/AnimalSpecies.swift
   import Foundation
   
   enum AnimalSpecies {
       case dog
       case cat
       case bird
       case rabbit
       case other
   }
   ```

   **AnimalGender.swift**:
   ```swift
   // File: iosApp/iosApp/Domain/Models/AnimalGender.swift
   import Foundation
   
   enum AnimalGender {
       case male
       case female
       case unknown
   }
   ```

   **AnimalStatus.swift**:
   ```swift
   // File: iosApp/iosApp/Domain/Models/AnimalStatus.swift
   import Foundation
   
   enum AnimalStatus {
       case available
       case pending
       case adopted
   }
   ```

3. **Update iOS imports** (remove `import Shared`):
   ```bash
   # Find files importing Shared
   grep -rl "import Shared" iosApp/iosApp/
   
   # For each file, remove the line:
   # import Shared
   
   # Replace Shared.Animal with Animal (local type)
   ```

4. **Update enum case references** (if switch statements exist):
   ```swift
   // BEFORE
   switch animal.species {
   case .DOG: // ...
   case .CAT: // ...
   }
   
   // AFTER
   switch animal.species {
   case .dog: // ...
   case .cat: // ...
   }
   ```

5. **Verify iOS builds**:
   ```bash
   xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
   # Expected: Build succeeded
   ```

6. **Verify iOS tests pass**:
   ```bash
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
   # Expected: All tests passing, 80%+ coverage
   ```

7. **Commit Phase 1**:
   ```bash
   git add iosApp/iosApp/Domain/Models/
   git add iosApp/iosApp/Features/ # (updated imports)
   git commit -m "[011-migrate-from-kmp] Phase 1: Migrate iOS domain models

   Translated Kotlin domain models to Swift structs and enums:
   - Animal, Location (structs with value semantics)
   - AnimalSpecies, AnimalGender, AnimalStatus (enums with camelCase)
   
   Changes:
   - Created Domain/Models/ directory with 5 Swift files
   - Removed 'import Shared' from ViewModels and repositories
   - Updated enum case references from SCREAMING_SNAKE_CASE to camelCase
   - Verified iOS build succeeds and tests pass (80%+ coverage maintained)
   
   Decision logic:
   - Kotlin data class → Swift struct (value semantics, immutability)
   - Kotlin enum SCREAMING_SNAKE_CASE → Swift enum camelCase (platform convention)
   - Optional types preserved with same syntax (Type?)
   "
   git tag migration-phase-1-ios-models
   ```

---

### Phase 2: Migrate iOS Repositories and Use Cases

**Estimated Time**: 2-3 hours

**Steps**:

1. **Create directories**:
   ```bash
   mkdir -p iosApp/iosApp/Domain/Repositories
   mkdir -p iosApp/iosApp/Domain/UseCases
   mkdir -p iosApp/iosAppTests/Fakes
   ```

2. **Translate repository protocol**:

   **AnimalRepository.swift**:
   ```swift
   // File: iosApp/iosApp/Domain/Repositories/AnimalRepository.swift
   import Foundation
   
   protocol AnimalRepository {
       func getAnimals() async throws -> [Animal]
   }
   ```

3. **Translate use case**:

   **GetAnimalsUseCase.swift**:
   ```swift
   // File: iosApp/iosApp/Domain/UseCases/GetAnimalsUseCase.swift
   import Foundation
   
   class GetAnimalsUseCase {
       private let repository: AnimalRepository
       
       init(repository: AnimalRepository) {
           self.repository = repository
       }
       
       func execute() async throws -> [Animal] {
           return try await repository.getAnimals()
       }
   }
   ```

4. **Translate test fake**:

   **FakeAnimalRepository.swift**:
   ```swift
   // File: iosApp/iosAppTests/Fakes/FakeAnimalRepository.swift
   import Foundation
   @testable import iosApp
   
   class FakeAnimalRepository: AnimalRepository {
       private let animals: [Animal]
       
       init(animals: [Animal] = []) {
           self.animals = animals
       }
       
       func getAnimals() async throws -> [Animal] {
           return animals
       }
   }
   ```

5. **Update ServiceContainer**:

   ```swift
   // File: iosApp/iosApp/DI/ServiceContainer.swift
   class ServiceContainer {
       static let shared = ServiceContainer()
       
       lazy var animalRepository: AnimalRepository = AnimalRepositoryImpl(
           // Use local mock data or hardcoded array
       )
       
       lazy var getAnimalsUseCase: GetAnimalsUseCase = GetAnimalsUseCase(
           repository: animalRepository
       )
   }
   ```

6. **Update ViewModel imports**:
   - Remove `import Shared`
   - Update type references to local types

7. **Verify iOS builds and tests**:
   ```bash
   xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
   ```

8. **Commit Phase 2**:
   ```bash
   git add iosApp/iosApp/Domain/Repositories/
   git add iosApp/iosApp/Domain/UseCases/
   git add iosApp/iosAppTests/Fakes/
   git add iosApp/iosApp/DI/
   git commit -m "[011-migrate-from-kmp] Phase 2: Migrate iOS repositories and use cases

   Translated Kotlin repository interfaces and use cases to Swift protocols and classes.
   
   Changes:
   - Created AnimalRepository protocol with async throws method
   - Created GetAnimalsUseCase class (transitional - iOS should remove in future)
   - Created FakeAnimalRepository test double
   - Updated ServiceContainer with local domain dependencies
   - Removed all 'import Shared' statements from iOS codebase
   - Verified iOS builds and tests pass (80%+ coverage maintained)
   
   Decision logic:
   - Kotlin interface → Swift protocol (behavior contract)
   - Kotlin suspend fun → Swift async throws (idiomatic async pattern)
   - Use case migrated as transitional code; iOS SHOULD refactor to call repositories directly in future per MVVM-C guidelines
   "
   git tag migration-phase-2-ios-repos
   ```

---

### Phase 3: Migrate Android Domain Models (Kotlin Copy)

**Estimated Time**: 1-2 hours

**Steps**:

1. **Create domain models directory**:
   ```bash
   mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models
   ```

2. **Copy Kotlin models from shared to Android**:
   ```bash
   # Copy all model files
   cp shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/*.kt \
      composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/
   ```

3. **Update package names** in copied files:

   ```kotlin
   // BEFORE (in each file)
   package com.intive.aifirst.petspot.domain.models
   
   // AFTER
   package com.intive.aifirst.petspot.composeapp.domain.models
   ```

4. **Remove @JsExport annotations** (if present):
   ```bash
   # Search and remove @JsExport
   grep -rl "@JsExport" composeApp/src/androidMain/kotlin/.../domain/models/
   # Edit files to remove annotation
   ```

5. **Update Android imports** (all files importing from shared):

   ```bash
   # Find files importing from shared domain
   grep -rl "import com.intive.aifirst.petspot.domain.models" composeApp/src/androidMain/
   
   # Replace with local imports:
   # BEFORE: import com.intive.aifirst.petspot.domain.models.Animal
   # AFTER:  import com.intive.aifirst.petspot.composeapp.domain.models.Animal
   ```

6. **Verify Android builds**:
   ```bash
   ./gradlew :composeApp:assembleDebug
   # Expected: Build succeeded
   ```

7. **Verify Android tests pass**:
   ```bash
   ./gradlew :composeApp:testDebugUnitTest koverHtmlReport
   # Expected: All tests passing, 80%+ coverage
   ```

8. **Commit Phase 3**:
   ```bash
   git add composeApp/src/androidMain/.../domain/models/
   git add composeApp/src/androidMain/.../data/ # (updated imports)
   git add composeApp/src/androidMain/.../presentation/ # (updated imports)
   git commit -m "[011-migrate-from-kmp] Phase 3: Migrate Android domain models

   Copied Kotlin domain models from shared module to Android platform.
   
   Changes:
   - Copied 5 Kotlin model files (Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus)
   - Updated package names to composeapp.domain.models
   - Removed @JsExport annotations (Android doesn't need them)
   - Updated all Android imports to reference local domain models
   - Verified Android build succeeds and tests pass (80%+ coverage maintained)
   
   Decision logic:
   - Straightforward Kotlin copy (no translation needed like iOS)
   - Package structure maintains consistency with existing composeApp structure
   "
   git tag migration-phase-3-android-models
   ```

---

### Phase 4: Migrate Android Repositories and Use Cases (Kotlin Copy)

**Estimated Time**: 1-2 hours

**Steps**:

1. **Create directories**:
   ```bash
   mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/repositories
   mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/usecases
   mkdir -p composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/composeapp/domain/fixtures
   mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/di
   ```

2. **Copy repository interface**:
   ```bash
   cp shared/src/commonMain/.../domain/repositories/AnimalRepository.kt \
      composeApp/src/androidMain/.../domain/repositories/
   ```

3. **Copy use case**:
   ```bash
   cp shared/src/commonMain/.../domain/usecases/GetAnimalsUseCase.kt \
      composeApp/src/androidMain/.../domain/usecases/
   ```

4. **Copy test fixtures**:
   ```bash
   cp shared/src/commonMain/.../domain/repositories/FakeAnimalRepository.kt \
      composeApp/src/androidUnitTest/.../domain/fixtures/
   
   cp shared/src/commonMain/.../domain/fixtures/MockAnimalData.kt \
      composeApp/src/androidUnitTest/.../domain/fixtures/
   ```

5. **Copy Koin DomainModule**:
   ```bash
   cp shared/src/commonMain/.../di/DomainModule.kt \
      composeApp/src/androidMain/.../di/
   ```

6. **Update package names** in all copied files:
   ```kotlin
   // Change package in each file
   package com.intive.aifirst.petspot.composeapp.domain.repositories
   package com.intive.aifirst.petspot.composeapp.domain.usecases
   package com.intive.aifirst.petspot.composeapp.domain.fixtures
   package com.intive.aifirst.petspot.composeapp.di
   ```

7. **Update imports** in all copied files:
   ```kotlin
   // BEFORE
   import com.intive.aifirst.petspot.domain.models.Animal
   
   // AFTER
   import com.intive.aifirst.petspot.composeapp.domain.models.Animal
   ```

8. **Register DomainModule in PetSpotApplication**:

   ```kotlin
   // File: composeApp/src/androidMain/.../PetSpotApplication.kt
   import com.intive.aifirst.petspot.composeapp.di.domainModule
   
   class PetSpotApplication : Application() {
       override fun onCreate() {
           super.onCreate()
           startKoin {
               androidContext(this@PetSpotApplication)
               modules(
                   dataModule,      // existing
                   domainModule,    // newly migrated
                   viewModelModule  // existing
               )
           }
       }
   }
   ```

9. **Update all Android imports** (ViewModels, repositories, tests):
   ```bash
   # Find and update imports
   grep -rl "import com.intive.aifirst.petspot.domain" composeApp/src/
   # Replace with composeapp.domain imports
   ```

10. **Verify Android builds and tests**:
    ```bash
    ./gradlew :composeApp:assembleDebug
    ./gradlew :composeApp:testDebugUnitTest koverHtmlReport
    ```

11. **Commit Phase 4**:
    ```bash
    git add composeApp/src/androidMain/.../domain/repositories/
    git add composeApp/src/androidMain/.../domain/usecases/
    git add composeApp/src/androidUnitTest/.../domain/fixtures/
    git add composeApp/src/androidMain/.../di/DomainModule.kt
    git add composeApp/src/androidMain/.../PetSpotApplication.kt
    git commit -m "[011-migrate-from-kmp] Phase 4: Migrate Android repositories and use cases

    Copied Kotlin repositories, use cases, and DI module from shared to Android platform.
    
    Changes:
    - Copied AnimalRepository interface and FakeAnimalRepository test double
    - Copied GetAnimalsUseCase business logic
    - Copied MockAnimalData test fixture
    - Migrated Koin DomainModule with domain layer bindings
    - Registered domainModule in PetSpotApplication
    - Updated all Android imports to reference local domain classes
    - Verified Android builds and tests pass (80%+ coverage maintained)
    
    Decision logic:
    - Koin DomainModule provides domain layer dependencies (use cases, interfaces if needed)
    - Test fixtures migrated to test directory for unit test reuse
    - All domain layer code now local to composeApp module
    "
    git tag migration-phase-4-android-repos
    ```

---

### Phase 5: Remove Gradle Configuration

**Estimated Time**: 30 minutes

**Steps**:

1. **Remove shared module from settings.gradle.kts**:

   ```kotlin
   // File: settings.gradle.kts
   // BEFORE
   rootProject.name = "AI-First"
   include(":composeApp")
   include(":shared")  // REMOVE THIS LINE
   
   // AFTER
   rootProject.name = "AI-First"
   include(":composeApp")
   ```

2. **Remove shared dependency from composeApp/build.gradle.kts**:

   ```kotlin
   // File: composeApp/build.gradle.kts
   dependencies {
       // BEFORE
       implementation(projects.shared)  // REMOVE THIS LINE
       
       // AFTER
       // projects.shared dependency removed
       implementation(libs.androidx.activity.compose)
       // ... other dependencies remain
   }
   ```

3. **Gradle sync**:
   ```bash
   ./gradlew clean
   ./gradlew --stop
   ./gradlew :composeApp:assembleDebug
   # Expected: Build succeeded without shared module
   ```

4. **Verify Android tests still pass**:
   ```bash
   ./gradlew :composeApp:testDebugUnitTest koverHtmlReport
   ```

5. **Commit Phase 5**:
   ```bash
   git add settings.gradle.kts
   git add composeApp/build.gradle.kts
   git commit -m "[011-migrate-from-kmp] Phase 5: Remove Gradle shared module configuration

    Removed shared module from Gradle build configuration.
    
    Changes:
    - Removed 'include(\":shared\")' from settings.gradle.kts
    - Removed 'implementation(projects.shared)' from composeApp/build.gradle.kts
    - Verified Gradle sync succeeds without shared module
    - Verified Android builds and tests pass without shared dependency
    
    Decision logic:
    - Android now builds entirely from composeApp local domain code
    - No shared module compilation required for Android builds
    "
   git tag migration-phase-5-gradle-config
   ```

---

### Phase 6: Remove iOS Framework References

**Estimated Time**: 30 minutes

**Steps**:

1. **Open iosApp in Xcode**:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```

2. **Remove KMP framework from build phases**:
   - Select iosApp target
   - Build Phases tab
   - "Link Binary With Libraries" section
   - Remove `Shared.framework` (if present)

3. **Remove framework search paths**:
   - Build Settings tab
   - Search for "Framework Search Paths"
   - Remove paths referencing `shared/build` or KMP-generated frameworks

4. **Verify iOS builds without KMP framework**:
   ```bash
   xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' clean build
   # Expected: Build succeeded
   ```

5. **Verify iOS tests pass**:
   ```bash
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
   ```

6. **Commit Phase 6**:
   ```bash
   git add iosApp/iosApp.xcodeproj/
   git commit -m "[011-migrate-from-kmp] Phase 6: Remove iOS KMP framework references

    Removed KMP Shared framework from Xcode project configuration.
    
    Changes:
    - Removed Shared.framework from 'Link Binary With Libraries' build phase
    - Removed framework search paths referencing shared/build
    - Verified iOS builds successfully without KMP framework compilation
    - Verified iOS tests pass without shared module dependency
    
    Decision logic:
    - iOS now builds entirely from iosApp local Swift domain code
    - No KMP framework generation or linking required
    - Build time improvement: Eliminated KMP framework compilation step
    "
   git tag migration-phase-6-ios-xcode-config
   ```

---

### Phase 7: Delete Shared Module Directory

**Estimated Time**: 15 minutes

**⚠️ CRITICAL**: Only proceed if Phases 1-6 are complete and all tests pass.

**Steps**:

1. **Final verification before deletion**:
   ```bash
   # Android builds without shared
   ./gradlew :composeApp:assembleDebug
   
   # iOS builds without shared
   xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
   
   # No shared imports remain
   git grep "import Shared" iosApp/
   git grep "import com.intive.aifirst.petspot.domain" composeApp/src/androidMain/ | grep -v composeapp
   # Both should return no results
   ```

2. **Delete shared module directory**:
   ```bash
   rm -rf shared/
   ```

3. **Verify full builds**:
   ```bash
   # Android
   ./gradlew :composeApp:assembleDebug
   ./gradlew :composeApp:testDebugUnitTest koverHtmlReport
   
   # iOS
   xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
   
   # E2E tests
   npx playwright test
   ```

4. **Commit Phase 7**:
   ```bash
   git add -u  # Stage deletions
   git commit -m "[011-migrate-from-kmp] Phase 7: Delete shared module directory

    Deleted entire shared Kotlin Multiplatform module after successful content migration.
    
    Changes:
    - Removed /shared directory and all contents
    - Verified Android builds and tests pass (80%+ coverage maintained)
    - Verified iOS builds and tests pass (80%+ coverage maintained)
    - Verified E2E tests pass (no functional regressions)
    - Verified no shared module imports remain in codebase
    
    Decision logic:
    - Platform independence achieved: Each platform has independent domain implementations
    - Build improvements: Eliminated KMP module overhead and compilation
    - Constitutional requirement fulfilled: No shared compiled code between platforms
    
    Rollback: If issues discovered, revert this commit and restore shared/ from git history
    "
   git tag migration-phase-7-delete-shared
   ```

5. **Create archive branch** (safety net):
   ```bash
   # Create branch preserving shared module
   git checkout migration-phase-6-ios-xcode-config
   git branch archive/shared-module
   git checkout 011-migrate-from-kmp
   
   # Push archive branch
   git push origin archive/shared-module
   ```

---

### Phase 8: Update CI/CD Pipeline (if applicable)

**Estimated Time**: 1 hour

**Steps**:

1. **Review CI/CD configuration** (e.g., `.github/workflows/`, `.gitlab-ci.yml`, `Jenkinsfile`):
   - Identify shared module build steps
   - Identify KMP-specific jobs

2. **Remove shared module build steps**:
   - Remove jobs that build shared module
   - Remove KMP framework generation for iOS
   - Remove shared module test execution

3. **Update platform build jobs**:
   - Ensure Android job builds composeApp only
   - Ensure iOS job builds iosApp without KMP framework

4. **Test CI/CD pipeline**:
   - Trigger pipeline manually or push commit
   - Verify all jobs pass

5. **Commit Phase 8**:
   ```bash
   git add .github/workflows/  # (or your CI config location)
   git commit -m "[011-migrate-from-kmp] Phase 8: Update CI/CD pipeline for platform independence

    Removed shared module build steps from CI/CD pipeline.
    
    Changes:
    - Removed shared module build job
    - Removed KMP framework generation for iOS
    - Removed shared module test execution
    - Updated platform builds to use local implementations
    - Verified all CI/CD jobs pass
    
    Decision logic:
    - CI/CD now reflects platform-independent architecture
    - Build times improved by removing shared module overhead
    "
   git tag migration-phase-8-cicd-update
   ```

---

### Phase 9: Documentation and Communication

**Estimated Time**: 1-2 hours

**Steps**:

1. **Update root README.md**:
   - Remove shared module references
   - Update build instructions
   - Update architecture diagram (if present)

2. **Update platform README files** (if they exist):
   - `composeApp/README.md`
   - `iosApp/README.md`

3. **Create migration guide for developers**:
   - Document for developers with open feature branches
   - Provide rebase/merge instructions
   - Include import update patterns

4. **Update CONTRIBUTING.md** (if exists):
   - Reflect platform-independent workflow

5. **Document build time improvements**:
   - Compare pre-migration vs post-migration metrics
   - Document in `specs/011-migrate-from-kmp/performance-improvements.md`

6. **Commit Phase 9**:
   ```bash
   git add README.md
   git add composeApp/README.md iosApp/README.md
   git add docs/migration/
   git commit -m "[011-migrate-from-kmp] Phase 9: Update documentation for platform independence

    Updated project documentation to reflect platform-independent architecture.
    
    Changes:
    - Updated root README.md (removed shared module references)
    - Updated platform-specific README files (Android, iOS)
    - Created migration guide for developers with open branches
    - Updated CONTRIBUTING.md with platform-independent workflow
    - Documented build time improvements and performance metrics
    
    Decision logic:
    - Documentation now accurately reflects current architecture
    - Developers have clear guidance for contributing to platform-specific code
    - Migration guide assists team members with merging/rebasing open branches
    "
   git tag migration-phase-9-documentation
   ```

7. **Communicate completion to team**:
   - Send announcement: "KMP to platform-independent migration complete on branch 011-migrate-from-kmp"
   - Share migration guide URL
   - Provide contact for questions

---

## Post-Migration Validation

### Build Time Comparison

```bash
# Android build time (compare to baseline)
time ./gradlew clean :composeApp:assembleDebug

# iOS build time (compare to baseline)
time xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' clean build

# Expected improvements:
# - Android: Same or faster (no KMP overhead)
# - iOS: 10-20% faster (no KMP framework compilation)
```

### Test Coverage Verification

```bash
# Android coverage (compare to baseline)
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# Open: composeApp/build/reports/kover/html/index.html
# Expected: 80%+ coverage maintained

# iOS coverage (compare to baseline)
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
# Open: Xcode → Product → Test → Show Code Coverage
# Expected: 80%+ coverage maintained
```

### Codebase Search Verification

```bash
# No shared imports remain
git grep "import Shared" iosApp/
# Expected: No results

git grep "import com.intive.aifirst.petspot.domain" composeApp/src/androidMain/ | grep -v composeapp
# Expected: No results

git grep "projects.shared" .
# Expected: No results

git grep ":shared" settings.gradle.kts
# Expected: No results
```

### E2E Test Validation

```bash
# Web E2E tests
npx playwright test
# Expected: All tests passing

# Mobile E2E tests (if configured)
npm run test:mobile:android
npm run test:mobile:ios
# Expected: All tests passing
```

---

## Rollback Procedures

### Rollback During Migration (Phases 1-6, shared still exists)

```bash
# Revert to before migration started
git reset --hard <pre-migration-commit>

# Or revert specific phase
git revert <phase-commit-sha>

# Restore shared module if partially deleted
git checkout <pre-deletion-commit> -- shared/

# Verify platforms build with shared module
./gradlew :composeApp:assembleDebug
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
```

### Rollback After Shared Module Deletion (Phase 7+)

```bash
# Restore shared module from archive branch
git checkout archive/shared-module -- shared/

# Restore settings.gradle.kts
git checkout archive/shared-module -- settings.gradle.kts

# Restore composeApp/build.gradle.kts
git checkout archive/shared-module -- composeApp/build.gradle.kts

# Restore iOS Xcode config
git checkout archive/shared-module -- iosApp/iosApp.xcodeproj/

# Revert platform-specific domain code
git rm -r composeApp/src/androidMain/.../domain/
git rm -r iosApp/iosApp/Domain/

# Restore original imports
# (Manual step: restore imports in ViewModels, repositories, tests)

# Verify builds work with restored shared module
./gradlew :composeApp:assembleDebug
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
```

---

## Troubleshooting

### Issue: Android build fails with "Unresolved reference"

**Symptoms**: Kotlin compiler errors like "Unresolved reference: Animal"

**Solution**:
1. Verify domain models exist in `/composeApp/src/androidMain/.../domain/models/`
2. Check package names match in model files and import statements
3. Clean and rebuild:
   ```bash
   ./gradlew clean
   ./gradlew :composeApp:assembleDebug
   ```

### Issue: iOS build fails with "No such module 'Shared'"

**Symptoms**: Swift compiler error "No such module 'Shared'"

**Solution**:
1. Verify all `import Shared` statements removed from iOS code
2. Verify Swift domain models exist in `/iosApp/iosApp/Domain/Models/`
3. Clean Xcode build:
   ```bash
   xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' clean build
   ```
4. If persists, delete Derived Data:
   ```bash
   rm -rf ~/Library/Developer/Xcode/DerivedData
   ```

### Issue: Tests fail after migration

**Symptoms**: Unit tests that previously passed now fail

**Solution**:
1. Verify test imports updated to platform-local packages
2. Verify test fixtures (FakeAnimalRepository, MockAnimalData) migrated
3. Check enum case name updates (SCREAMING_SNAKE_CASE → camelCase in iOS)
4. Re-run tests with verbose output:
   ```bash
   # Android
   ./gradlew :composeApp:testDebugUnitTest --info
   
   # iOS
   xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -verbose
   ```

### Issue: Coverage dropped below 80%

**Symptoms**: Code coverage report shows < 80% after migration

**Solution**:
1. Compare coverage report to baseline (pre-migration)
2. Identify which files lost coverage
3. Verify tests for those files were migrated and updated
4. Add missing tests if needed
5. Re-run coverage report

---

## Success Criteria Checklist

- [ ] **iOS Migration Complete** (Phases 1-2)
  - [ ] All iOS domain models translated to Swift
  - [ ] All iOS repositories and use cases translated
  - [ ] No `import Shared` statements remain in iOS code
  - [ ] iOS builds successfully
  - [ ] iOS tests pass with 80%+ coverage

- [ ] **Android Migration Complete** (Phases 3-4)
  - [ ] All Android domain models copied to composeApp
  - [ ] All Android repositories and use cases copied
  - [ ] Koin DomainModule registered
  - [ ] No shared package imports remain in Android code
  - [ ] Android builds successfully
  - [ ] Android tests pass with 80%+ coverage

- [ ] **Build Configuration Cleanup** (Phases 5-6)
  - [ ] Shared module removed from settings.gradle.kts
  - [ ] Shared dependency removed from composeApp/build.gradle.kts
  - [ ] KMP framework removed from iOS Xcode project
  - [ ] Both platforms build without shared module

- [ ] **Shared Module Deleted** (Phase 7)
  - [ ] /shared directory completely removed
  - [ ] Archive branch created for safety
  - [ ] All platform tests pass
  - [ ] E2E tests pass

- [ ] **CI/CD Updated** (Phase 8)
  - [ ] Shared module build steps removed
  - [ ] Platform builds updated
  - [ ] Pipeline tests pass

- [ ] **Documentation Complete** (Phase 9)
  - [ ] README.md updated
  - [ ] Platform README files updated
  - [ ] Migration guide created
  - [ ] Team notified

---

## Next Steps

After successful migration:

1. **Create Pull Request**:
   ```bash
   git push origin 011-migrate-from-kmp
   # Create PR: "Complete KMP to Platform-Independent Migration"
   # Link to spec: specs/011-migrate-from-kmp/spec.md
   # Include performance improvements and validation results
   ```

2. **Code Review**:
   - Request review from team leads
   - Highlight key changes per phase
   - Provide baseline vs post-migration metrics

3. **Merge to Main**:
   - After approval, merge PR
   - Keep 011-migrate-from-kmp branch for 2 weeks (safety)
   - Keep archive/shared-module branch indefinitely

4. **Monitor Production**:
   - Watch for regressions after merge
   - Monitor build times in CI/CD
   - Collect developer feedback

5. **Future Refactoring** (Optional):
   - iOS: Remove GetAnimalsUseCase, call repositories directly from ViewModels (per MVVM-C guidelines)
   - Android: Consider additional MVI pattern refinements
   - Both: Continue platform-specific optimizations

---

## Resources

- **Feature Spec**: `/specs/011-migrate-from-kmp/spec.md`
- **Research**: `/specs/011-migrate-from-kmp/research.md`
- **Data Model**: `/specs/011-migrate-from-kmp/data-model.md`
- **Kotlin-Swift Contract**: `/specs/011-migrate-from-kmp/contracts/kotlin-swift-mapping.md`
- **Constitution**: `.specify/memory/constitution.md` (Platform Independence principle)

## Support

For questions or issues during migration:
- Review troubleshooting section above
- Check git tags for phase reference points
- Consult archive/shared-module branch if needed
- Contact migration lead or team lead

