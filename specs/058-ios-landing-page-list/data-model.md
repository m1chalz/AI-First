# Data Model: iOS Landing Page (Home Tab)

**Branch**: `058-ios-landing-page-list` | **Date**: 2025-12-16

This document defines the data models, state management structures, and validation rules for the iOS landing page feature.

---

## Core Entities

### 1. Announcement (Existing Model - Reused)

Represents a pet announcement fetched from the backend API.

**Source**: `/iosApp/iosApp/Domain/Models/Announcement.swift` (existing)

**Fields**:
| Field | Type | Required | Description | Validation |
|-------|------|----------|-------------|------------|
| `id` | String | Yes | Unique identifier for announcement | Non-empty string |
| `petName` | String | Yes | Name of the pet | Non-empty string, max 100 characters |
| `species` | String | Yes | Pet species (e.g., "Dog", "Cat") | Non-empty string from predefined list |
| `status` | AnnouncementStatus | Yes | Announcement status (Lost/Found) | Enum value |
| `photoUrl` | String? | No | URL to pet photo | Valid URL or nil |
| `location` | Location | Yes | Pet's last known location | Valid Location object |
| `description` | String? | No | Additional details about pet | Max 500 characters |
| `contactName` | String | Yes | Owner/reporter contact name | Non-empty string |
| `contactPhone` | String | Yes | Owner/reporter phone number | Valid phone format |
| `createdAt` | Date | Yes | Announcement creation timestamp | ISO 8601 date |
| `updatedAt` | Date | Yes | Last update timestamp | ISO 8601 date |

**Relationships**:
- Has one `Location` (embedded)
- Has zero or more tags (future extension)

**State Transitions**: N/A (read-only model for landing page)

**Validation Rules**:
- `id` must be non-empty
- `petName` must be non-empty and max 100 characters
- `species` must be from predefined list: ["Dog", "Cat", "Bird", "Other"]
- `status` must be valid enum value (.lost or .found)
- `photoUrl` must be valid URL format if present (scheme: http/https)
- `contactPhone` must match phone number pattern (digits, spaces, dashes, parentheses allowed)
- `createdAt` must be valid ISO 8601 timestamp
- `updatedAt` must be >= `createdAt`

**Business Rules**:
- Landing page displays only the 5 most recent announcements (sorted by `createdAt` descending)
- Announcements with invalid data (missing required fields) are skipped during client-side filtering
- Photo display fallback: Show placeholder image if `photoUrl` is nil or image load fails

---

### 2. Location (Existing Model - Reused)

Represents geographic coordinates for pet location.

**Source**: `/iosApp/iosApp/Domain/Models/Location.swift` (existing)

**Fields**:
| Field | Type | Required | Description | Validation |
|-------|------|----------|-------------|------------|
| `latitude` | Double | Yes | Latitude coordinate | -90.0 to 90.0 |
| `longitude` | Double | Yes | Longitude coordinate | -180.0 to 180.0 |
| `address` | String? | No | Human-readable address | Max 200 characters |

**Validation Rules**:
- `latitude` must be in range [-90.0, 90.0]
- `longitude` must be in range [-180.0, 180.0]
- `address` max 200 characters if present

**Business Rules**:
- Distance calculation: Use Haversine formula to calculate distance from user's current location
- Distance display: Show distance in kilometers with 1 decimal place (e.g., "2.3 km")
- Distance visibility: Only show if user has granted location permissions

---

### 3. AnnouncementStatus (Existing Enum - Reused)

Enum representing announcement type.

**Source**: `/iosApp/iosApp/Domain/Models/AnnouncementStatus.swift` (existing)

**Values**:
- `.lost` - Pet is lost (missing)
- `.found` - Pet was found

**Usage**: Displayed on announcement cards as status badge (e.g., "LOST" in red, "FOUND" in green)

---

## Query Configuration Models (New)

### 4. AnnouncementListQuery

Configuration object for querying announcements with filtering and sorting options.

**Source**: `/iosApp/iosApp/Domain/Models/AnnouncementListQuery.swift` (new)

**Structure**:
```swift
struct AnnouncementListQuery {
    let limit: Int?
    let sortBy: SortOption
    let location: Coordinate?
    
    enum SortOption {
        case createdAtDescending
        case createdAtAscending
        case distanceFromUser
    }
}
```

**Fields**:
| Field | Type | Required | Description | Default Value |
|-------|------|----------|-------------|---------------|
| `limit` | Int? | No | Maximum number of results (nil = all) | nil |
| `sortBy` | SortOption | Yes | Sorting order for results | .createdAtDescending |
| `location` | Coordinate? | No | User's current location for distance calculation (nil = no location) | nil |

**Usage Examples**:
- **Full list query**: `AnnouncementListQuery.defaultQuery(location: userLocation)` → Returns all announcements sorted by date with distance info
- **Landing page query**: `AnnouncementListQuery.landingPageQuery(location: userLocation)` → Returns 5 most recent announcements with distance info
- **No location**: `AnnouncementListQuery.landingPageQuery(location: nil)` → Returns 5 most recent announcements without distance info

**Business Rules**:
- `limit = nil` means no limit (return all matching announcements)
- `limit = 5` means return first 5 items after sorting
- `sortBy = .createdAtDescending` is default (newest first)
- Future extension: `.distanceFromUser` requires location parameter

---

## View State Models (New)

### 5. AnnouncementCardsListViewModel State

Represents the UI state for the autonomous announcement cards list component.

**Source**: `/iosApp/iosApp/ViewModels/AnnouncementCardsListViewModel.swift` (new, managed via @Published properties)

**Structure** (as @Published properties on AnnouncementCardsListViewModel):

```swift
@MainActor
class AnnouncementCardsListViewModel: ObservableObject {
    @Published private(set) var cardViewModels: [AnnouncementCardViewModel] = []
    @Published private(set) var isLoading: Bool = false
    @Published private(set) var errorMessage: String? = nil
    
    private let repository: AnnouncementRepositoryProtocol
    private var query: AnnouncementListQuery  // Mutable - can be updated by parent
    private let onAnnouncementTapped: (String) -> Void
}
```

**Fields**:
| Field | Type | Initial Value | Description |
|-------|------|---------------|-------------|
| `cardViewModels` | [AnnouncementCardViewModel] | [] | Array of card ViewModels (filtered/sorted per query) |
| `isLoading` | Bool | false | Loading indicator state |
| `errorMessage` | String? | nil | Error message text (nil when no error) |
| `repository` | AnnouncementRepositoryProtocol | (injected) | Data source for announcements |
| `query` | AnnouncementListQuery | (injected, mutable) | Configuration for filtering/sorting/location (parent prepares query with location) |
| `onAnnouncementTapped` | (String) -> Void | (injected) | Callback for card tap events |

**State Transitions**:

1. **Initial State** → **Loading State**:
   - Trigger: Parent ViewModel calls `setQuery()` with query containing location
   - Transition: Set `isLoading = true`, `errorMessage = nil`

2. **Loading State** → **Success State**:
   - Trigger: Repository returns announcements successfully
   - Transition: Set `cardViewModels = filtered/sorted/converted array`, `isLoading = false`, `errorMessage = nil`

3. **Loading State** → **Error State**:
   - Trigger: Repository throws error
   - Transition: Set `errorMessage = error description`, `isLoading = false`, `cardViewModels = []`

4. **Error State** → **Loading State**:
   - Trigger: User taps retry button in ErrorView (calls `reload()` with current query)
   - Transition: Set `isLoading = true`, `errorMessage = nil`

5. **Success State** → **Empty State**:
   - Trigger: Repository returns empty array
   - Transition: Set `cardViewModels = []`, `isLoading = false`, `errorMessage = nil`

**Public Methods**:
- `setQuery(_ newQuery: AnnouncementListQuery)`: Sets new query and triggers reload (called by parent ViewModel)
- `reload()`: Reloads with current query (called by retry button in error screen)

**Business Rules**:
- `cardViewModels` array is filtered/sorted according to `query` configuration
- `query.limit` controls max items (nil = all, 5 = landing page limit)
- `isLoading` and `errorMessage` are mutually exclusive with success state
- Location fetching is delegated to LocationPermissionHandler (non-blocking)
- If `cardViewModels` is empty and not loading: AnnouncementCardsListView displays EmptyStateView
- If `errorMessage` is non-nil: AnnouncementCardsListView displays ErrorView with retry option
- If `isLoading` is true: AnnouncementCardsListView displays LoadingView

---

### 6. LandingPageViewModel State (Parent ViewModel)

Represents the parent ViewModel for the landing page screen.

**Source**: `/iosApp/iosApp/ViewModels/LandingPageViewModel.swift` (new)

**Structure**:
```swift
@MainActor
class LandingPageViewModel: ObservableObject {
    let listViewModel: AnnouncementCardsListViewModel  // Child component ViewModel
    private let locationHandler: LocationPermissionHandler
    
    init(repository: AnnouncementRepositoryProtocol,
         locationHandler: LocationPermissionHandler,
         onAnnouncementTapped: @escaping (String) -> Void) {
        
        self.locationHandler = locationHandler
        
        // Create child with initial empty query
        self.listViewModel = AnnouncementCardsListViewModel(
            repository: repository,
            query: .landingPageQuery(location: nil),
            onAnnouncementTapped: onAnnouncementTapped
        )
        
        // Note: loadData() is called from View .task, not here
    }
    
    func loadData() async {
        let result = await locationHandler.requestLocationWithPermissions()
        let queryWithLocation = AnnouncementListQuery.landingPageQuery(location: result.location)
        listViewModel.setQuery(queryWithLocation)  // Triggers reload
    }
}
```

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| `listViewModel` | AnnouncementCardsListViewModel | Child ViewModel managing list state and data |
| `locationHandler` | LocationPermissionHandler | Parent handles location permissions, fetches location, and updates child query |

**Responsibilities**:
- Create and configure `AnnouncementCardsListViewModel` with initial empty query (limit: 5, location: nil)
- Expose `loadData()` method called from View `.task`
- Handle location permissions via `LocationPermissionHandler` (parent-controlled)
- Fetch user location and set query on child ViewModel via `setQuery()` (triggers automatic reload)
- Pass through cross-tab navigation closure (`onAnnouncementTapped`)
- No state management for list (delegated to child ViewModel)
- Future: Can add landing page-specific logic (e.g., analytics, refresh triggers)

**Loading Flow**:
1. LandingPageView has `.task { await viewModel.loadData() }`
2. `loadData()` fetches location from LocationPermissionHandler
3. `loadData()` calls `listViewModel.setQuery(queryWithLocation)`
4. Child ViewModel automatically reloads with new query

**Autonomous Component Pattern**:
- LandingPageViewModel acts as thin wrapper/factory for child component
- All list state (`cardViewModels`, `isLoading`, `errorMessage`) managed by `listViewModel`
- Parent can trigger reload: `listViewModel.reload()` when needed
- View observes child ViewModel: `@ObservedObject var viewModel: LandingPageViewModel` → accesses `viewModel.listViewModel`

---

## API Response Models (Existing - No Changes)

### 5. AnnouncementsResponse

Response structure for GET /api/v1/announcements endpoint.

**Source**: Existing backend API contract

**Structure**:
```json
{
  "announcements": [
    {
      "id": "uuid-string",
      "petName": "Max",
      "species": "Dog",
      "status": "Lost",
      "photoUrl": "https://example.com/photo.jpg",
      "location": {
        "latitude": 52.2297,
        "longitude": 21.0122,
        "address": "Warsaw, Poland"
      },
      "description": "Golden Retriever, friendly",
      "contactName": "John Doe",
      "contactPhone": "+48 123 456 789",
      "createdAt": "2025-12-15T10:30:00Z",
      "updatedAt": "2025-12-15T10:30:00Z"
    }
  ]
}
```

**Mapping**:
- Backend response is parsed into Swift `[Announcement]` array by existing AnnouncementRepository
- Landing page reuses existing parsing logic (no custom mapping required)

---

## Validation Summary

### Client-Side Validation (iOS)

| Entity | Field | Rule | Error Handling |
|--------|-------|------|----------------|
| Announcement | `id` | Non-empty | Skip invalid item during filtering |
| Announcement | `petName` | Non-empty, max 100 chars | Skip invalid item |
| Announcement | `species` | From predefined list | Skip invalid item |
| Announcement | `photoUrl` | Valid URL or nil | Use placeholder image on load failure |
| Announcement | `createdAt` | Valid ISO 8601 date | Skip invalid item |
| Location | `latitude` | -90.0 to 90.0 | Skip announcement with invalid location |
| Location | `longitude` | -180.0 to 180.0 | Skip announcement with invalid location |

**Error Handling Strategy**:
- Invalid announcements are silently skipped (not displayed)
- Network errors are displayed to user with retry option
- Image load failures show placeholder image (no error message)
- Location permission denial hides distance info (no error message)

---

## Summary

The landing page reuses all existing data models (Announcement, Location, AnnouncementStatus) without modifications. New UI state management is handled by LandingPageViewModel using SwiftUI's @Published properties. Client-side filtering ensures only the 5 most recent valid announcements are displayed. Validation occurs during JSON parsing (existing repository logic) and during client-side filtering (ViewModel logic). No backend changes or new API contracts are required.

