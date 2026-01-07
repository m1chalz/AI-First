# How We Chose the Anchor

## The Anchor Reference

In our methodology, we use:

> **Pet Details Screen = 3 SP (Medium Complexity)**

## Why This Anchor Works

| Criteria | Pet Details Screen |
|----------|-------------------|
| Well-Understood | Already implemented on all platforms (iOS, Android, Web) |
| Medium Complexity | Not trivial, not epic - good middle ground |
| Representative | Includes common patterns: API call, data display, navigation |
| Team Familiarity | Everyone knows what it involves |

## What Pet Details Screen Includes (3 SP)

Based on the existing implementation in the codebase:

- **Backend:** Single `GET /api/v1/announcements/:id` endpoint
- **iOS:** `PetDetailsView` + `PetDetailsViewModel` + `PetDetailsCoordinator`
- **Android:** `PetDetailsScreen` + `PetDetailsViewModel` + MVI pattern
- **Web:** Pet details page with routing
- **Tests:** Unit tests for ViewModels, 80% coverage
- **No external integrations, no complex permissions**

## How to Use the Anchor for Estimation

### Relative Comparison

When estimating a new feature, compare it to the anchor:

| Question | If Yes | If No |
|----------|--------|-------|
| Is it simpler than Pet Details? | < 3 SP (1-2) | ≥ 3 SP |
| Does it have similar complexity? | = 3 SP | Adjust up/down |
| Does it add external integrations? | +2-3 SP | — |
| Does it require permissions? | +1-2 SP | — |
| Does it have complex UI? | +1-2 SP | — |

### Examples from Our Session

| Feature | Comparison to Anchor (3 SP) | Initial SP |
|---------|----------------------------|------------|
| Report Found Pet | Same screens as Pet Details but adds: photo upload, location, permissions, form submission | 13 SP (~4x anchor) |
| Social Share | Adds: external share APIs, Open Graph meta tags, platform-specific sharing | 8 SP (~2.5x anchor) |

## Establishing Your Own Anchor

If you don't have Pet Details Screen, choose an anchor by:

1. Pick a completed feature the team understands well
2. Ensure it's medium complexity (not too simple, not too complex)
3. Assign it **3 SP** as the baseline
4. Document what it includes (API calls, screens, tests, integrations)
5. Use it consistently for all future estimates

### Anchor Documentation Template

```markdown
## Team Anchor: [Feature Name] = 3 SP

### What It Includes:
- Backend: [describe API work]
- iOS: [describe iOS work]
- Android: [describe Android work]
- Web: [describe Web work]
- Tests: [describe test coverage]

### What It Does NOT Include:
- External integrations
- Complex permissions
- Offline support
- Real-time features
```

## Key Point

The anchor came from the project's workspace rules (`.specify/memory/constitution.md`) which stated:

> **Anchor: Pet Details Screen = 3 SP (medium complexity)**

This was pre-established by the team to ensure consistent estimation across features. The anchor should be **calibrated periodically** based on actual completion times to improve estimation accuracy.

