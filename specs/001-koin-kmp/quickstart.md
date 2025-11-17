# Quickstart Guide: Adding Your First Dependency with Koin

**Goal**: Add your first dependency to the Koin DI system in 15 minutes

**Prerequisites**: 
- Koin is already configured in the project (Android, iOS, Web)
- You have a basic understanding of dependency injection concepts

---

## Overview

This guide walks you through adding a new dependency (repository, use case, or service) to the Koin DI system. We'll use a `PetRepository` example, but the same pattern applies to any dependency.

**Time estimate**: 15 minutes

---

## Step 1: Define Your Dependency Interface (5 min)

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/repositories/PetRepository.kt`

Create an interface for your dependency in the shared module:

```kotlin
package com.intive.aifirst.petspot.domain.repositories

/**
 * Repository for pet data operations.
 * Platform implementations handle persistence and network calls.
 */
interface PetRepository {
    /**
     * Fetches all pets from data source.
     * @return Result with list of pets or error
     */
    suspend fun getPets(): Result<List<Pet>>
    
    /**
     * Retrieves a single pet by ID.
     * @param id Pet identifier
     * @return Result with pet or error if not found
     */
    suspend fun getPetById(id: String): Result<Pet>
}
```

**Why in shared?**
- Interface is platform-agnostic (no Android/iOS/Web specific code)
- All platforms can reference the same interface
- Use cases depend on interface, not implementation (Dependency Inversion Principle)

---

## Step 2: Register Interface in Domain Module (2 min)

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt`

Add a use case that depends on your interface:

```kotlin
val domainModule = module {
    // Register use case that depends on PetRepository interface
    single { GetPetsUseCase(repository = get()) }
    // Koin will inject PetRepository implementation provided by platform
}
```

**What's happening?**
- `single { }`: Creates one instance for the entire app (singleton)
- `get()`: Tells Koin to inject `PetRepository` (implementation provided by platform module)
- Use case is now available to all platforms

---

## Step 3: Implement Platform-Specific Repository (Android) (5 min)

### 3a. Create Android Implementation

**Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/repositories/PetRepositoryImpl.kt`

```kotlin
package com.intive.aifirst.petspot.data.repositories

import com.intive.aifirst.petspot.domain.repositories.PetRepository
import com.intive.aifirst.petspot.domain.models.Pet

/**
 * Android implementation of PetRepository using Room database and Retrofit API.
 */
class PetRepositoryImpl(
    private val api: PetApi,
    private val database: PetDatabase
) : PetRepository {
    
    override suspend fun getPets(): Result<List<Pet>> = try {
        // Fetch from API
        val pets = api.fetchPets()
        // Cache in database
        database.petDao().insertAll(pets)
        Result.success(pets)
    } catch (e: Exception) {
        // Fallback to cached data
        val cachedPets = database.petDao().getAll()
        if (cachedPets.isNotEmpty()) {
            Result.success(cachedPets)
        } else {
            Result.failure(e)
        }
    }
    
    override suspend fun getPetById(id: String): Result<Pet> = try {
        val pet = api.fetchPetById(id)
        database.petDao().insert(pet)
        Result.success(pet)
    } catch (e: Exception) {
        val cachedPet = database.petDao().getById(id)
        if (cachedPet != null) {
            Result.success(cachedPet)
        } else {
            Result.failure(e)
        }
    }
}
```

### 3b. Register in Android Data Module

**Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DataModule.kt`

```kotlin
val androidDataModule = module {
    // Register repository implementation
    single<PetRepository> { 
        PetRepositoryImpl(
            api = get(),      // Inject PetApi (define below if not exists)
            database = get()  // Inject PetDatabase (define below if not exists)
        ) 
    }
    
    // Register dependencies (if not already defined)
    single { PetApi(baseUrl = "https://api.petspot.com") }
    single { PetDatabase.getInstance(androidContext()) }
}
```

**Done!** Android implementation is ready.

---

## Step 4: Use Your Dependency in a ViewModel (Android) (3 min)

**Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/viewmodels/PetListViewModel.kt`

```kotlin
package com.intive.aifirst.petspot.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.domain.usecases.GetPetsUseCase
import com.intive.aifirst.petspot.domain.models.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for pet list screen.
 * Loads pets using injected use case.
 */
class PetListViewModel(
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    sealed class UiState {
        object Loading : UiState()
        data class Success(val pets: List<Pet>) : UiState()
        data class Error(val message: String) : UiState()
    }
    
    fun loadPets() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getPetsUseCase()
                .onSuccess { pets ->
                    _uiState.value = UiState.Success(pets)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
```

### Register ViewModel

**Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt`

```kotlin
val androidViewModelModule = module {
    viewModel { PetListViewModel(getPetsUseCase = get()) }
}
```

### Use in Compose Screen

```kotlin
@Composable
fun PetListScreen(
    viewModel: PetListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadPets()
    }
    
    when (val state = uiState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> PetList(pets = state.pets)
        is UiState.Error -> ErrorMessage(message = state.message)
    }
}
```

---

## Checklist: Did You Follow These Steps?

- [ ] ✅ **Step 1**: Created interface in `/shared/.../repositories/`
- [ ] ✅ **Step 2**: Registered use case in `domainModule`
- [ ] ✅ **Step 3a**: Implemented platform-specific repository
- [ ] ✅ **Step 3b**: Registered implementation in `androidDataModule`
- [ ] ✅ **Step 4**: Used dependency in ViewModel via constructor injection

---

## Common Mistakes & How to Fix

### ❌ Error: "No definition found for type 'PetRepository'"

**Cause**: Repository implementation not registered in platform module

**Fix**: Add to `androidDataModule`:
```kotlin
single<PetRepository> { PetRepositoryImpl(get(), get()) }
```

---

### ❌ Error: "Circular dependency detected"

**Cause**: A depends on B, B depends on A

**Example**:
```kotlin
single { ServiceA(get<ServiceB>()) }
single { ServiceB(get<ServiceA>()) }
```

**Fix**: Break circular dependency:
1. Introduce interface/mediator
2. Use lazy initialization
3. Refactor design

---

### ❌ ViewModel not updating UI

**Cause**: Using `single { }` instead of `viewModel { }` for ViewModel

**Fix**: Use `viewModel` scope:
```kotlin
viewModel { PetListViewModel(get()) } // ✅ Correct
// single { PetListViewModel(get()) } // ❌ Wrong
```

---

### ❌ "KoinApplication has not been started"

**Cause**: Trying to resolve dependency before `startKoin()` called

**Fix**: Ensure Koin initialized in `Application.onCreate()` (Android) or `@main` init (iOS) or `index.tsx` (Web)

---

## iOS Implementation (Bonus)

For iOS, create Kotlin/Native implementation:

**Location**: `/shared/src/iosMain/kotlin/.../repositories/IosPetRepositoryImpl.kt`

```kotlin
class IosPetRepositoryImpl : PetRepository {
    override suspend fun getPets(): Result<List<Pet>> {
        // iOS-specific implementation (using NSURLSession, CoreData, etc.)
        TODO("Implement using iOS APIs")
    }
    
    override suspend fun getPetById(id: String): Result<Pet> {
        TODO("Implement using iOS APIs")
    }
}
```

Register in shared iOS module:

```kotlin
// /shared/src/iosMain/kotlin/.../di/IosModule.kt
val iosDataModule = module {
    single<PetRepository> { IosPetRepositoryImpl() }
}
```

Use from Swift:

```swift
@MainActor
class PetListViewModel: ObservableObject {
    private let getPetsUseCase: GetPetsUseCase
    @Published var pets: [Pet] = []
    
    init(getPetsUseCase: GetPetsUseCase? = nil) {
        self.getPetsUseCase = getPetsUseCase ?? KoinKt.get()
    }
    
    func loadPets() async {
        let result = await getPetsUseCase.invoke()
        if let pets = result.getOrNil() {
            self.pets = pets
        }
    }
}
```

---

## Web Implementation (Bonus)

For Web, consume shared Kotlin/JS module:

**TypeScript Hook**:

```typescript
import { get } from 'shared';
import { GetPetsUseCase, Pet } from 'shared';

export function usePets() {
    const getPetsUseCase = get<GetPetsUseCase>();
    
    const [pets, setPets] = useState<Pet[]>([]);
    const [loading, setLoading] = useState(false);
    
    const loadPets = async () => {
        setLoading(true);
        try {
            const result = await getPetsUseCase.invoke();
            setPets(result);
        } finally {
            setLoading(false);
        }
    };
    
    useEffect(() => {
        loadPets();
    }, []);
    
    return { pets, loading, loadPets };
}
```

---

## Testing Your Dependency

### Unit Test for Use Case

**Location**: `/shared/src/commonTest/kotlin/.../usecases/GetPetsUseCaseTest.kt`

```kotlin
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
        assertEquals(2, result.getOrNull()?.size)
    }
}
```

### Unit Test for ViewModel

**Location**: `/composeApp/src/androidUnitTest/kotlin/.../viewmodels/PetListViewModelTest.kt`

```kotlin
class PetListViewModelTest : KoinTest {
    private lateinit var viewModel: PetListViewModel
    
    @Before
    fun setup() {
        startKoin {
            modules(module {
                single<GetPetsUseCase> { FakeGetPetsUseCase() }
                viewModel { PetListViewModel(get()) }
            })
        }
        viewModel = get()
    }
    
    @After
    fun tearDown() {
        stopKoin()
    }
    
    @Test
    fun `should update state to Success when pets loaded`() = runTest {
        // Given - ViewModel initialized
        
        // When
        viewModel.loadPets()
        
        // Then
        val state = viewModel.uiState.first { it is UiState.Success }
        assertTrue(state is UiState.Success)
        assertEquals(2, (state as UiState.Success).pets.size)
    }
}
```

---

## Summary

You've successfully:
1. ✅ Defined a repository interface in shared module
2. ✅ Registered use case in domain module
3. ✅ Implemented platform-specific repository
4. ✅ Registered implementation in platform data module
5. ✅ Injected dependency into ViewModel
6. ✅ Used ViewModel in Compose screen

**Next Steps**:
- Add more repositories following the same pattern
- Explore advanced Koin features (qualifiers, scopes)
- Read ADR for architectural decisions: `/docs/adr/001-koin-dependency-injection.md`

**Need Help?**
- Check contracts: `/specs/001-koin-kmp/contracts/`
- Read research: `/specs/001-koin-kmp/research.md`
- Ask team for pair programming session

---

**Estimated Time Spent**: ~15 minutes ✅

