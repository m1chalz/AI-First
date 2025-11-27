# Figma Design Update Analysis - Pet Details Screen

**Date**: 2025-11-26  
**Figma Node**: 297-7437 (Pet detail streched)  
**Previous Figma Node**: 179-8157  
**Current Spec Location**: `/specs/010-pet-details-screen/spec.md`

## Summary

The Figma design has been updated from the previous version. This analysis compares the new design (node 297-7437) with the current specification to identify what needs updating.

---

## Key Changes & Updates Required

### 1. ✅ Layout & Structure (NO CHANGES NEEDED)
- **Current Spec**: Vertical scrolling layout with hero image at top, content below
- **New Design**: Same vertical scrolling layout maintained
- **Status**: ✅ MATCHES - No update needed

---

### 2. ✅ Header Section (NO CHANGES NEEDED)
- **Current Spec**: Mobile phone status bar, back button (X icon)
- **New Design**: Mobile phone status bar with time, signal, battery; back button (X icon)
- **Status**: ✅ MATCHES - Design detail is consistent

---

### 3. ✅ Hero Image Section (NO CHANGES NEEDED)
- **Current Spec**: Full-width pet photo with status badge (top-right) and reward badge (left side)
- **New Design**: 
  - Pet photo with status badge "MISSING" (red, top-right)
  - Reward badge "500 PLN" with money bag icon (left side on image)
  - Back button (X icon, white, top-left)
- **Status**: ✅ MATCHES - All elements present

---

### 4. ✅ Contact Information (CLARIFIED)

**Current Spec (Line 15)**:
```
- Q: Should phone and email be masked? → A: No, display phone and email in full without masking
```

**New Design Shows**:
- Phone: `+ 48 ********` (shown as masked, but this is placeholder/mock data only)
- Email: `mail@email.com` (NOT masked)

**Status**: ✅ **CLARIFIED** - Phone and email should NOT be masked. Design shows asterisks as placeholder/mock data only.

**Action Taken**: 
- ✅ Updated spec clarification to note that masked phone in design is placeholder only
- ✅ Spec already correctly states: "display phone and email in full without masking"

---

### 5. ✅ Date of Disappearance (NO CHANGES NEEDED)
- **Current Spec**: "MMM DD, YYYY" format (e.g., "Nov 18, 2025")
- **New Design**: `Nov 18, 2025`
- **Status**: ✅ MATCHES exactly

---

### 6. ✅ Identification Information (NO CHANGES NEEDED)
- **Current Spec**: 
  - Animal Name & Microchip (2-column)
  - Animal Species & Race (2-column)
  - Animal Sex (with icon) & Age (2-column)
- **New Design**: 
  - Animal Name: `Max` | Microchip: `000-000-000-000` (2-column) ✅
  - Animal Species: `Dog` | Animal Race: `Doberman` (2-column) ✅
  - Animal Sex: `♂ Male` | Animal Approx. Age: `3 years` (2-column) ✅
- **Status**: ✅ MATCHES exactly

---

### 7. 🔄 Location Information (CHANGED - REQUIRES UPDATE)

**Current Spec**: 
- FR-011: "Screen MUST display place of disappearance with city name, radius (e.g., '±15 km'), and location icon"
- User Story 3: "city name and approximate radius are displayed (e.g., 'Warsaw • ±15 km')"

**New Design**:
- Label: `Lat / Long`
- Value: `52.2297° N, 21.0122° E` (with location pin icon)
- "Show on the map" button (blue outlined button)

**Status**: ⚠️ **CHANGED** - Design now shows coordinates instead of city/radius format.

**Action Required**: 
- ✅ Updated FR-011 to reflect coordinates format
- ✅ Updated User Story 3 acceptance criteria
- ✅ Updated edge cases section
- ✅ Updated Key Entities section
- ✅ Updated design README

---

### 8. ✅ Additional Description (NO CHANGES NEEDED)
- **Current Spec**: Multi-line text without truncation
- **New Design**: 
  ```
  Friendly and energetic golden retriever looking for a loving home. 
  Great with kids and other pets.
  ```
- **Status**: ✅ MATCHES

---

### 9. ✅ Remove Report Button (NO CHANGES NEEDED)
- **Current Spec**: Red button at bottom of screen, full width
- **New Design**: Red button ("Remove Report") at bottom, full width
- **Status**: ✅ MATCHES exactly

---

### 10. ✅ Status Badge Colors (NO CHANGES NEEDED)
- **Current Spec**: 
  - Red for MISSING
  - Blue for FOUND
  - Gray for CLOSED
- **New Design**: Shows red "MISSING" badge
- **Status**: ✅ MATCHES (shows MISSING example)

---

## Summary of Required Changes

### ✅ COMPLETED UPDATES

1. **Phone Number Masking** ✅
   - Clarified: Phone and email should NOT be masked
   - Updated spec clarification to note that masked phone in design is placeholder/mock data only
   - Spec already correctly states: "display phone and email in full without masking"

2. **Location Field Format** ✅
   - **Changed from**: City name + radius (e.g., "Warsaw • ±15 km")
   - **Changed to**: Latitude/Longitude coordinates (e.g., "52.2297° N, 21.0122° E")
   - Updated FR-011 to reflect coordinates format
   - Updated User Story 3 acceptance criteria
   - Updated edge cases section (location city → location coordinates)
   - Updated Key Entities section (removed city/radius, kept coordinates)
   - Updated design README with new Figma node ID and location format

### ✅ NO OTHER CHANGES REQUIRED

All other aspects of the design match the current specification:
- Layout structure
- Field organization and content
- Button placement and styling
- Status badge system
- Reward badge display
- Contact information structure
- Additional description handling
- Remove Report button

---

## Final Status

**All updates completed!** ✅

- Phone masking clarified: NOT masked (design shows placeholder data)
- Location format updated: Changed from city/radius to coordinates
- Spec updated: All relevant sections reflect new design
- Design README updated: New Figma node ID and location format documented


