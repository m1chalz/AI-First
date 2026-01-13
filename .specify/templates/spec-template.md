# Feature Specification: [FEATURE NAME]

**Feature Branch**: `[###-feature-name]`  
**Created**: [DATE]  
**Status**: Draft  
**Jira Ticket**: _Pending sync_ <!-- Auto-populated after spec creation -->  
**Design**: _Pending_ <!-- See Design Deliverables section below -->  
**Input**: User description: "$ARGUMENTS"

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - [Brief Title] (Priority: P1)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently - e.g., "Can be fully tested by [specific action] and delivers [specific value]"]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]
2. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 2 - [Brief Title] (Priority: P2)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 3 - [Brief Title] (Priority: P3)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

[Add more user stories as needed, each with an assigned priority]

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- What happens when [boundary condition]?
- How does system handle [error scenario]?

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: System MUST [specific capability, e.g., "allow users to create accounts"]
- **FR-002**: System MUST [specific capability, e.g., "validate email addresses"]  
- **FR-003**: Users MUST be able to [key interaction, e.g., "reset their password"]
- **FR-004**: System MUST [data requirement, e.g., "persist user preferences"]
- **FR-005**: System MUST [behavior, e.g., "log all security events"]

*Example of marking unclear requirements:*

- **FR-006**: System MUST authenticate users via [NEEDS CLARIFICATION: auth method not specified - email/password, SSO, OAuth?]
- **FR-007**: System MUST retain user data for [NEEDS CLARIFICATION: retention period not specified]

### Key Entities *(include if feature involves data)*

- **[Entity 1]**: [What it represents, key attributes without implementation]
- **[Entity 2]**: [What it represents, relationships to other entities]

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: [Measurable metric, e.g., "Users can complete account creation in under 2 minutes"]
- **SC-002**: [Measurable metric, e.g., "System handles 1000 concurrent users without degradation"]
- **SC-003**: [User satisfaction metric, e.g., "90% of users successfully complete primary task on first attempt"]
- **SC-004**: [Business metric, e.g., "Reduce support tickets related to [X] by 50%"]

## Test Identifiers *(mandatory for UI features)*

<!--
  TEST IDENTIFIERS: All interactive UI elements MUST have stable test identifiers for E2E testing.
  See docs/testing-workflow.md for complete guide.
  
  For backend-only features, mark this section as N/A.
-->

### Naming Convention

Format: `{screen}.{element}.{action-or-id}`

**Examples**:
- Buttons/Inputs: `petDetails.shareButton.click`, `login.emailInput.field`
- List Items: `petList.item.${petId}` (use stable IDs, NEVER array indices)
- Actions: `click`, `field`, `toggle`, `submit` (descriptive of interaction type)

### Test Identifiers for This Feature

| Screen | Element | Test Identifier | Platform Notes |
|--------|---------|----------------|----------------|
| [Screen Name] | [Button/Input/Link] | `[screen].[element].[action]` | Android: testTag, iOS: accessibilityIdentifier, Web: data-testid |

**Example**:
| Pet Details | Share Button | `petDetails.shareButton.click` | All platforms |
| Pet List | List Item | `petList.item.${petId}` | Dynamic ID based on pet |

### Test Identifier Checklist

- [ ] All buttons have test identifiers
- [ ] All input fields have test identifiers
- [ ] All navigation elements have test identifiers
- [ ] List items use stable IDs (not indices)
- [ ] Naming follows `{screen}.{element}.{action}` convention

## Testing Strategy *(optional - use for complex features)*

<!--
  TESTING STRATEGY: For complex features (5+ SP), document which test types apply.
  See docs/testing-workflow.md for test decision tree.
  
  For simple features (1-2 SP), this section is optional.
-->

### Test Types Required

| Test Type | Required? | Rationale |
|-----------|-----------|-----------|
| **Unit Tests** | ✅ Yes | Business logic in ViewModels, services, use cases |
| **Integration Tests** | ✅ Yes | Backend API endpoints (all platforms consume REST API) |
| **E2E Tests** | ✅ Yes | Critical user flow across platforms |
| **Manual Tests** | ⚠️ Optional | [Specify if needed, e.g., social platform preview validation] |

### Coverage Targets

- **Backend**: 80% (services, lib, API endpoints) - TDD mandatory
- **Web**: 80% (hooks, lib functions) - TDD recommended, 80% coverage mandatory
- **Android**: 80% (ViewModels, use cases, domain models)
- **iOS**: 80% (ViewModels, domain models)

### Test Complexity Indicators

- **Simple** (1-2 SP): Standard unit + integration + E2E tests
- **Medium** (3-5 SP): Add edge case tests, error scenario tests
- **Complex** (8+ SP): Add integration with external systems, performance tests (if needed), security tests (if auth-related)

## Design Deliverables *(mandatory for UI features)*

<!--
  DESIGN WORKFLOW: Auto-generated after spec.md creation.
  See `.specify/memory/estimation-methodology.md` for full workflow.
  
  All design assets are:
  1. Generated automatically where possible
  2. Linked to Jira ticket
  3. Stored in specs/[feature]/design/ folder
-->

### Design Assets

| Asset | Status | Link |
|-------|--------|------|
| **User Flow** | _Pending_ | FigJam diagram showing navigation and logic |
| **Wireframe** | _Pending_ | FigJam layout showing screen structure |
| **Design Brief** | _Pending_ | Component specs, interactions, platform notes |
| **Figma Make Prompt** | _Pending_ | AI prompt for generating visual designs |
| **Visual Mockups** | _Pending_ | High-fidelity screens (created in Figma Make or by designer) |

### Design Requirements

- [ ] User flow diagram created
- [ ] Wireframe layout created
- [ ] Design brief with component specs
- [ ] Figma Make prompt ready
- [ ] All assets linked in Jira ticket
- [ ] Visual mockups approved (if applicable)

---


## Estimation *(mandatory)*

<!--
  ESTIMATION METHODOLOGY: See `.specify/memory/estimation-methodology.md` for full details.
  
  Story Point Definition: 1 SP = Effort to implement across ALL platforms with 80% test coverage.
  Anchor: Pet Details Screen = 3 SP (medium complexity)
  Budget Formula: SP × 4 days × 1.3 (risk buffer)
-->

### Initial Estimate

- **Story Points**: [1|2|3|5|8|13 - Fibonacci ONLY, no other values!]
- **Initial Budget**: [SP × 4 × 1.3 = X days]
- **Confidence**: ±50%
- **Anchor Comparison**: [Compare to Pet Details (3 SP) - simpler/similar/more complex because...]

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | [from above] | [from above] | ±50% | Gut feel from feature title |
| After SPEC | — | — | ±30% | [Update when spec.md complete] |
| After PLAN | — | — | ±20% | [Update when plan.md complete] |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | — | — | [Fill after tasks.md] |
| iOS | — | — | [Fill after tasks.md] |
| Android | — | — | [Fill after tasks.md] |
| Web | — | — | [Fill after tasks.md] |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | [X SP] | [Y SP] | [Calculate: (Y - X) / X × 100%] |
| **Budget (days)** | [X days] | [Y days] | [Calculate: (Y - X) / X × 100%] |

**Variance Reasons**: [Why was estimate different? Reuse? Native APIs? Backend already done?]

**Learning for Future Estimates**: [What pattern should the team apply to similar features?]