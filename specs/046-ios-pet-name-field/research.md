# Research: iOS - Add Pet Name Field to Animal Details Screen

**Feature**: 046-ios-pet-name-field  
**Date**: December 4, 2025  
**Status**: Complete

## Research Summary

No additional research required. All technical details were clearly specified in the feature specification.

## Technical Decisions

### Decision 1: Field Naming Convention
- **Decision**: Use "Animal name" in UI, `petName` in code and API
- **Rationale**: 
  - UI label matches Figma design (node 706:8443)
  - `petName` in flow state and API matches existing backend field name
  - Maintains consistency across iOS client and backend API
- **Alternatives considered**: 
  - Using `animalName` throughout - rejected to maintain backend API compatibility

### Decision 2: Data Validation Strategy
- **Decision**: Client-side whitespace trimming only, backend handles length validation
- **Rationale**:
  - Simplifies iOS implementation (no character limit enforcement)
  - Backend already has validation logic
  - Send `null` or omit field when trimmed value is empty/whitespace-only
- **Alternatives considered**:
  - Client-side character limit - rejected per spec requirement FR-009 (no client-side limit)
  - Client-side input filtering (emoji/special chars) - rejected per spec clarification (accept all Unicode)

### Decision 3: Test Identifier Format
- **Decision**: `animalDescription.animalNameTextField.input`
- **Rationale**:
  - Follows existing pattern from `AnimalDescriptionViewModel`
  - Matches constitution requirement: `{screen}.{element}.{action}`
  - Consistent with other text fields in the flow
- **Alternatives considered**: None - convention is established

### Decision 4: ViewModel Property Type
- **Decision**: `@Published var petName: String` (not optional)
- **Rationale**:
  - SwiftUI TextField binding requires non-optional String
  - Empty string represents "no pet name" state
  - Convert empty/whitespace-only to `nil` when submitting to API
- **Alternatives considered**:
  - Optional `String?` - rejected due to TextField binding complexity

### Decision 5: Flow State Property Type
- **Decision**: `var petName: String?` (optional)
- **Rationale**:
  - Matches API field type (optional)
  - Clearly represents "pet name not provided" state
  - Flow state already uses optionals for other optional fields
- **Alternatives considered**:
  - Non-optional with empty string - rejected to maintain consistency with flow state pattern

## Technology Choices

### SwiftUI TextField
- **Chosen**: SwiftUI TextField with `.textFieldStyle(.roundedBorder)`
- **Rationale**: 
  - Consistent with existing Animal Details form fields
  - Native SwiftUI component, no custom implementation needed
  - Supports placeholder text for "Animal name (optional)" label
- **Best practices**:
  - Use `.accessibilityIdentifier()` for E2E testing
  - Bind to ViewModel `@Published` property for two-way data flow
  - Use `.submitLabel(.next)` for keyboard "Next" button

### Swift Concurrency (async/await)
- **Chosen**: Existing pattern - API call uses `async`/`await` with `@MainActor`
- **Rationale**:
  - No new async operations required (only flow state update)
  - Existing API submission already handles async submission
- **Best practices**:
  - ViewModel already marked with `@MainActor`
  - No additional concurrency handling needed for petName field

### XCTest for Unit Testing
- **Chosen**: XCTest with Swift Concurrency (existing test framework)
- **Rationale**:
  - Consistent with existing iOS test suite
  - Native Apple testing framework, no additional dependencies
- **Best practices**:
  - Follow Given-When-Then structure
  - Test names: `test_petName_whenUserEntersText_shouldUpdateFlowState()`
  - Cover: property updates, whitespace trimming, API payload inclusion

## Integration Patterns

### Existing Flow State Pattern
- **Pattern**: `ReportMissingPetFlowState` model holds multi-step form data
- **Implementation**: Add `var petName: String?` property
- **No changes needed**: Existing flow state initialization, serialization patterns work as-is

### Existing ViewModel Pattern
- **Pattern**: `AnimalDescriptionViewModel` manages form state with `@Published` properties
- **Implementation**: Add `@Published var petName: String = ""`
- **Computed property**: `petNameTextFieldModel` returns `ValidatedTextField.Model` (if that pattern exists)
- **No changes needed**: Existing ViewModel initialization, coordinator communication patterns work as-is

### Existing API Submission Pattern
- **Pattern**: Coordinator triggers API submission with flow state data
- **Implementation**: Include `petName` in request body (trim and convert empty to `nil`)
- **No changes needed**: Existing error handling mechanism applies to petName field

## Unknowns Resolved

No unknowns were identified. All requirements were clearly specified in the feature specification, including:
- Field positioning (after "Date of disappearance", before "Animal species")
- Field naming conventions (UI label, flow state property, API field)
- Validation rules (client-side trimming only, no character limit)
- Test identifier format (`animalDescription.animalNameTextField.input`)
- Error handling (use existing flow error mechanism)
- Accessibility support (none - per project-wide approach)

## Dependencies

No new dependencies required:
- SwiftUI: Already in use for iOS views
- Foundation: Already in use for String manipulation (trimming)
- XCTest: Already in use for iOS unit tests

## Performance Considerations

No performance concerns:
- Single text field addition (negligible memory/CPU impact)
- No network calls triggered by field interaction
- Standard SwiftUI rendering performance

