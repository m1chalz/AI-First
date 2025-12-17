# Research: Home Lost Pets Teaser

## Overview
Research existing patterns and implementations to ensure consistency with the current codebase.

## Research Tasks

### Task 1: Existing Announcements Backend Endpoint
**Objective**: Understand the structure and response format of the existing announcements endpoint used for lost pets data.

**Findings**:
- Endpoint: GET /api/v1/announcements (existing)
- Response format: JSON array of announcement objects
- Each announcement includes: id, title, description, createdAt, location, images, contactInfo
- Filtering: Support for type=LOST_PET parameter
- Sorting: Default sort by createdAt descending (newest first)

**Decision**: Use existing endpoint with type=LOST_PET filter and limit=5 parameter
**Rationale**: Maintains consistency with existing data fetching patterns and avoids custom endpoints

### Task 2: Existing Lost Pets List Implementation
**Objective**: Analyze the current lost pets list component to understand tile design and navigation patterns.

**Findings**:
- Component: LostPetsList composable in /ui/lostpets/
- Tile design: LostPetListItem composable with image, title, description, date
- Navigation: Uses UiEffect.NavigateToLostPetDetails(petId) pattern
- Test IDs: Follow naming convention {screen}.{element}.{action}

**Decision**: Reuse LostPetListItem composable and existing navigation effect
**Rationale**: Ensures visual consistency and leverages proven navigation patterns. No separate LostPetsTeaserItem needed.

### Task 3: Existing Domain Models and Repository Interfaces
**Objective**: Review existing LostPet domain model and repository interfaces.

**Findings**:
- Domain model: LostPet data class exists with id, title, description, createdAt, location, images, contactInfo
- Repository interface: Following project pattern with suspend functions that throw exceptions
- Implementation: LostPetsRepositoryImpl using Ktor HTTP client for API calls

**Decision**: Extend existing LostPetsRepository interface with getRecentLostPets(limit: Int) method
**Rationale**: Single source of truth for lost pets data, avoids repository duplication, maintains consistency with existing patterns. No dedicated repository needed for teaser feature.

### Task 4: Home Screen Integration Points
**Objective**: Identify how to integrate the teaser component into the existing home screen.

**Findings**:
- Home screen: HomeScreen composable with vertical scrolling Column
- Existing components: Header, search bar, featured sections
- Layout: Uses LazyColumn for scrollable content
- State management: HomeViewModel following MVI pattern

**Decision**: Add LostPetsTeaser composable to HomeScreen LazyColumn
**Rationale**: Follows existing home screen patterns and maintains scrollable layout

## Implementation Decisions

| Decision | Rationale | Alternatives Considered |
|----------|-----------|-------------------------|
| Reuse existing LostPetListItem | Ensures visual consistency with full list | Custom teaser-specific design |
| Extend existing repository interface | Maintains single source of truth for lost pets data | Separate teaser repository |
| Use existing navigation effect | Leverages proven navigation patterns | Direct navigation calls |
| Integrate into home LazyColumn | Follows existing home screen structure | Separate scroll container |

## Unknowns Resolved
- ✅ Backend endpoint structure and parameters
- ✅ Existing component designs and patterns  
- ✅ Domain model compatibility
- ✅ Home screen integration approach

## Next Steps
All research complete. Proceed to Phase 1: Design & Contracts.
