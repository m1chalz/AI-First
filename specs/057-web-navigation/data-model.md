# Data Model: Web App Navigation Bar

**Feature**: 057-web-navigation  
**Date**: 2025-12-16  
**Status**: Complete

## Overview

This document defines the entities, state management, and data structures for the web navigation bar feature. The navigation bar is a presentation-layer feature with minimal state managed by React Router.

## Entities

### NavigationItem

Represents a single clickable item in the navigation bar.

**Attributes**:
- `id`: string - Unique identifier (e.g., "home", "lostPet", "foundPet")
- `label`: string - Display text (e.g., "Home", "Lost Pet", "Found Pet")
- `icon`: React component - Icon component from react-icons (already installed v5.5.0)
- `path`: string - Destination URL path (e.g., "/", "/lost-pets", "/found-pets")
- `testId`: string - Test identifier for E2E/component tests (e.g., "navigation.home.link")

**Relationships**:
- Contained by: NavigationBar (1-to-many)
- No persistence required (static configuration)

**Validation Rules**:
- `id` must be unique within navigation bar
- `label` must be non-empty string
- `path` must start with "/" (absolute path)
- `testId` must follow convention: "navigation.{id}.link"

**State Transitions**:
- Active ↔ Inactive (based on current URL path)
- Hover ↔ Default (based on mouse interaction)

**TypeScript Interface**:
```typescript
interface NavigationItemConfig {
  id: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  path: string;
  testId: string;
}
```

---

### NavigationBar

Represents the entire navigation component containing multiple navigation items.

**Attributes**:
- `items`: NavigationItemConfig[] - Array of navigation item configurations
- `currentPath`: string - Current URL path (provided by React Router)
- `logoPath`: string - Path to PetSpot logo image
- `logoAlt`: string - Alt text for logo

**Relationships**:
- Contains: NavigationItem[] (1-to-many)
- Integrates with: React Router (useLocation, NavLink)

**Validation Rules**:
- `items` array must not be empty
- `logoPath` must be valid image path or URL
- All `items[].id` must be unique

**State Transitions**:
- Visible ↔ Hidden (based on screen width ≥768px)
- Active item changes based on URL navigation

**TypeScript Interface**:
```typescript
interface NavigationBarProps {
  items: NavigationItemConfig[];
  logoPath: string;
  logoAlt: string;
}
```

---

## State Management

### Component State

**Source of Truth**: React Router's URL state (`useLocation` hook)

**State Flow**:
1. User clicks navigation item → React Router updates URL
2. React Router triggers re-render with new `location.pathname`
3. `NavLink` components detect active state via pathname comparison
4. Active navigation item receives `isActive: true` prop
5. CSS classes update to reflect active/inactive states

**No local state required**: Navigation state is derived entirely from URL.

---

### Static Configuration

Navigation items are defined as a static configuration array:

```typescript
import { AiOutlineHome, BiSearch, AiOutlineHeart, AiOutlineMail, AiOutlineUser } from 'react-icons/ai';

const NAVIGATION_ITEMS: NavigationItemConfig[] = [
  {
    id: 'home',
    label: 'Home',
    icon: AiOutlineHome,
    path: '/',
    testId: 'navigation.home.link',
  },
  {
    id: 'lostPet',
    label: 'Lost Pet',
    icon: BiSearch,
    path: '/lost-pets',
    testId: 'navigation.lostPet.link',
  },
  {
    id: 'foundPet',
    label: 'Found Pet',
    icon: AiOutlineHeart,
    path: '/found-pets',
    testId: 'navigation.foundPet.link',
  },
  {
    id: 'contact',
    label: 'Contact Us',
    icon: AiOutlineMail,
    path: '/contact',
    testId: 'navigation.contact.link',
  },
  {
    id: 'account',
    label: 'Account',
    icon: AiOutlineUser,
    path: '/account',
    testId: 'navigation.account.link',
  },
];
```

**Rationale for static configuration**:
- Navigation structure is fixed (not user-configurable)
- No backend API required (presentation-only feature)
- Configuration can evolve (e.g., dynamic icons based on auth state) without restructuring

---

## Data Flow

### Navigation Interaction Flow

```
User Action: Click "Lost Pet" navigation item
    ↓
React Router: Navigate to "/lost-pets"
    ↓
Browser: Update URL to "/lost-pets"
    ↓
React Router: Trigger re-render with location.pathname = "/lost-pets"
    ↓
NavLink Components: Compare path prop with location.pathname
    ↓
Lost Pet NavLink: isActive = true (path matches)
Home NavLink: isActive = false (path doesn't match)
    ↓
CSS Classes Applied: Active item gets blue background, inactive items remain gray
    ↓
User sees: Lost Pet highlighted, other items unhighlighted
```

### Responsive Visibility Flow

```
Browser Window Resize
    ↓
CSS Media Query Evaluation: @media (max-width: 767px)
    ↓
Match (mobile): Apply display: none
    ↓
No Match (desktop): Apply display: flex
    ↓
User sees: Navigation visible on desktop (≥768px), hidden on mobile (<768px)
```

---

## Styling State

### CSS Module Classes

**NavigationBar Component**:
- `.navigationBar` - Base container styling (flexbox, padding, background)
- `.logo` - Logo image styling
- `.navigationItems` - Navigation items container (flexbox, gap)

**NavigationItem Component** (via NavLink):
- `.navigationItem` - Inactive state (gray text, transparent background)
- `.navigationItemActive` - Active state (blue text, blue background)
- `.navigationItem:hover` - Hover state (background color change)
- `.icon` - Icon styling (size, margin)

**Responsive Classes**:
```css
@media (max-width: 767px) {
  .navigationBar {
    display: none;
  }
}
```

---

## Accessibility State

**ARIA Attributes** (future enhancement, not in current scope):
- `aria-current="page"` on active navigation link
- `aria-label` on navigation container
- `role="navigation"` on nav element

**Current Scope**: Semantic HTML only
- Use `<nav>` element for navigation container
- Use `<a>` elements via React Router's NavLink
- Use `alt` attribute on logo image

---

## Test Data

### Test Scenarios

**Scenario 1: Home is active**
```typescript
const testState = {
  currentPath: '/',
  expectedActive: 'home',
  expectedInactive: ['lostPet', 'foundPet', 'contact', 'account'],
};
```

**Scenario 2: Lost Pet is active**
```typescript
const testState = {
  currentPath: '/lost-pets',
  expectedActive: 'lostPet',
  expectedInactive: ['home', 'foundPet', 'contact', 'account'],
};
```

**Scenario 3: Mobile viewport**
```typescript
const testState = {
  viewportWidth: 375, // mobile
  expectedVisibility: 'hidden',
};
```

**Scenario 4: Desktop viewport**
```typescript
const testState = {
  viewportWidth: 1024, // desktop
  expectedVisibility: 'visible',
};
```

---

## Summary

The navigation bar data model is intentionally simple:
- **No database persistence** - static configuration only
- **No API calls** - presentation-only feature
- **State derived from URL** - React Router as single source of truth
- **CSS-driven responsive behavior** - no JavaScript state for viewport detection

This simplicity aligns with constitution Principle XIV (performance not a concern) and minimizes complexity.
