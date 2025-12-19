# Research: iOS Landing Page Full Content Scroll

**Feature**: KAN-30 iOS Landing Page Full Content Scroll  
**Date**: 2025-12-19  
**Status**: Complete

## Overview

This document captures technical research and architectural decisions for replacing the nested scroll architecture in iOS landing page with a single outer ScrollView to enable continuous scrolling of all page sections.

## Decision 1: Single Outer ScrollView Architecture

**Context**: Current landing page has nested ScrollView inside `AnnouncementCardsListView`, causing only the list to scroll while hero panel and list header remain static.

**Decision**: Wrap entire landing page content in single outer ScrollView with LazyVStack containing all sections (hero, header, list content).

**Rationale**:
- Eliminates competing/nested scroll regions (Apple HIG discourages nested scrolling)
- Improves accessibility on smaller screens (all content reachable via single scroll gesture)
- Follows iOS best practices (single scroll container per screen)
- Maintains smooth 60fps scrolling with LazyVStack (lazy rendering of off-screen items)
- Preserves existing MVVM-C architecture (no ViewModel or coordinator changes)

**Alternatives Considered**:
1. **Keep nested scroll architecture** (REJECTED)
   - Violates FR-001 (no nested scroll regions)
   - Poor UX on smaller screens (content trapped in subsection)
   - Accessibility issues (confusing scroll handoff)

2. **Custom UIScrollView coordinator** (REJECTED)
   - Over-engineered for simple scroll behavior change
   - Adds unnecessary UIKit bridging complexity
   - SwiftUI native ScrollView sufficient for requirement

3. **ScrollViewReader with programmatic scrolling** (REJECTED)
   - Not needed - no requirement for programmatic scroll control
   - Adds unnecessary state management
   - Simple declarative ScrollView meets all requirements

## Decision 2: Conditional ScrollView in AnnouncementCardsListView

**Context**: `AnnouncementCardsListView` currently wraps list content in its own ScrollView (line 53), making it an autonomous scrollable component. This component is **reused** in multiple contexts:
- `LandingPageView` - embedded in parent ScrollView (needs NO own scroll)
- `AnnouncementListView` - standalone full-screen list (needs own scroll)

**Decision**: Add `hasOwnScrollView: Bool = true` parameter to conditionally wrap content in ScrollView.

**Rationale**:
- **Backwards compatible**: Default `true` preserves existing behavior for `AnnouncementListView`
- **Explicit control**: Parent view explicitly chooses scroll behavior
- **Reusability maintained**: Component works in both contexts
- **Pragmatic approach**: Small API change vs. major refactoring

**Implementation**:
```swift
struct AnnouncementCardsListView: View {
    // ... existing properties ...
    
    /// Whether this view manages its own scroll. Set to `false` when embedded in parent ScrollView.
    let hasOwnScrollView: Bool
    
    init(
        viewModel: AnnouncementCardsListViewModel,
        emptyStateModel: EmptyStateView.Model,
        listAccessibilityId: String,
        hasOwnScrollView: Bool = true  // Default: true (backwards compatible)
    ) {
        // ...
    }
    
    var body: some View {
        // ...
        if hasOwnScrollView {
            ScrollView { listContent }
        } else {
            listContent  // Parent provides scroll container
        }
    }
}
```

**Trade-off Accepted (FR-001 Violation)**:
- FR-001 states "no nested scroll regions" - with `hasOwnScrollView: true` in `AnnouncementListView`, technically nested scrolling is still possible if someone wraps it in another ScrollView
- **Justification**: Reusability of `AnnouncementCardsListView` across multiple screens is more valuable than strict FR-001 compliance. The parameter gives explicit control to prevent nested scrolling where it matters (LandingPageView).

**Alternatives Considered**:
1. **Remove ScrollView entirely, always use parent scroll** (REJECTED)
   - Breaks `AnnouncementListView` which uses component standalone
   - Would require refactoring all usages
   
2. **Create two separate components** (REJECTED)
   - `AnnouncementCardsListView` (with scroll) and `AnnouncementCardsListContent` (without)
   - Code duplication, maintenance burden
   - Parameter approach is cleaner

3. **Environment variable for scroll context** (REJECTED)
   - Implicit behavior (harder to understand)
   - Explicit parameter is clearer and more testable

## Decision 3: LazyVStack for Performance

**Context**: Landing page composes multiple sections (hero, header, list) that could all be visible simultaneously.

**Decision**: Use LazyVStack in outer ScrollView for vertical layout.

**Rationale**:
- Lazy rendering for announcement list items (only visible items rendered)
- Efficient memory usage for long lists (important for landing page with 5+ items)
- Native SwiftUI performance optimizations (view recycling)
- Hero and header always visible (small overhead), list items lazy-loaded

**Alternatives Considered**:
1. **Plain VStack** (REJECTED for list items)
   - Renders all list items immediately (performance issue with 10+ items)
   - No view recycling (memory waste)
   - Acceptable for hero/header (always visible), not for list

2. **List instead of LazyVStack** (REJECTED)
   - List enforces its own styling (separator lines, inset grouped appearance)
   - Less design flexibility (cards need custom spacing, padding, background)
   - LazyVStack provides more control over layout

## Decision 4: Preserve .frame(maxHeight: .infinity) Removal

**Context**: Current LandingPageView sets `.frame(maxHeight: .infinity)` on AnnouncementCardsListView (line 57) to fill remaining VStack space.

**Decision**: Remove this frame modifier when transitioning to outer ScrollView architecture.

**Rationale**:
- ScrollView content sizing is intrinsic (content height determines scroll area)
- `.frame(maxHeight: .infinity)` conflicts with ScrollView content sizing
- Each section (hero, header, list) contributes natural height to scroll content

**Alternatives Considered**:
1. **Keep frame modifier** (REJECTED)
   - Causes layout conflicts in ScrollView (tries to expand infinitely inside scroll content)
   - ScrollView content should have fixed/intrinsic height

## Decision 5: Testing Strategy

**Context**: Need to verify scroll behavior change without introducing regressions.

**Decision**: 
- **Unit tests**: Existing LandingPageViewModelTests unchanged (no ViewModel logic changes)
- **E2E tests**: New Gherkin scenarios verify continuous scroll behavior (Java + Cucumber)

**Rationale**:
- UI refactoring doesn't change ViewModel logic (same @Published properties, same coordinator callbacks)
- E2E tests verify user-facing scroll behavior (User Story 1 & 2 acceptance criteria)
- XCTest UI testing not needed (Appium E2E coverage sufficient)

**E2E Test Scenarios**:
1. Continuous scroll from top to bottom (verify all sections reachable)
2. Scroll position preservation during state changes (loading → success)
3. Interactive element taps during/after scrolling (verify no tap conflicts)

**Alternatives Considered**:
1. **XCTest UI tests in iosAppTests** (REJECTED)
   - Appium E2E tests already provide scroll verification
   - Avoids duplication (both verify same scroll behavior)
   - Appium tests run in real-world environment (more valuable)

2. **Snapshot tests** (REJECTED)
   - Scroll behavior is interactive (snapshots don't verify scrolling)
   - E2E tests more appropriate for scroll verification

## SwiftUI Best Practices Applied

**ScrollView Performance**:
- Use LazyVStack (not VStack) for list items (lazy rendering)
- Fixed/intrinsic content sizing (no infinite frames inside scroll content)
- Avoid nested ScrollViews (iOS handles nested scrolling poorly)

**Accessibility**:
- Single scroll gesture reaches all content (improves VoiceOver experience)
- No competing scroll regions (reduces cognitive load)
- Existing accessibility identifiers preserved (no changes needed)

**State Management**:
- Loading/error/empty states render inline in scroll content (FR-006)
- No full-screen overlays that detach hero from scrollable content
- ZStack approach in AnnouncementCardsListView preserves state rendering

**MVVM-C Architecture**:
- View layer change only (LandingPageView structure modified)
- ViewModel unchanged (same @Published properties, same loadData() logic)
- Coordinator unchanged (same UIHostingController wrapping)

## Technical Unknowns Resolution

**Q1**: Should loading/error states be full-screen overlays or inline in scroll?  
**A**: Inline in single ScrollView (FR-006) - hero/header remain visible during loading/error

**Q2**: Should hero panel or list header be pinned/sticky during scroll?  
**A**: No pinned sections (FR-007) - everything scrolls together

**Q3**: How to handle state transitions (loading → success) with scroll position?  
**A**: SwiftUI preserves scroll position automatically when content changes (no manual handling needed)

**Q4**: Should AnnouncementCardsListView remain reusable in other contexts?  
**A**: Yes - make it pure content component (no scroll management) that works in any scroll context

**Q5**: Is scroll performance a concern with 5-10 announcement cards?  
**A**: No - LazyVStack handles 10+ items efficiently, landing page shows max 5 items (well within performance budget)

## Impact Analysis

**Affected Components**:
- `LandingPageView.swift` - wrap in ScrollView, use LazyVStack, remove frame modifier
- `AnnouncementCardsListView.swift` - remove nested ScrollView, keep ZStack for states
- `LandingPageViewModelTests.swift` - verify tests still pass (no logic changes)
- E2E tests - add scroll verification scenarios

**Unaffected Components**:
- `LandingPageViewModel.swift` - no changes (UI-only refactoring)
- `HomeCoordinator.swift` - no changes (same view wrapping)
- `HeroPanelView.swift` - no changes (same composition in parent)
- `ListHeaderRowView.swift` - no changes (same composition in parent)
- `AnnouncementCardsListViewModel.swift` - no changes (same state management)

**Risk Assessment**:
- **Low risk**: UI-only refactoring, no business logic changes
- **Testable**: E2E tests verify scroll behavior, unit tests verify no regressions
- **Reversible**: Easy to revert if issues discovered (2 file changes)

## References

- **Apple HIG**: [Scroll Views](https://developer.apple.com/design/human-interface-guidelines/scroll-views) - avoid nested scrolling
- **SwiftUI Documentation**: [ScrollView](https://developer.apple.com/documentation/swiftui/scrollview) - container for scrollable content
- **SwiftUI Documentation**: [LazyVStack](https://developer.apple.com/documentation/swiftui/lazyvstack) - lazy vertical stack for performance
- **Feature Spec**: [spec.md](./spec.md) - functional requirements FR-001 through FR-007

## Summary

Replace nested scroll architecture in LandingPageView with single outer ScrollView wrapping LazyVStack containing all landing page sections. `AnnouncementCardsListView` gains `hasOwnScrollView: Bool = true` parameter for conditional scroll behavior, allowing LandingPageView to disable nested scroll while preserving standalone usage in AnnouncementListView.

**Trade-off**: FR-001 technically allows nested scroll (misuse possible), but practically satisfied for LandingPageView via explicit `hasOwnScrollView: false`. Component reusability prioritized over strict spec compliance.

This approach follows iOS best practices, preserves existing MVVM-C architecture, and maintains backwards compatibility. Changes are low-risk (UI-only), testable (E2E coverage), and reversible (2 file modifications).

