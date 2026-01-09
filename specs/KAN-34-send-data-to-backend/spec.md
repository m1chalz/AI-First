# Feature Specification: iOS Send report data to backend (status by flow)

**Feature Branch**: `KAN-34-send-data-to-backend`  
**Created**: 2026-01-09  
**Status**: Draft  
**Platform**: iOS  
**Jira Ticket**: KAN-34  
**Input**: User description: "utwórz specyfikację tylko dla iOS o nazwie KAN-34-send-data-to-backend. Nie numeruj specki jak masz w skrypcie, nazwa dokładnie taka jak napisałem. Branch o takiej samej nazwie. W tej specyfikacji zrobimy wysyłanie danych na backend. Obecnie przy wysyłaniu zgłoszenia mamy zahardcodowany status na .active. Musimy mieć to pole modyfikowalne i w missing flow ustawić na .active a w found na .found. W found flow wysyłamy dane jakie mamy w state i backend je akceptuje, sprawdź czy nie jest tak, że coś mamy optional a backend wymaga, jak mamy jakieś dane których nie wysyłamy do backendu to spoko, nie jest to problemem"

## Clarifications

### Session 2026-01-09

- Q: Where should the status decision live in iOS (higher layer) so Found vs Missing sends correct status? → A: Add `status` field to `CreateAnnouncementData`.
- Q: Should iOS require at least one contact method, or require both email and phone? → A: Require both email and phone.
- Q: Should submission be blocked when coordinates are missing? → A: Yes; user cannot reach submit without coordinates, so keep current implementation behavior.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Submit a Missing report to backend (Priority: P1)

As a user reporting a missing pet, I want my report to be submitted to the backend with the correct status so it appears as a missing announcement in the system.

**Why this priority**: Missing reports are a core business flow; incorrect status breaks listing, filters, and user expectations.

**Independent Test**: Complete the Missing flow and verify the created announcement is stored as “missing” in backend status terms.

**Acceptance Scenarios**:

1. **Given** the user completes the Missing flow with valid data, **When** they submit the report, **Then** the backend accepts the request and stores the report with status representing “missing”.
2. **Given** the user completes the Missing flow, **When** they later view the announcement details, **Then** the status label is shown as missing (per existing iOS presentation rules).

---

### User Story 2 - Submit a Found report to backend (Priority: P1)

As a user reporting a found pet, I want my report to be submitted to the backend with the correct status so it appears as a found announcement in the system.

**Why this priority**: Found reports must be distinguishable from missing reports; incorrect status breaks map/list legend and user trust.

**Independent Test**: Complete the Found flow and verify the created announcement is stored as “found” in backend status terms.

**Acceptance Scenarios**:

1. **Given** the user completes the Found flow with valid data, **When** they submit the report, **Then** the backend accepts the request and stores the report with status representing “found”.
2. **Given** the user completes the Found flow with valid data, **When** they submit, **Then** only fields supported by the backend are included in the request (no extra iOS-only fields).
3. **Given** optional fields are empty on iOS, **When** submitting, **Then** iOS does not send invalid “empty” values that would be rejected by backend validation rules.
4. **Given** the user submits a report, **When** the request payload is validated by the backend, **Then** it contains only recognized fields and passes validation.

---

### Edge Cases

- The submission payload includes an unknown field: backend rejects it; iOS must not send unknown fields.
- The submission payload omits required fields (e.g., species, sex, last-seen date, coordinates, status): backend rejects it; iOS must prevent submission until required data is present.
- Coordinates missing: user cannot reach the submit action without valid coordinates, so this case is prevented by the flow UI/validation.
- Contact details missing: iOS requires both email and phone; submission is blocked until both are provided.
- Optional values present but malformed (e.g., non-digit microchip): backend rejects; iOS must enforce correct formats before submission.

## Requirements *(mandatory)*

### Functional Requirements

#### Status handling (core)

- **FR-001**: The iOS app MUST include a status field in the backend submission payload.
- **FR-002**: The status MUST be configurable by the reporting flow (not hardcoded).
- **FR-003**: When the user submits a report from the Missing flow, the status MUST represent “missing” (iOS domain: `.active`).
- **FR-004**: When the user submits a report from the Found flow, the status MUST represent “found” (iOS domain: `.found`).
- **FR-004a**: The iOS Domain model `CreateAnnouncementData` MUST include a `status` field so the reporting flow can set it before mapping to DTO/payload.

#### Payload compatibility with backend validation

- **FR-005**: The iOS app MUST send only fields supported by the backend schema; sending extra (unknown) fields MUST NOT happen.
- **FR-006**: The iOS app MUST include all backend-required fields in the submission payload:
  - Species
  - Sex
  - Last-seen date (in the backend-accepted date format)
  - Location coordinates (latitude and longitude, within valid ranges)
  - Status (representing missing or found as appropriate)
- **FR-007**: The iOS app MUST treat the following fields as optional in the submission payload (send only when present and valid):
  - Pet name
  - Breed
  - Age
  - Description
  - Microchip number (digits-only)
  - Reward
- **FR-008**: The iOS app MUST require both contact methods before submission:
  - Email MUST be provided
  - Phone MUST be provided
- **FR-009**: The iOS app MUST NOT send invalid “empty” values for fields that are not provided (e.g., empty strings) that would fail backend validation.

#### Found flow: “state vs payload”

- **FR-010**: In the Found flow, the iOS app MUST build the submission payload from the current Found flow state at the time of submission.
- **FR-011**: If Found flow collects iOS-only fields that are not supported by the backend schema, the iOS app MUST keep them in iOS state/UI only and MUST NOT include them in the backend submission payload.

### Key Entities *(include if feature involves data)*

- **Report submission payload**: The set of fields sent from iOS to backend to create a report. It must match backend-recognized fields only.
- **Announcement status**: A classification for announcements used across the system. For submission, it must represent either “missing” or “found” depending on the flow.

### Assumptions

- The backend supports exactly two submission statuses for report creation: missing and found.
- Backend validation rejects unknown fields in the payload (strict schema).
- Missing and Found flows already collect the required fields (species, sex, date, coordinates) before submission.

### Dependencies

- Existing iOS Missing and Found report flows and their state management.
- Backend validation rules for announcement creation (required fields, accepted formats, and strict payload shape).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In QA, 100% of Missing submissions result in announcements stored as missing (status is correct for Missing flow).
- **SC-002**: In QA, 100% of Found submissions result in announcements stored as found (status is correct for Found flow).
- **SC-003**: In QA, 0 submissions fail due to backend rejection caused by unknown payload fields.
- **SC-004**: In QA, 0 submissions fail due to missing required fields in the payload when the user has completed the flow screens.

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 1
- **Initial Budget**: 1 × 4 × 1.3 = 5.2 days
- **Confidence**: ±50%
- **Anchor Comparison**: Much smaller than Pet Details (3 SP) because this is iOS-only scope focused on request payload correctness and status selection by flow.

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 1 | 5.2 | ±50% | Gut feel from feature title - iOS-only request payload adjustment |
| After SPEC | 1 | 5.2 | ±30% | Status must depend on flow; backend schema is strict and rejects unknown fields |
| After PLAN | 1 | 5.2 | ±20% | 6 source files + 2 test files; existing mapper pattern; no backend changes |
| **After TASKS** | **1** | **5.2** | **±15%** | **14 tasks: 7 code changes + 5 test tasks + 2 polish; minimal effort** |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | Not in scope (iOS-only spec) |
| **iOS** | **14** | **~1.5** | Status property in protocol + 2 impl, domain model, service, mapper + 6 tests |
| Android | — | — | Not in scope |
| Web | — | — | Not in scope |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | 1 SP | [Y SP] | [Calculate: (Y - 1) / 1 × 100%] |
| **Budget (days)** | 5.2 days | [Y days] | [Calculate: (Y - 5.2) / 5.2 × 100%] |

**Variance Reasons**: [Update after delivery]  
**Learning for Future Estimates**: [Update after delivery]


