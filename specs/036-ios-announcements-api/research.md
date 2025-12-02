# Research: iOS Announcements API Integration

**Feature**: 036-ios-announcements-api  
**Date**: 2025-12-01  
**Status**: Complete

## Overview

This document consolidates research findings and design decisions for integrating iOS app with backend REST API endpoints. All clarifications were resolved during spec creation phase.

## Research Topics

### 1. Error Handling Strategy

**Question**: What happens when backend API returns malformed JSON or unexpected data structure?

**Decision**: Log error (print), show generic error message to user (no analytics tracking)

**Rationale**: 
- Simplicity: iOS development environment, no analytics infrastructure in scope
- User experience: Generic error message prevents confusion from technical details
- Debugging: Print statements sufficient for development/testing phase
- No crash: App remains stable even with malformed responses

**Implementation Approach**:
- Use Swift Codable for JSON parsing with do-catch error handling
- Log parsing errors with `print()` for console visibility
- Display user-friendly message: "Unable to load data. Please try again later."
- No retry mechanism (user must navigate away and return)

---

### 2. Handling Missing Required Fields and Invalid Data

**Question**: What happens when backend returns announcement with missing required fields or invalid data?

**Decision**: Use failable initializer (init?) for DTO→Domain conversion + compactMap to skip invalid items

**Rationale**:
- Graceful degradation: Show valid announcements even if some items are corrupted
- User experience: Better to show 9/10 items than nothing
- Type safety: Domain models remain non-optional, invalid data filtered before domain layer
- Simple implementation: compactMap naturally filters out nil results from failable init

**Implementation Approach**:
- DTO decoding: Swift Codable decodes JSON to DTOs (throws if JSON structure is invalid)
- Domain conversion: Failable init (`init?`) converts DTO → Domain model
  - Returns `nil` if invalid species/status enum
  - Returns `nil` if date parsing fails
  - Returns `nil` if any data validation fails
  - Logs warning with item ID for debugging
- Repository: Uses `compactMap` to filter out invalid items: `listResponse.data.compactMap { Animal(from: $0) }`
- Result: Valid items displayed, invalid items logged and skipped

**For Single Item (Details)**:
- Failable init returns `nil` → repository throws `RepositoryError.invalidData`
- ViewModel displays error message: "Unable to load pet details"

**Alternatives Considered**:
- Option A (make all fields optional): Rejected - loses type safety, complicates UI logic
- Option C (substitute default values): Rejected - misleading data, incorrect for pet announcements
- Throwing init: Rejected - one bad item crashes entire list, poor UX

---

### 3. Network Timeout Configuration

**Question**: What is the network timeout duration for API requests?

**Decision**: Use URLSession system default timeout value (no custom timeout configuration)

**Rationale**:
- Default timeout: URLSession provides ~60 seconds for resource timeout, 7 days for request timeout
- Simplicity: No custom URLSessionConfiguration needed
- iOS standard: System defaults are well-tested and appropriate for most use cases
- User can interrupt: Manual cancellation via navigation or app backgrounding

**Implementation Approach**:
- Use default `URLSession.shared` for HTTP requests
- No custom timeout parameters
- If user wants to retry → navigate away and return to screen (triggers new request)
- Document in tests that timeout is system default (~60s)

**Alternatives Considered**:
- Custom 30s timeout: Rejected - too aggressive, may fail on slow networks
- Custom 10s timeout: Rejected - inappropriate for mobile networks with variable latency
- Retry strategy with exponential backoff: Rejected - out of scope, adds complexity

---

### 4. Backend Base URL Configuration

**Question**: What is the backend base URL and configuration strategy for different environments?

**Decision**: `http://localhost:3000` (development), easily configurable from code

**Rationale**:
- Development environment: Local backend server on port 3000
- HTTP allowed: iOS App Transport Security (ATS) exception required for insecure transport
- Code-level configuration: Simple constant in Swift code (no need for build configurations yet)
- Easy to change: Single constant can be updated for different environments without recompilation

**Implementation Approach**:
- Create `APIConfig.swift` with base URL constant: `static let baseURL = "http://localhost:3000"`
- Add ATS exception to `Info.plist` for localhost (allow insecure loads for localhost domain)
- No authentication headers (development/testing environment)
- Future: Can be enhanced with build configurations (Debug/Release) or environment variables

**Alternatives Considered**:
- Environment variables: Rejected - overkill for current scope, no multi-environment requirement yet
- Build configurations: Rejected - adds complexity, not needed for single development environment
- HTTPS: Rejected - requires SSL certificates, inappropriate for localhost development

---

### 5. Location Filtering Implementation

**Question**: How does the app handle location permissions for API filtering?

**Decision**: When granted, send lat/lng query parameters; when denied/unavailable, call endpoint without parameters

**Rationale**:
- Backend API design: Supports optional `lat` and `lng` query parameters
- User privacy: Location permissions must be explicitly granted
- Graceful degradation: App remains functional without location (shows all announcements)
- iOS location services: Existing permission handling from previous features

**Implementation Approach**:
- Check location authorization status before API call
- If `.authorizedWhenInUse` or `.authorizedAlways` → fetch current location and include in request
- If `.denied` or `.notDetermined` → call API without location parameters (backend returns all announcements)
- No location permission prompt in this feature (already handled in previous features)
- ViewModels check location status, repositories receive optional lat/lng parameters

**Alternatives Considered**:
- Always require location: Rejected - poor UX, blocks users who deny permission
- Prompt for permission in this feature: Rejected - location permission already handled in announcement creation flow

---

### 6. Race Condition Handling (Rapid Screen Switching)

**Question**: What happens when user rapidly switches between animal list and details screens?

**Decision**: Cancel previous request before starting new one (task cancellation via async/await)

**Rationale**:
- Swift Concurrency: Native support for task cancellation
- Resource efficiency: Prevents wasted network bandwidth and processing
- Correct data: Ensures displayed data matches current screen/selection
- No custom cancellation logic needed: async/await tasks are automatically cancellable

**Implementation Approach**:
- Use `Task` for all async operations in ViewModels
- Store Task reference in ViewModel property
- Cancel previous task before starting new fetch: `previousTask?.cancel()`
- Check for cancellation in repository if needed: `try Task.checkCancellation()`
- Swift Concurrency handles cleanup automatically when task is cancelled

**Alternatives Considered**:
- No cancellation: Rejected - wastes resources, may display stale data
- Operation queues: Rejected - more complex, async/await is more idiomatic
- Debouncing/throttling: Rejected - unnecessary delay, task cancellation is instant

---

### 7. Large Response Handling (1000+ Announcements)

**Question**: How does the app handle backend returning more than 1000 announcements?

**Decision**: Load all data into memory, iOS UI handles scrolling (backend should implement pagination/limits if needed)

**Rationale**:
- Backend responsibility: Backend should limit response size to reasonable amounts
- SwiftUI List/LazyVStack: Built-in lazy loading and efficient scrolling for large datasets
- iOS memory: Modern iPhones can handle thousands of small objects in memory
- No client-side pagination in this iteration: Simplifies implementation
- Performance assumption: Backend will not return unreasonable amounts of data

**Implementation Approach**:
- No special handling for large lists in iOS client
- SwiftUI `List` with `ForEach` provides lazy rendering automatically
- If performance issues arise → backend implements pagination (future enhancement)
- No UI for "load more" or pagination controls

**Alternatives Considered**:
- Client-side pagination: Rejected - adds complexity, backend should handle limits
- Infinite scroll: Rejected - requires backend pagination support first
- Memory warnings: Rejected - over-engineering for expected data volumes

---

### 8. Deduplication of Announcements

**Question**: What happens if backend returns duplicate announcement IDs in the list response?

**Decision**: Deduplicate by ID (keep first occurrence), log warning (print)

**Rationale**:
- Data integrity: Backend should not return duplicates, but handle gracefully if it does
- User experience: Prevent confusion from seeing same pet twice
- Simplicity: Keep first occurrence (simpler than last or merge logic)
- Debugging aid: Log warning to identify backend issues

**Implementation Approach**:
- After decoding JSON array, filter announcements by unique IDs
- Use `Dictionary(uniqueKeysWithValues:)` or `Set` to deduplicate by ID
- Log warning with `print("Warning: Duplicate announcement ID: \(id)")` for each duplicate
- Return deduplicated array to ViewModel

**Alternatives Considered**:
- Keep last occurrence: Rejected - no clear advantage over first
- Merge duplicates: Rejected - complex logic, unclear which fields to prioritize
- No deduplication: Rejected - poor UX, displays duplicate items

---

## Technology Stack Validation

### Networking
- **URLSession**: iOS native HTTP client, supports async/await, no third-party dependencies
- **Codable**: Swift native JSON parsing, type-safe, compiler-enforced schemas
- **async/await**: Modern Swift Concurrency for asynchronous operations
- **@MainActor**: Ensures UI updates on main thread

**Decision**: Use URLSession with Codable - no external HTTP libraries needed (Alamofire not required)

---

### Error Handling
- **Swift Result type**: Not used (ViewModels use try/catch with async/await)
- **Optional chaining**: For missing optional fields in models
- **do-catch blocks**: Standard error handling for network and parsing errors

**Decision**: Use Swift error handling with throw/try/catch pattern, consistent with Swift Concurrency

---

### Testing
- **XCTest**: iOS native testing framework
- **Fake repositories**: Protocol-based fakes for ViewModel tests (no mocking frameworks)
- **Async/await tests**: XCTest supports async test methods natively

**Decision**: No third-party testing dependencies - XCTest sufficient for unit and integration tests

---

## API Contract Validation

### GET /api/v1/announcements
- **Method**: GET
- **Query Parameters** (optional): `lat` (number), `lng` (number)
- **Response**: `{ data: [Announcement] }`
- **Status Codes**: 200 (success), 500 (server error)

### GET /api/v1/announcements/:id
- **Method**: GET
- **Path Parameter**: `id` (string/number)
- **Response**: Single `Announcement` object (no wrapper)
- **Status Codes**: 200 (success), 404 (not found), 500 (server error)

### Backend Response Format (from backend spec)
```typescript
Announcement {
  id: number;
  petName: string;
  species: string; // "dog", "cat", "bird", etc.
  status: string; // "missing", "found"
  photoUrl: string;
  lastSeenDate: string; // ISO 8601 date
  latitude: number;
  longitude: number;
  breed?: string; // optional
  microchipNumber?: string; // optional
  contactEmail?: string; // optional
  contactPhone: string;
  reward?: number; // optional
  description: string;
  createdAt: string; // ISO 8601 datetime
  updatedAt: string; // ISO 8601 datetime
}
```

**Field Mapping (Backend → iOS)**:
- `petName` → `name`
- `species` → `species` (map to enum)
- `status` → `status` (map to enum)
- `photoUrl` → `photoUrl`
- `lastSeenDate` → `lastSeenDate` (parse to Date)
- `latitude`, `longitude` → `Coordinate` struct
- All other fields map 1:1

**Validation**: Backend endpoints already implemented and tested. iOS client will consume existing API without backend changes.

---

## Summary

All technical clarifications resolved. No additional research required. Ready to proceed to Phase 1 (Design).

**Key Decisions**:
1. URLSession with system defaults for networking
2. Codable for JSON parsing with graceful error handling
3. Skip invalid items when possible, fail list when Codable cannot decode
4. Task cancellation for race condition prevention
5. Location filtering with optional query parameters
6. Deduplicate by ID (keep first), log warnings
7. No client-side pagination (backend responsibility)
8. HTTP localhost development environment with ATS exception

**Next Steps**: Generate data-model.md and API contracts in Phase 1.

