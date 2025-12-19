# Quickstart: iOS tab bar design update

**Feature**: 065-ios-tabbar-design  
**Branch**: `065-ios-tabbar-design`  
**Date**: 2025-12-18

## Overview

This feature updates the iOS bottom tab bar visual style to match the Figma design. It's a **visual-only update** with no functional changes to navigation or business logic.

**Design Reference**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=974-4861&m=dev

## Prerequisites

1. **Xcode** installed (iOS 18+ SDK)
2. **Figma access** to view design reference
3. **Icon assets** exported from Figma (or use provided URLs in research.md)

## Quick Setup

### 1. Checkout Feature Branch

```bash
git checkout 065-ios-tabbar-design
```

### 2. Open Project in Xcode

```bash
open iosApp/iosApp.xcodeproj
```

### 3. Review Design

Open Figma link to see target design:
- Background: white (#FFFFFF)
- Selected tab: blue (#155dfc)
- Unselected tabs: gray (#6a7282)
- Custom icons (5 total)
- Top border: thin black line

### 4. Export Icon Assets

Download custom tab bar icons from Figma:

**Asset URLs** (valid for 7 days from 2025-12-18):
1. Home: `https://www.figma.com/api/mcp/asset/5b413598-1548-47b7-a356-ae5260cf1f94`
2. Lost Pet: `https://www.figma.com/api/mcp/asset/08f536f5-d6ec-4211-817f-8363e9311f75`
3. Found Pet: `https://www.figma.com/api/mcp/asset/e159d927-701c-4fbe-8027-725230fe63b4`
4. Contact Us: `https://www.figma.com/api/mcp/asset/51397536-55ba-40fc-a96b-944f42b868fb`
5. Account: `https://www.figma.com/api/mcp/asset/c59ac936-720e-4720-953e-d648596865cb`

**Process** (PREFER SVG):
- **Recommended**: Download as SVG (iOS 13+, Xcode natively supports SVG!)
  - No conversion needed - just drag & drop into Asset Catalog
- **Alternative**: Download as PNG (24x24pt @1x, 48x48pt @2x, 72x72pt @3x)
- Ensure icons are monochrome (single color) for template rendering

### 5. Add Icons to Xcode Assets

1. Open `iosApp/iosApp/Assets.xcassets` in Xcode
2. Create new folder: `TabBar`
3. Add 5 image sets:
   - `home`
   - `lostPet`
   - `foundPet`
   - `contactUs`
   - `account`
4. For each image set:
   - **If SVG**: Drag & drop SVG, set "Scales" to "Single Scale", check "Preserve Vector Data" ✓
   - **If PNG**: Drag & drop @1x, @2x, @3x versions into respective slots
   - Set "Render As" to **"Template Image"** (critical!)

### 6. Key Files to Modify

| File | Changes |
|------|---------|
| `iosApp/iosApp/Coordinators/TabCoordinator.swift` | Update `configureTabBarAppearance()` and `configureTabBarItem()` methods |
| `iosApp/iosApp/Assets.xcassets/TabBar/` | Add 5 custom icon image sets |
| `iosApp/iosAppTests/Coordinators/TabCoordinatorTests.swift` | Optional: Add tests for appearance properties |

## Implementation Checklist

- [ ] Download icon assets from Figma as SVG (use URLs above)
- [ ] Add SVG icons to `Assets.xcassets/TabBar/` as template images ("Single Scale" + "Preserve Vector Data")
- [ ] Update `TabCoordinator.configureTabBarAppearance()`:
  - [ ] Background color: `#FFFFFF`
  - [ ] Selected color: `#155dfc`
  - [ ] Unselected color: `#6a7282`
  - [ ] Top border: `shadowColor = UIColor.black`
- [ ] Update `TabCoordinator.configureTabBarItem()`:
  - [ ] Replace SF Symbols with custom asset names
  - [ ] Remove `selectedImage` parameter (use template rendering)
- [ ] Build and run on iPhone 16 simulator
- [ ] Visual QA against Figma design
- [ ] Test tab navigation (no functional regressions)
- [ ] Test accessibility (max text size)

## Testing

### Build & Run

```bash
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' build
```

### Run Unit Tests

```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES
```

### Manual QA

1. **Visual Verification**:
   - Compare tab bar against Figma design side-by-side
   - Verify colors match exactly
   - Verify icon shapes match Figma
   - Verify top border is visible

2. **Navigation Testing**:
   - Tap each tab, verify navigation works
   - Verify selected state updates correctly
   - Verify no functional regressions

3. **Accessibility Testing**:
   - Settings → Accessibility → Display & Text Size → Larger Text
   - Set text size to maximum
   - Verify tab labels remain readable and tappable

## Design Color Reference

Quick reference for implementation:

```swift
// Background
appearance.backgroundColor = UIColor(hex: "#FFFFFF") // was #FAFAFA

// Top border
appearance.shadowColor = UIColor.black
appearance.shadowImage = UIImage()

// Unselected (normal)
normalItemAppearance.iconColor = UIColor(hex: "#6a7282") // was #808080
normalItemAppearance.titleTextAttributes = [
    .foregroundColor: UIColor(hex: "#6a7282")
]

// Selected (active)
selectedItemAppearance.iconColor = UIColor(hex: "#155dfc") // was #FF6B35
selectedItemAppearance.titleTextAttributes = [
    .foregroundColor: UIColor(hex: "#155dfc")
]
```

## Acceptance Criteria

Per spec.md, success criteria:

- **SC-001**: 0 high-severity visual deviations from Figma design
- **SC-002**: 0 blocker issues in navigation scenarios
- **SC-003**: Tab labels/icons readable at max text size

## Resources

- **Spec**: [spec.md](./spec.md)
- **Plan**: [plan.md](./plan.md)
- **Research**: [research.md](./research.md)
- **Figma Design**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=974-4861&m=dev

## Questions?

- Check [research.md](./research.md) for implementation details
- Review [spec.md](./spec.md) for functional requirements
- Compare current vs target design in Figma

---

**Estimated Time**: 2-3 hours (asset export + code changes + QA)

