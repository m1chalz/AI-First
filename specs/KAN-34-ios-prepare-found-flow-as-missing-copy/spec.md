# Feature Specification: iOS Prepare Found Pet Flow as Missing Pet Copy

**Feature Branch**: `KAN-34-ios-prepare-found-flow-as-missing-copy`  
**Created**: 2026-01-09  
**Status**: Draft  
**Platform**: iOS  
**Jira Ticket**: KAN-34  
**Design**: N/A (temporary scaffolding; reuse existing Missing Pet flow UI patterns)  
**Input**: User description: "utwórz specyfikację tylko dla iOS o nazwie KAN-34-ios-prepare-found-flow-as-missing-copy. Nie numeruj specki jak masz w skrypcie, nazwa dokładnie taka jak napisałem. Branch o takiej samej nazwie. W tej specyfikacji przygotujemy strukturę projektu pod feature raportowania znalezionego zwierzęcia (069-report-found-pet). Wewnątrz katalogu ReportMissingAndFoundPet stwórzmy katalog ReportFoundPet. Zawartość katalogu powinna być kopią katalogu ReportMissingPet, ale wszystkie klasy wewnątrz powinny mieć zmienione nazwy na prefix FoundPet* zamiast MissingPet*. Analogicznie dla testów. Jest to rozwiązanie przejściowe, flow będziemy modyfikować później, więc cały kontent widoków zostaje bez zmian Na announcementListView stwórzmy przycisk Report Found Animal uruchamiający flow. Część implementacji pod to powinna być już przygotowana."

## Clarifications

### Session 2026-01-09

- Q: Where should the “Report Found Animal” entry point be placed on announcements list? → A: Enable the existing secondary floating action button above the Missing button (`style: .secondary`) with `accessibilityIdentifier` `animalList.reportFoundButton`.
- Q: How should the Report Found Animal flow be presented? → A: Present it modally as `.fullScreen` with its own `UINavigationController` (mirror Missing flow behavior).
- Q: What should happen to announcements list after successful completion of the Found flow? → A: Refresh announcements list (same as Missing flow), using a callback from Found flow to invoke `requestToRefreshData()` on `AnnouncementListViewModel`.
- Q: Should the Found flow actually submit an announcement in this iteration? → A: Yes - it should submit using the same submission logic as the Missing flow (scaffolding copy).
- Q: Should `accessibilityIdentifier` values inside the Found flow be renamed to Found equivalents? → A: Yes - use `reportFoundPet.*` / `foundPet.*` identifiers instead of `reportMissingPet.*` / `missingPet.*`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Enter “Report Found Animal” flow from announcement list (Priority: P1)

As a user who found an animal, I want to start a “Report Found Animal” flow from the announcements list, so I can begin reporting a found animal.

**Why this priority**: This creates the entry point needed for the future found-pet reporting feature while keeping current behavior stable.

**Independent Test**: Can be tested by opening announcements list and tapping “Report Found Animal”, verifying the app starts a multi-step flow (even if it is a temporary copy of missing-pet screens).

**Acceptance Scenarios**:

1. **Given** I am on the announcements list screen, **When** I tap “Report Found Animal”, **Then** the app presents the Report Found Animal flow modally (full-screen) and shows the first screen.
2. **Given** I am in the Report Found Animal flow, **When** I navigate through steps, **Then** the content and behavior matches the current Missing Pet flow (temporary copy, no copy/content changes in this iteration).
3. **Given** I am in the Report Found Animal flow, **When** I use back navigation from the first screen, **Then** I return to the announcements list screen.
4. **Given** I successfully complete the Report Found Animal flow, **When** I return to the announcements list, **Then** the announcements list refresh is triggered (same behavior as Missing flow completion).

---

### User Story 2 - Keep Missing Pet flow unchanged (Priority: P1)

As a user reporting a missing pet, I want the existing “Report Missing Animal” flow to behave exactly the same as before, so I can continue using it without regressions.

**Why this priority**: This is a scaffolding change; it must not regress a working user-facing flow.

**Independent Test**: Can be tested by starting the Missing Pet flow and completing navigation as before, verifying all screens render and interactions remain identical.

**Acceptance Scenarios**:

1. **Given** I am on the announcements list screen, **When** I tap “Report Missing Animal”, **Then** the Missing Pet flow starts and behaves as it did before this change.
2. **Given** the app is built after this change, **When** I run the existing unit tests, **Then** all existing Missing Pet-related tests continue to pass.

---

### Edge Cases

- What happens when both “Report Missing Animal” and “Report Found Animal” are available on the same screen? → Both buttons should work independently and start the correct flow.
- What happens when names clash between Missing and Found types? → All Found types must use a `FoundPet*` prefix to avoid collisions and ambiguity.
- What happens when a user navigates quickly (double-tap) on “Report Found Animal”? → No special handling is required in this iteration; behavior should remain consistent with the existing Missing flow entry point.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST create a new directory `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/`.
- **FR-002**: System MUST copy the contents of `ReportMissingPet/` into `ReportFoundPet/` as a temporary scaffolding for the found-pet flow.
- **FR-003**: All types inside `ReportFoundPet/` MUST be renamed to use the `FoundPet*` prefix instead of `MissingPet*`.
- **FR-004**: All corresponding unit tests MUST be copied and renamed analogously (types and file names) to use the `FoundPet*` prefix.
- **FR-005**: The UI content (texts, layout, components) of the copied found-pet views MUST remain unchanged in this iteration (copy-only; future changes are out of scope).
- **FR-006**: Announcements list screen MUST include a new secondary floating action button labeled “Report Found Animal” (above the existing Missing button), matching existing `FloatingActionButton` patterns.
- **FR-007**: The “Report Found Animal” button MUST have `accessibilityIdentifier` `animalList.reportFoundButton`.
- **FR-008**: Tapping “Report Found Animal” MUST start the Report Found Animal flow.
- **FR-009**: The Report Found Animal flow MUST be presented modally as `.fullScreen` using its own `UINavigationController` (same navigation pattern as Missing flow).
- **FR-010**: Existing “Report Missing Animal” entry point MUST remain intact and unchanged.
- **FR-011**: On successful completion of the Found flow, the announcements list MUST trigger a refresh (same callback pattern as Missing flow: coordinator receives `onReportSent` and calls `AnnouncementListViewModel.requestToRefreshData()`).
- **FR-012**: The Found flow MUST keep the same submission behavior as the Missing flow in this iteration (same service call and payload shape; differentiating Found vs Missing is out of scope).
- **FR-013**: All `accessibilityIdentifier` values inside the Found flow MUST be renamed to Found equivalents (e.g., `reportFoundPet.*` / `foundPet.*`) to avoid collisions and ambiguity with Missing flow identifiers.
- **FR-014**: The iOS app MUST compile successfully after all changes.
- **FR-015**: All iOS unit tests MUST pass after all changes.

### Key Entities *(include if feature involves data)*

- **Found Pet Flow (temporary)**: A UI-only multi-step flow that currently mirrors the Missing Pet flow behavior and screens.
- **Found Pet Types**: All classes/structs/enums used by the found-pet flow, prefixed with `FoundPet*` to clearly indicate ownership and avoid collisions.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: “Report Found Animal” button is visible on announcements list and reliably starts the found-pet flow.
- **SC-002**: Existing Missing Pet flow remains unchanged from user perspective (manual smoke test confirms same behavior/screens).
- **SC-003**: After successful completion of the Found flow, the announcements list refresh is triggered (observable via refreshed data load).

## Assumptions

- This iteration is **scaffolding only**: it prepares a Found flow entry point and code structure, but does not implement the final Found Pet reporting UX defined in `069-report-found-pet`.
- The Found flow is intentionally a **copy** of the Missing flow for now; future work will diverge the flows.
- Copying and renaming types will not require backend/API changes in this iteration (Found flow submission reuses existing Missing submission logic temporarily).

## Dependencies

- Existing `ReportMissingAndFoundPet/ReportMissingPet` implementation is the source-of-truth for the temporary Found flow copy.
- Announcements list screen already supports a “Report Missing Animal” entry point and has (at least partially) prepared wiring for a Found flow entry.

## Design Deliverables *(mandatory for UI features)*

N/A for this iteration. This change is explicitly a temporary copy of an existing UI flow; no new UI design is introduced besides an additional entry button matching existing patterns.

---

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 1
- **Initial Budget**: 1 × 4 × 1.3 = 5.2 days
- **Confidence**: ±50%
- **Anchor Comparison**: Simpler than Pet Details (3 SP) because this is iOS-only scaffolding: copy + rename + wiring an entry point, with no new UX and no backend/API work.

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 1 | 5.2 | ±50% | Gut feel from feature title - iOS-only scaffolding |
| After SPEC | — | — | ±30% | [Update when spec.md complete] |
| After PLAN | — | — | ±20% | [Update when plan.md complete] |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | No backend changes |
| iOS | — | — | [Fill after tasks.md] |
| Android | 0 | 0 | No Android changes |
| Web | 0 | 0 | No web changes |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | 1 SP | [Y SP] | [Calculate: (Y - 1) / 1 × 100%] |
| **Budget (days)** | 5.2 days | [Y days] | [Calculate: (Y - 5.2) / 5.2 × 100%] |

**Variance Reasons**: [Why was estimate different? Unexpected rename fallout? Additional wiring needed?]  
**Learning for Future Estimates**: [What pattern should the team apply to similar scaffolding tasks?]


