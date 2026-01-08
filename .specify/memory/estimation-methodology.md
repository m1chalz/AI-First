# Estimation Methodology

**Version**: 1.0  
**Created**: 2025-12-18  
**Context**: Multi-platform project (Backend + Web + iOS + Android) using spec-kit framework

---

## Overview

This project uses a two-phase estimation approach:

1. **Initial Estimation**: Story Points for budgeting (high uncertainty, ±50%)
2. **Spec-Kit Re-Estimation**: Refined per-platform estimates after SPEC, PLAN, TASKS phases (±10-15%)

---

## Story Point Definition (Multi-Platform)

> **1 SP = Effort to implement a well-understood, simple feature across ALL platforms (Backend + Web + iOS + Android), including spec-kit documentation and 80% test coverage.**

### Reference Scale (Fibonacci ONLY)

> **IMPORTANT**: Only use Fibonacci values: **1, 2, 3, 5, 8, 13**. No 4, 6, 7, 9, 10, 11, 12!

| SP | Complexity | Example |
|----|------------|---------|
| 1 | Trivial | Add field to existing screen |
| 2 | Simple | Static content page, client-side PDF |
| **3** | **Medium (ANCHOR)** | **Pet details screen** |
| 5 | Complex | List with filters + pagination |
| 8 | Very Complex | Social sharing, file uploads |
| 13 | Epic-sized | Push notifications with location |

### Budget Formula

```
Budget = SP × 4 days × 1.3 (risk buffer)
Example: 8 SP × 4 × 1.3 = ~42 days
```

---

## Spec-Kit Re-Estimation Process

```
Initial SP (all platforms) → SPEC → PLAN → TASKS → Per-Platform Days
        ±50%                  ±30%   ±20%   ±10-15%
```

### What Each Phase Reveals

| Phase | Key Questions | Typical Discovery |
|-------|--------------|-------------------|
| **SPEC** | How many user stories? In/out of scope? | Scope clarity |
| **PLAN** | Does backend already support this? Can we reuse components? | Architecture reuse |
| **TASKS** | How many tasks per platform? Parallel opportunities? | Concrete effort |

---

## Estimation Fields (Required in spec.md)

Every `spec.md` MUST include an Estimation section after the Scope section:

```markdown
## Estimation

### Initial Estimate
- **Story Points**: [1-13 Fibonacci scale]
- **Initial Budget**: [SP × 4 days × 1.3] days
- **Confidence**: ±50%
- **Anchor Comparison**: [How does this compare to Pet Details (3 SP)?]

### Re-Estimation (Updated After Each Phase)
| Phase | Estimate | Confidence | Key Discovery |
|-------|----------|------------|---------------|
| After SPEC | [days] | ±30% | [Scope insight] |
| After PLAN | [days] | ±20% | [Reuse opportunity] |
| After TASKS | [days] | ±15% | [Task-based calculation] |

### Per-Platform Breakdown (After TASKS)
| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | [n] | [days] | [key insight] |
| iOS | [n] | [days] | [key insight] |
| Android | [n] | [days] | [key insight] |
| Web | [n] | [days] | [key insight] |
| **Total** | | **[days]** | |

### Variance Tracking
- **Initial Budget**: [days]
- **Final Estimate**: [days]
- **Variance**: [(Final - Initial) / Initial × 100]%
- **Variance Reason**: [Why was initial estimate off?]
```

---

## Task-to-Days Conversion

```
Platform Days = Task Count × 1.5 hours / 8 hours
```

Example: 30 tasks × 1.5 = 45 hours = ~6 days

---

## Testing Effort Estimation

Testing effort is included in Story Points but should be explicitly tracked for learning and accuracy.

### Testing Effort Guidelines

| Feature Complexity | Test Tasks % | Test Days Ratio | Example |
|--------------------|--------------|-----------------|---------|
| **Simple** (1-2 SP) | 20-30% | 0.2-0.3× implementation | 1 SP feature: 3 days impl + 1 day tests = 4 days |
| **Medium** (3-5 SP) | 30-40% | 0.3-0.4× implementation | 3 SP feature: 9 days impl + 3.5 days tests = 12.5 days |
| **Complex** (8-13 SP) | 40-50% | 0.4-0.5× implementation | 8 SP feature: 20 days impl + 10 days tests = 30 days |

### Test Type Breakdown

Typical test effort distribution:

- **Unit Tests**: 50-60% of test time (most coverage, fast feedback)
- **Integration Tests**: 15-20% of test time (backend API endpoints)
- **E2E Tests**: 25-30% of test time (user flows, Page Objects, stability fixes)

### Testing Effort Formula

```
Test Days = Implementation Days × Test Ratio
Total Days = Implementation Days + Test Days
```

**Example** (3 SP feature):
```
Implementation: 9 days (70%)
Testing: 3.5 days (30%) = 9 × 0.35
Total: 12.5 days ≈ 13 days
```

### Variance Tracking for Testing

When re-estimating after PLAN/TASKS, track testing variance separately:

| Metric | Initial | Final | Variance | Reason |
|--------|---------|-------|----------|--------|
| Implementation Days | [X] | [Y] | [(Y-X)/X × 100%] | [Reuse, native APIs, etc.] |
| Testing Days | [X] | [Y] | [(Y-X)/X × 100%] | [Test complexity, E2E flakiness, etc.] |
| **Total Days** | **[X]** | **[Y]** | **[(Y-X)/X × 100%]** | |

**Common Testing Variance Patterns**:
- **-30% to -50%**: Backend API already works, minimal testing needed
- **+30% to +50%**: E2E tests flaky, required significant stabilization effort
- **+50% to +100%**: Complex test scenarios, edge cases not anticipated

### Integration with Spec-Kit Phases

Testing effort should be estimated and tracked at each phase:

| Phase | Testing Estimation Activity |
|-------|----------------------------|
| **SPEC** | Identify test types needed (unit, integration, E2E) |
| **PLAN** | Document test strategy decisions, estimate test task count |
| **TASKS** | Create concrete test tasks per platform, calculate test percentage |
| **IMPLEMENTATION** | Track actual testing time vs estimate, document variance |

**Reference**: See `docs/testing-spec-kit-integration.md` for detailed integration guide.

---

## Common Estimation Patterns

### Pattern 1: Reuse Discovery (-60% to -80%)
- Initial assumption: Building from scratch
- Reality: Can extend existing infrastructure
- Example: Report Found Pet reusing Report Missing Pet flow

### Pattern 2: Native vs SDK (-50% to -70%)
- Initial assumption: Need external SDKs
- Reality: Native platform APIs are sufficient
- Example: Social Share using native share sheets

### Pattern 3: Backend Already Works (-80% to -95%)
- Initial assumption: New backend development needed
- Reality: Existing API already supports the feature
- Example: FOUND status already supported in announcements API

---

## Estimation Checklist

### Initial Estimation (Before SPEC)
- [ ] Compare to anchor (Pet Details = 3 SP)
- [ ] Apply complexity factors
- [ ] Use Fibonacci scale (1,2,3,5,8,13)
- [ ] Calculate budget: SP × 4 days × 1.3
- [ ] Record with ±50% confidence

### After SPEC
- [ ] Count user stories
- [ ] Identify scope boundaries
- [ ] Update estimate (±30%)

### After PLAN
- [ ] Check: Does backend already support this?
- [ ] Check: Can we reuse components?
- [ ] Check: Native APIs or SDKs needed?
- [ ] Update estimate (±20%)

### After TASKS
- [ ] Count tasks per platform
- [ ] Calculate days: tasks × 1.5h / 8h
- [ ] Update estimate (±10-15%)
- [ ] Calculate variance from initial
- [ ] Document variance reason for team learning

---

## Jira Integration

Every feature specification MUST have a corresponding Jira ticket of type **Feature** in the "AI First" (KAN) project.

### Automatic Ticket Creation

When a `spec.md` file is created or updated with an estimation, a Jira Feature ticket is automatically created/updated with:

| spec.md Field | Jira Field | Notes |
|---------------|------------|-------|
| Feature title | Summary | From first heading |
| User stories + Requirements | Description | Markdown content |
| Story Points (Initial) | Story point estimate | From Initial Estimate section |
| Feature branch name | Labels | e.g., `062-printable-flyer` |

### Workflow

```
1. Create spec.md with estimation
2. Run: specify sync-jira (or automatic on spec.md save)
3. Jira ticket created with Story Points
4. FigJam user flow diagram auto-generated
5. After PLAN/TASKS phases → update spec.md → Story Points updated in Jira
```

---

## Design Deliverables (Mandatory for UI Features)

Every feature specification with UI components gets a complete design package.

### Design Workflow

```
1. Create spec.md with user stories
2. Generate User Flow diagram (FigJam)
3. Generate Wireframe layout (FigJam)
4. Create Design Brief (markdown)
5. Create Figma Make Prompt
6. Link all assets to Jira ticket
7. Generate Visual Mockups (Figma Make or designer)
```

### Design Assets Table

| Asset | Format | Purpose | Auto-Generated? |
|-------|--------|---------|-----------------|
| **User Flow** | FigJam flowchart | Navigation and logic | ✅ Yes |
| **Wireframe** | FigJam layout | Screen structure | ✅ Yes |
| **Design Brief** | Markdown | Component specs, interactions | ✅ Yes |
| **Figma Make Prompt** | Markdown | AI prompt for visual design | ✅ Yes |
| **Visual Mockups** | Figma design | High-fidelity screens | ❌ Manual (Figma Make) |

### File Structure

```
specs/[feature]/
├── spec.md
├── plan.md
├── tasks.md
└── design/
    ├── design-brief.md      # Component specs, interactions
    └── figma-make-prompt.md # Ready-to-paste AI prompt
```

### spec.md Design Field Format

```markdown
**Design**: [User Flow](url) | [Wireframe](url) | [Design Brief](design/design-brief.md) | [Figma Make Prompt](design/figma-make-prompt.md)
```

### Jira Integration

All design assets are added to the Jira ticket as comments:

1. **Comment 1**: User Flow diagram link
2. **Comment 2**: Wireframe + ASCII layout
3. **Comment 3**: Figma Make prompt (copy-paste ready)
4. **Comment 4**: Visual mockup links (when available)

### Figma Make Prompt Template

Every UI feature gets a Figma Make prompt that references the existing prototype.

**Existing Prototype (REQUIRED REFERENCE)**:
🔗 [PetSpot Lost Pet Reporting System](https://www.figma.com/make/ZbqpULNnXH99lsfgscxBcK/Lost-Pet-Reporting-System)

Every Figma Make prompt MUST include:

```
Create a [screen type] for [app name].

SCREEN LAYOUT:
1. [Header/navigation elements]
2. [Main content area]
3. [Action buttons/CTAs]
4. [Secondary elements]

COMPONENTS:
- [Component 1 with specs]
- [Component 2 with specs]

STYLE:
- [Design system/brand guidelines]
- [Platform patterns (iOS/Android/Web)]

Create [N] screens: [list of variations]

DESIGN SYSTEM REFERENCE:
Use the existing PetSpot prototype as the design foundation:
https://www.figma.com/make/ZbqpULNnXH99lsfgscxBcK/Lost-Pet-Reporting-System

Match the existing design system:
- Colors: Use the same color palette from the prototype
- Typography: Match fonts and type scale
- Components: Reuse existing card, button, and navigation patterns
- Spacing: Follow the same spacing tokens
- Style: Maintain visual consistency with existing screens
```

### When to Skip Design Deliverables

Skip design for:
- Backend-only features (API endpoints, database migrations)
- Infrastructure features (CI/CD, deployment)
- Refactoring tasks (no UI changes)

Mark in spec.md: `**Design**: N/A - Backend only`

### spec.md Required Fields for Jira

Add this to the frontmatter or metadata section of spec.md:

```markdown
**Jira Ticket**: [KAN-XXX](https://ai-first-intive.atlassian.net/browse/KAN-XXX)
```

This field is automatically populated after the first Jira sync.

### Jira Configuration

| Setting | Value |
|---------|-------|
| Jira Cloud ID | `2b980644-05e9-43e0-aab6-1bcf1e6cb9de` |
| Jira Project Key | `KAN` |
| Issue Type | `Feature` (ID: 10003) |
| Story Points Field | `customfield_10016` |
