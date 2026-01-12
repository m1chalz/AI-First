# Implementation Plan: iOS Send Report Data to Backend (Status by Flow)

**Branch**: `KAN-34-send-data-to-backend` | **Date**: 2026-01-09 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-34-send-data-to-backend/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Enable iOS app to send announcement reports with correct status based on the reporting flow. Currently status is hardcoded to `.active` (MISSING) in `CreateAnnouncementMapper`. The implementation will:
1. Add `status` field to `CreateAnnouncementData` domain model
2. Add `status` property to `PetReportFlowStateProtocol`
3. Set appropriate status in Missing flow (`.active` → MISSING) and Found flow (`.found` → FOUND)
4. Update mapper to use status from domain model instead of hardcoded value

## Technical Context

**Language/Version**: Swift 5.x (iOS 18+)
**Primary Dependencies**: SwiftUI, URLSession, Foundation
**Storage**: N/A (backend storage via REST API)
**Testing**: XCTest with Swift Concurrency (async/await)
**Target Platform**: iOS 18+ (iPhone 16 Simulator for tests)
**Project Type**: Mobile (iOS)
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)
**Constraints**: N/A
**Scale/Scope**: Single platform (iOS-only), ~5 files affected

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is iOS-only feature. Backend/Web/Android checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS-only spec)
  - iOS: Domain models, ViewModels, repositories in `/iosApp` ✓
  - Web: N/A (iOS-only spec)
  - Backend: N/A (no backend changes required - API already supports status field)
  - NO shared compiled code between platforms ✓
  - Violation justification: _N/A - compliant_

- [ ] **Android MVI Architecture**: N/A - iOS-only spec

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation ✓ (existing coordinators unchanged)
  - ViewModels conform to `ObservableObject` with `@Published` properties ✓
  - ViewModels communicate with coordinators via methods or closures ✓
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✓
  - Violation justification: _N/A - compliant, minimal changes to existing architecture_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: Repository protocols in `/iosApp/iosApp/Domain/Repositories/` ✓
  - Implementations in platform-specific data/repositories modules ✓
  - Use cases reference interfaces, not concrete implementations ✓
  - Violation justification: _N/A - existing pattern maintained_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: MUST use manual DI - setup in `/iosApp/iosApp/DI/` ✓
  - Violation justification: _N/A - no new DI required, using existing ServiceContainer_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/`, run via XCTest ✓
  - Coverage target: 80% line + branch coverage per platform ✓
  - Violation justification: _N/A - will add/update tests for mapper and service_

- [ ] **End-to-End Tests**: N/A - E2E test changes not in scope for this ticket
  - Violation justification: _iOS status change is internal implementation detail; E2E tests verify visible behavior which doesn't change_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` ✓
  - No Combine, RxSwift, or callback-based patterns ✓
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: N/A - no UI changes
  - Violation justification: _No new interactive UI elements; status is set programmatically based on flow_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`) ✓
  - Document only when purpose is not clear from name alone ✓
  - Violation justification: _N/A - will document status field purpose_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then) ✓
  - Test names follow Swift conventions (camelCase_with_underscores) ✓
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A - `/server` not affected
- [ ] **Backend Code Quality**: N/A - `/server` not affected
- [ ] **Backend Dependency Management**: N/A - `/server` not affected
- [ ] **Backend Directory Structure**: N/A - `/server` not affected
- [ ] **Backend TDD Workflow**: N/A - `/server` not affected
- [ ] **Backend Testing Strategy**: N/A - `/server` not affected

### Web Architecture & Quality Standards (if `/webApp` affected)

- [ ] **Web Technology Stack**: N/A - `/webApp` not affected
- [ ] **Web Code Quality**: N/A - `/webApp` not affected
- [ ] **Web Dependency Management**: N/A - `/webApp` not affected
- [ ] **Web Business Logic Extraction**: N/A - `/webApp` not affected
- [ ] **Web TDD Workflow**: N/A - `/webApp` not affected
- [ ] **Web Testing Strategy**: N/A - `/webApp` not affected

## Project Structure

### Documentation (this feature)

```text
specs/KAN-34-send-data-to-backend/
├── spec.md              # Feature specification ✓
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (backend contract reference)
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (iOS platform)

```text
iosApp/iosApp/
├── Domain/
│   ├── Models/
│   │   ├── CreateAnnouncementData.swift      # ADD: status field
│   │   └── AnnouncementStatus.swift          # EXISTS: .active, .found, .closed
│   └── Services/
│       └── AnnouncementSubmissionService.swift  # UPDATE: pass status from flowState
├── Data/
│   ├── Models/
│   │   └── AnnouncementStatusDTO.swift       # EXISTS: maps domain to backend
│   └── Mappers/
│       └── CreateAnnouncementMapper.swift    # UPDATE: use data.status instead of hardcoded
└── Features/
    └── ReportMissingAndFoundPet/
        ├── Common/
        │   └── Models/
        │       └── PetReportFlowStateProtocol.swift  # ADD: status property
        ├── ReportMissingPet/
        │   └── Models/
        │       └── MissingPetReportFlowState.swift   # ADD: status = .active
        └── ReportFoundPet/
            └── Models/
                └── FoundPetReportFlowState.swift     # ADD: status = .found

iosApp/iosAppTests/
├── Data/
│   └── Mappers/
│       └── CreateAnnouncementMapperTests.swift     # UPDATE: test status mapping
└── Features/
    └── ReportMissingAndFoundPet/
        └── Common/
            └── Services/
                └── AnnouncementSubmissionServiceTests.swift  # UPDATE: test status from flowState
```

**Structure Decision**: iOS-only changes following existing MVVM-C architecture. Domain model updated to include status, mapper updated to use it dynamically, flow states provide correct status based on flow type.

## Complexity Tracking

> No constitution violations requiring justification. Implementation follows existing patterns.

---

## Post-Design Constitution Re-Evaluation

*Re-checked after Phase 1 design completion.*

### Compliance Status: ✅ PASS

All applicable constitution checks remain compliant after design phase:

| Check | Pre-Design | Post-Design | Notes |
|-------|-----------|-------------|-------|
| Platform Independence | ✅ | ✅ | iOS-only, no cross-platform impact |
| iOS MVVM-C Architecture | ✅ | ✅ | Protocol extension, no architecture change |
| Interface-Based Design | ✅ | ✅ | Using existing protocol pattern |
| Dependency Injection | ✅ | ✅ | No new dependencies |
| 80% Test Coverage | ✅ | ✅ | Test updates planned |
| Async Programming | ✅ | ✅ | No async changes |
| Public API Documentation | ✅ | ✅ | Will document new `status` property |
| Given-When-Then Tests | ✅ | ✅ | Test updates follow convention |

### Design Decisions Validated

1. **Adding `status` to protocol as computed property** - Ensures compile-time correctness, each flow returns correct value
2. **No backend changes needed** - API already supports both status values
3. **Minimal file changes** - 6 source files + 2 test files

### Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Breaking existing tests | Low | Low | Fakes need `status` property added |
| Missing status in edge cases | Very Low | Medium | Protocol ensures status always available |

---

## Estimation Update (After PLAN)

Based on detailed design, estimation remains at **1 SP** with increased confidence:

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 1 | 5.2 | ±50% | Gut feel from feature title |
| After SPEC | 1 | 5.2 | ±30% | Status must depend on flow; backend schema is strict |
| After PLAN | 1 | 5.2 | ±20% | 6 source files + 2 test files; existing mapper pattern; no backend changes |
| **After TASKS** | **1** | **5.2** | **±15%** | **14 tasks completed: 7 code + 5 test + 2 polish. Actual effort ~1.5 days** |

**Rationale**: Design confirmed this is a straightforward refactoring:
- Adding one field to domain model
- Adding one computed property to protocol + 2 implementations
- Updating one mapper line (hardcoded → dynamic)
- Updating one service line (pass status through)
- Test updates are minor (add `status` to fakes, verify mapping)
