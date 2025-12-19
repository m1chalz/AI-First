# Research: iOS tab bar design update

**Feature**: 065-ios-tabbar-design  
**Date**: 2025-12-18

## Research Questions

This document consolidates research findings for implementing the iOS tab bar design update. The primary goal is to update visual styling while preserving all existing functionality.

## 1. Custom Tab Bar Icon Assets from Figma

### Decision

Use **custom PNG/SVG assets** exported from Figma as **template images** with tint colors applied via `UITabBarAppearance`.

### Implementation Approach

1. **Export icons from Figma**:
   - **Preferred**: Download as **SVG** (vector format, scales perfectly, natively supported in Xcode)
   - Alternative: PNG at multiple resolutions (24x24pt @1x, 48x48pt @2x, 72x72pt @3x)
   - Figma provides asset URLs via MCP tool (already retrieved)
   - Icons should be **monochrome** (single color) for template rendering

2. **Add to Xcode Assets Catalog**:
   - Create `Assets.xcassets/TabBar/` folder
   - Add 5 image sets: `home.imageset`, `lostPet.imageset`, `foundPet.imageset`, `contactUs.imageset`, `account.imageset`
   - **For SVG** (iOS 13+, recommended):
     - Drag & drop SVG file directly into Asset Catalog
     - Set "Scales" to **"Single Scale"**
     - Check **"Preserve Vector Data"** ✓
   - **For PNG**: Import @1x, @2x, @3x versions
   - Set "Render As" to **"Template Image"** in Xcode inspector (critical for tinting)

3. **Use in code**:
   ```swift
   let tabBarItem = UITabBarItem(
       title: title,
       image: UIImage(named: "home"), // Template image
       selectedImage: nil // Use same image, tint will differentiate
   )
   ```

4. **Apply tint colors via UITabBarAppearance**:
   - Unselected: `#6a7282`
   - Selected: `#155dfc`

### Rationale

- Template rendering allows single icon asset with tint colors (no separate selected/unselected assets needed)
- Matches spec requirement FR-006: "custom tab bar icon assets... as template images, with selected/unselected states driven by tint via UITabBarAppearance"
- Cleaner asset management (5 icons instead of 10)
- **SVG native support**: Xcode 11+ (iOS 13+) supports SVG directly in Asset Catalog with "Preserve Vector Data" option - no PDF conversion needed

### Alternatives Considered

- **SF Symbols (current)**: Rejected - design specifies custom icons from Figma
- **Separate selected/unselected assets**: Rejected - template rendering is simpler and matches spec
- **PNG vs SVG**: SVG strongly preferred for:
  - Infinite scaling (future device resolutions)
  - Smaller file size (1 file vs 3)
  - Crisper rendering on all screens
  - Native Xcode support (iOS 13+, no conversion needed)
  - PNG acceptable fallback if SVG unavailable

## 2. UITabBarAppearance Configuration Best Practices

### Decision

Use **`UITabBarAppearance`** API (iOS 13+) to configure all visual properties in a single method.

### Implementation Approach

Update `TabCoordinator.configureTabBarAppearance()`:

```swift
private func configureTabBarAppearance() {
    let appearance = UITabBarAppearance()
    appearance.configureWithOpaqueBackground()
    
    // Background color: #FFFFFF (white)
    appearance.backgroundColor = UIColor(hex: "#FFFFFF")
    
    // Shadow (top border): 0.667px black
    appearance.shadowColor = UIColor.black
    appearance.shadowImage = UIImage() // Empty to enable shadowColor
    
    // Normal (inactive) item appearance
    let normalItemAppearance = appearance.stackedLayoutAppearance.normal
    normalItemAppearance.iconColor = UIColor(hex: "#6a7282")
    normalItemAppearance.titleTextAttributes = [
        .foregroundColor: UIColor(hex: "#6a7282"),
        .font: UIFont.systemFont(ofSize: 12) // Arial equivalent
    ]
    
    // Selected (active) item appearance
    let selectedItemAppearance = appearance.stackedLayoutAppearance.selected
    selectedItemAppearance.iconColor = UIColor(hex: "#155dfc")
    selectedItemAppearance.titleTextAttributes = [
        .foregroundColor: UIColor(hex: "#155dfc"),
        .font: UIFont.systemFont(ofSize: 12)
    ]
    
    // Apply appearance to both standard and scrollEdge
    _tabBarController.tabBar.standardAppearance = appearance
    _tabBarController.tabBar.scrollEdgeAppearance = appearance
}
```

### Rationale

- `UITabBarAppearance` is modern iOS API (13+) for styling tab bars
- Separates visual configuration from structural setup
- Centralizes all appearance settings in one method
- `standardAppearance` + `scrollEdgeAppearance` ensures consistent look across scroll states

### Alternatives Considered

- **Legacy UITabBar properties**: Rejected - deprecated, less control
- **SwiftUI TabView**: Rejected - app uses UIKit coordinators for navigation

## 3. Top Border Implementation

### Decision

Use **`UITabBarAppearance.shadowColor` and `shadowImage`** to create a subtle top border effect.

### Implementation Approach

```swift
appearance.shadowColor = UIColor.black // Top border color
appearance.shadowImage = UIImage() // Empty image to enable shadowColor
```

**Note**: iOS automatically renders `shadowColor` as a thin line (approximately 0.5-1px) when `shadowImage` is set to an empty `UIImage()`. This matches the Figma design requirement of 0.667px black border.

### Rationale

- Native iOS API for tab bar borders
- No custom drawing or layers required
- Matches Figma design: "border-t-[0.667px] solid black"

### Alternatives Considered

- **Custom CALayer border**: Rejected - more complex, less maintainable
- **Custom separator view**: Rejected - requires manual layout management

## 4. Font Selection

### Decision

Use **`UIFont.systemFont(ofSize: 12)`** as closest equivalent to Arial 12px.

### Rationale

- Figma design specifies Arial 12px
- iOS system font (San Francisco) is the native alternative
- System font provides better readability on iOS devices
- Design team typically accepts system font substitutions for native platforms

### Alternatives Considered

- **Custom Arial font**: Rejected - requires font file inclusion, design rarely requires exact Arial on iOS
- **Different size**: Rejected - 12pt matches Figma spec

## 5. Color Mappings

From Figma design to iOS implementation:

| Element | Figma | iOS UIColor(hex:) |
|---------|-------|-------------------|
| Background | `#FFFFFF` (white) | `UIColor(hex: "#FFFFFF")` |
| Top border | black (0.667px) | `UIColor.black` via shadowColor |
| Selected icon/text | `#155dfc` (blue) | `UIColor(hex: "#155dfc")` |
| Unselected icon/text | `#6a7282` (gray) | `UIColor(hex: "#6a7282")` |

## 6. Testing Strategy

### Unit Tests

Update `TabCoordinatorTests.swift` to verify appearance configuration:

```swift
func testTabBarAppearance_shouldMatchDesignColors() {
    // Given
    let coordinator = TabCoordinator()
    let tabBar = coordinator.tabBarController.tabBar
    
    // When
    let appearance = tabBar.standardAppearance
    
    // Then
    XCTAssertEqual(appearance.backgroundColor, UIColor(hex: "#FFFFFF"))
    XCTAssertEqual(appearance.stackedLayoutAppearance.normal.iconColor, UIColor(hex: "#6a7282"))
    XCTAssertEqual(appearance.stackedLayoutAppearance.selected.iconColor, UIColor(hex: "#155dfc"))
}
```

**Note**: Unit testing visual properties is optional - primary validation is manual QA + design review (per spec SC-001).

### Manual QA + Design Review

Primary validation method per spec:
- SC-001: 0 high-severity visual deviations from Figma design
- SC-002: 0 blocker issues in navigation scenarios
- SC-003: Accessibility verification (readable at max text size)

## 7. Asset Export Workflow

### Steps to Export Icons from Figma

1. **Download icon images**:
   - Figma URLs already retrieved via MCP tool:
     - Home: `https://www.figma.com/api/mcp/asset/5b413598-1548-47b7-a356-ae5260cf1f94`
     - Lost Pet: `https://www.figma.com/api/mcp/asset/08f536f5-d6ec-4211-817f-8363e9311f75`
     - Found Pet: `https://www.figma.com/api/mcp/asset/e159d927-701c-4fbe-8027-725230fe63b4`
     - Contact Us: `https://www.figma.com/api/mcp/asset/51397536-55ba-40fc-a96b-944f42b868fb`
     - Account: `https://www.figma.com/api/mcp/asset/c59ac936-720e-4720-953e-d648596865cb`

2. **Process icons** (PREFER SVG):
   - **Option A (RECOMMENDED): SVG** (iOS 13+, Xcode 11+):
     - Download SVG files directly from Figma URLs
     - No conversion needed! Xcode supports SVG natively
     - Ensure 24x24pt canvas size in SVG
   - **Option B: PNG raster** (fallback):
     - Download PNG at 24x24pt @1x, 48x48pt @2x, 72x72pt @3x
     - Less preferred (no vector scaling)
   - Ensure monochrome (single color) for template rendering

3. **Import to Xcode**:
   - Open `Assets.xcassets` in Xcode
   - Create folder `TabBar`
   - Add 5 image sets with descriptive names
   - **For SVG** (recommended): 
     - Drag & drop SVG file into image set
     - Set "Scales" to "Single Scale"
     - Check "Preserve Vector Data" ✓
   - **For PNG**: Drag & drop @1x, @2x, @3x files into respective slots
   - Set "Render As" to "Template Image" (critical!)

## Summary

All research questions resolved. Implementation is straightforward:

1. **Export 5 custom icons** from Figma (URLs provided)
2. **Add to Assets.xcassets/TabBar/** as template images
3. **Update TabCoordinator.swift**:
   - `configureTabBarAppearance()`: New colors, shadow for border
   - `configureTabBarItem()`: Use custom asset names instead of SF Symbols
4. **Test visually** against Figma design (primary validation)
5. **Optional unit tests** for appearance properties

No technical blockers identified. All requirements can be met with native iOS APIs.

---

**Next Phase**: Phase 1 - Design & Contracts (N/A - no data models or API contracts for this visual update)

