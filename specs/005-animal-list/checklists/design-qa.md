# Design QA Checklist: Animal List Screen

**Purpose**: Measurable criteria for visual design accuracy (Success Criteria SC-002)  
**Figma References**:
- Mobile (Android/iOS): node 48:6096
- Web: node 71:9154

---

## Color Accuracy

**Pass Criteria**: Colors within ±2 RGB values of Figma specifications

| Element | Figma Spec | Tolerance | Verify |
|---------|------------|-----------|--------|
| Primary text | #2D2D2D | ±2 RGB | [ ] |
| Secondary text | #545F71 | ±2 RGB | [ ] |
| Tertiary text | #93A2B4 | ±2 RGB | [ ] |
| Background white | #FAFAFA | ±2 RGB | [ ] |
| Light gray placeholders | #EEEEEE | ±2 RGB | [ ] |
| Status "Active" badge | #FF0000 | ±2 RGB | [ ] |
| Status "Found" badge | #0074FF | ±2 RGB | [ ] |
| Status "Closed" badge | #93A2B4 | ±2 RGB | [ ] |
| Primary button | #2D2D2D | ±2 RGB | [ ] |
| Secondary button | #E5E9EC | ±2 RGB | [ ] |

---

## Typography Accuracy

**Pass Criteria**: Exact font family, size, and weight match

**Note**: Figma values shown in px. Implementation uses platform-specific units: **sp** (Android), **pt** (iOS), **px** (Web).

| Element | Font Family | Size (Figma) | Weight | Verify |
|---------|-------------|--------------|--------|--------|
| Screen title | Inter | 24px (24sp/24pt) | Regular (400) | [ ] |
| Card animal name | Inter | 16px (16sp/16pt) | Regular (400) | [ ] |
| Card species/breed | Inter | 14px (14sp/14pt) | Regular (400) | [ ] |
| Card location | Inter | 13px (13sp/13pt) | Regular (400) | [ ] |
| Status badge | Roboto | 12px (12sp/12pt) | Regular (400) | [ ] |
| Button labels | Inter | 16px (16sp/16pt) | Regular (400) | [ ] |

---

## Spacing & Layout

**Pass Criteria**: Spacing within ±1px (or ±1dp/1pt) of specifications

**Note**: Figma values in px. Implementation: **dp** (Android), **pt** (iOS), **px** (Web).

| Element | Specification (Figma) | Platform Units | Tolerance | Verify |
|---------|----------------------|----------------|-----------|--------|
| Card gap (between items) | 8px | 8dp/8pt/8px | ±1 unit | [ ] |
| Card padding horizontal | 16px | 16dp/16pt/16px | ±1 unit | [ ] |
| Search space (mobile) | 48-56px height | 48-56dp/48-56pt | ±2 units | [ ] |
| Search space (web) | 64px height | 64px | ±2px | [ ] |
| Image placeholder size (mobile) | 63x63px circular | 63dp/63pt | Exact | [ ] |

---

## Visual Effects

**Pass Criteria**: Exact match to Figma specifications

**Note**: Figma values in px. Implementation: **dp/elevation** (Android), **pt** (iOS), **px** (Web).

| Element | Specification (Figma) | Platform Units | Verify |
|---------|----------------------|----------------|--------|
| Card border radius | 4px | 4dp/4pt/4px | [ ] |
| Card shadow | 0px 1px 4px 0px rgba(0,0,0,0.05) | elevation 2dp / 1pt offset 4pt blur / 0px 1px 4px | [ ] |
| Status badge radius | 10px | 10dp/10pt/10px | [ ] |
| Button border radius | 2px | 2dp/2pt/2px | [ ] |
| Image placeholder radius | 50% (perfect circle) | 50% all platforms | [ ] |

---

## Platform-Specific Layout

### Mobile (Android/iOS)

**Note**: Specifications shown in Figma px. Android uses **dp**, iOS uses **pt**.

| Element | Specification (Figma) | Platform Units | Verify |
|---------|----------------------|----------------|--------|
| Screen title | "Missing animals list" centered, 24px | 24sp/24pt | [ ] |
| Single button at bottom | "Report a Missing Animal" fixed, always visible | N/A | [ ] |
| Cards | Full-width (328px at 375px screen), horizontal layout | Full-width with 16dp/16pt margins | [ ] |
| Search/Filters button | 186px wide, outlined style | 186dp/186pt | [ ] |

### Web

| Element | Specification | Verify |
|---------|---------------|--------|
| Sidebar navigation | 219px wide, dark background #4F3C4C | [ ] |
| Content area | 1181px wide | [ ] |
| Two buttons at top-right | "Report a Missing Animal" + "Report Found Animal", fixed | [ ] |
| Search bar width | 582px on left | [ ] |
| Cards | Full-width, table-like layout with description, gender icons | [ ] |

---

## Verification Method

1. **Screenshot comparison**: Take screenshot of implementation and overlay on Figma export at 100% zoom
2. **Color picker**: Use digital color picker to verify hex values (within ±2 RGB tolerance)
3. **Typography inspector**: Use browser DevTools / Xcode Inspector to verify computed font properties (sp for Android, pt for iOS, px for Web)
4. **Spacing measurement**: Use ruler tool in DevTools / design inspector (within ±1dp/±1pt/±1px tolerance per platform)
5. **Visual effects**: Inspect shadow, radius values in DevTools / inspector (dp for Android, pt for iOS, px for Web)

---

## Acceptance

**Pass Requirement**: ALL checkboxes must be checked with measurements within tolerance  
**Failure Action**: Document specific deviations and create remediation task  
**Sign-off**: [ ] Android, [ ] iOS, [ ] Web

---

## Notes

- Use browser DevTools "Show Rulers" and "Measure" features for web measurements
- Use Xcode "Debug View Hierarchy" for iOS measurements
- Use Android Studio "Layout Inspector" for Android measurements
- Take screenshots before and after fixes for documentation

