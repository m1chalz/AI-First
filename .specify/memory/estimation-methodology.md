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

