# Research: Web Application Landing Page

**Feature**: 061-web-landing-page  
**Date**: 2025-12-17  
**Status**: Complete

## Research Findings

### 1. Backend API Endpoint

**Decision**: Use existing `/api/v1/announcements` endpoint with status filter

**Findings**:
- Endpoint location: `server/src/routes/announcements.ts`
- Route: `GET /api/v1/announcements`
- Query parameters (all optional):
  - `lat`: latitude for location-based filtering
  - `lng`: longitude for location-based filtering
  - `range`: distance range in km for location filtering
- Response format: `{ data: Announcement[] }`
- Announcement type definition in `server/src/types/announcement.d.ts`
- Status field: `'MISSING' | 'FOUND'`

**Implementation approach**:
- Frontend will call `/api/v1/announcements` endpoint
- Filter for `status === 'MISSING'` in frontend (backend returns both MISSING and FOUND)
- Limit to 5 most recent announcements after filtering
- Sort by `createdAt` field (newest first)
- Include user's geolocation (lat/lng) if available to get distance calculations

**Rationale**: Existing endpoint provides all required data. Frontend filtering is acceptable for this use case as we need a small subset (5 items). Backend already handles location-based distance calculations when lat/lng are provided.

**Alternatives considered**:
- Create new backend endpoint `/api/v1/announcements/recent-missing` → Rejected because existing endpoint is sufficient and adding a new endpoint increases maintenance burden
- Backend filtering with query param `?status=MISSING&limit=5` → Rejected because backend doesn't currently support these query params, and adding them would require backend changes (violates web-only feature scope)

---

### 2. Navigation Integration

**Decision**: Replace existing `Home` component with `LandingPage` component at root route

**Findings**:
- Navigation system: React Router v6
- Routes defined in `webApp/src/pages/routes.ts`
- Home route: `AppRoutes.home = '/'`
- Current Home component: `webApp/src/pages/Home.tsx` (placeholder)
- Navigation bar: `webApp/src/components/NavigationBar.tsx`
- Navigation items include "Home" tab with `HiOutlineHome` icon

**Implementation approach**:
- Replace `Home` component content with new `LandingPage` component
- Route already exists in `App.tsx`: `<Route path={AppRoutes.home} element={<Home />} />`
- Landing page will be the default view when users navigate to `/`
- "View all" link navigates to `AppRoutes.lostPets` (`/lost-pets`)
- Footer links:
  - "Report Lost Pet" → `AppRoutes.reportMissing.microchip`
  - "Report Found Pet" → Placeholder (no route yet, will use `#` or disabled link)
  - "Search Database" → Placeholder (non-functional per spec)

**Rationale**: Replacing the Home component content is simpler than creating a new route and ensures the landing page is immediately visible on app launch.

**Alternatives considered**:
- Create new route `/landing` and redirect from `/` → Rejected because it adds unnecessary complexity
- Keep Home as separate component and import LandingPage → Rejected because Home is currently a placeholder with no other purpose

---

### 3. CSS Approach and Shared Styles

**Decision**: Use CSS modules with shared color variables extracted to `:root` in each module

**Findings**:
- Project uses CSS modules (`.module.css` files)
- Global styles: `webApp/src/index.css` (minimal - only body font and background)
- Shared layout styles: `webApp/src/components/NewAnnouncement/NewAnnouncementLayout.module.css`
- Color palette:
  - Primary blue: `#155dfc` (navigation, primary buttons)
  - Text colors: `#101828` (dark), `#545F71` (secondary), `#93A2B4` (tertiary)
  - Background: `#FAFAFA`, `#F2F4F8`
  - Border: `#E5E7EB`, `#D1D5DC`
- Figma design reference in CSS comments: `https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=970-4075`

**Implementation approach**:
- Create separate CSS module for each component:
  - `LandingPage.module.css` - main layout, sections spacing
  - `HeroSection.module.css` - gradient background, feature cards grid
  - `FeatureCard.module.css` - individual card styles
  - `RecentPetsSection.module.css` - section layout, heading styles
  - `LandingPageCard.module.css` - simplified pet card (reuse/adapt from `AnnouncementCard.module.css`)
  - `Footer.module.css` - footer layout, columns, links
- Define color variables in `:root` of each module (or extract to shared CSS file if needed)
- Reuse existing color values from navigation and announcement list components
- Add Figma link in CSS comments per existing convention
- Gradient background for hero section: `linear-gradient(to bottom right, #EFF6FF, #F3E8FF)` (light blue to light purple per spec)

**Rationale**: CSS modules provide component-scoped styles, preventing global style conflicts. Existing components already use this pattern successfully.

**Alternatives considered**:
- Create global CSS variables file → Rejected because it's not the current pattern (each component defines its own variables)
- Use Tailwind CSS → Rejected because spec explicitly states "no Tailwind per project rules"
- Styled-components → Rejected because project already uses CSS modules consistently

---

### 4. Icon Library

**Decision**: Use `react-icons` library (Heroicons and Material Design icons)

**Findings**:
- Library: `react-icons` (already installed)
- Usage examples:
  - Navigation: `HiOutlineHome`, `HiOutlineSearch`, `HiOutlineLocationMarker`, `HiOutlineChatAlt2`, `HiOutlineUser` (Heroicons outline)
  - Location icon: `MdLocationOn` (Material Design)
- Icon size: typically `size={20}` or `size={24}`
- Icon colors: set via CSS `color` property

**Implementation approach**:
- Hero section feature cards:
  - Search Database: `HiOutlineSearch` with blue background (`#155DFC`)
  - Report Missing: `HiOutlineExclamation` or similar with red background (`#FB2C36`)
  - Found a Pet: `HiOutlineCheckCircle` or similar with green background (emerald - `#10B981`)
  - Location Based: `HiOutlineLocationMarker` with purple background (violet - `#7C3AED`)
- Footer:
  - Email: `HiOutlineMail` or `MdEmail`
  - Phone: `HiOutlinePhone` or `MdPhone`
  - Address: `HiOutlineLocationMarker` or `MdLocationOn`
- Recent pets section:
  - Calendar icon: `HiOutlineCalendar` for report date
  - Use existing `MdLocationOn` for location (consistent with `AnnouncementCard`)

**Rationale**: `react-icons` is already installed and used throughout the project. Consistent icon style (Heroicons outline) maintains visual coherence.

**Alternatives considered**:
- Install new icon library (e.g., Lucide React) → Rejected because it adds unnecessary dependency
- Use SVG icons directly → Rejected because `react-icons` provides type-safe, tree-shakeable imports

---

### 5. Existing Components for Reuse

**Decision**: Create simplified landing page card component, inspired by `AnnouncementCard` but adapted for landing page layout

**Findings**:
- Existing component: `webApp/src/components/AnnouncementList/AnnouncementCard.tsx`
- Current card layout:
  - Horizontal layout: photo (left) | info | description | status/date/button (right)
  - Width: fluid (stretches to container)
  - Height: 140px minimum
  - Photo: 140x140px square, left edge
  - Includes: location, species/breed, gender, description, status badge, date, details button
- Uses: `ANNOUNCEMENT_STATUS_BADGE_COLORS` from `webApp/src/types/announcement.ts`
- Styling: `AnnouncementList.module.css`

**Implementation approach**:
- Create new `LandingPageCard` component in `/webApp/src/components/home/` for landing page with simplified layout:
  - Photo at top (not left)
  - Status badge (MISSING only - red)
  - Location with distance
  - Pet type and breed
  - Report date with calendar icon
  - Clickable card (navigates to pet details)
- Do NOT reuse existing `AnnouncementCard` directly because:
  - Landing page requires different layout (vertical, not horizontal)
  - Landing page cards need to be more compact (grid layout, not list)
  - Landing page doesn't need description text, gender icon, or details button
- All landing page components organized in `/webApp/src/components/home/` directory:
  - `LandingPage.tsx` - Main component
  - `HeroSection.tsx` - Hero section
  - `FeatureCard.tsx` - Feature cards
  - `RecentPetsSection.tsx` - Recent pets section
  - `LandingPageCard.tsx` - Pet card for landing page
  - `Footer.tsx` - Footer component
- Reuse utility functions:
  - `formatCoordinates` from `webApp/src/utils/coordinate-formatter.ts`
  - `toPascalCase` from `webApp/src/utils/pascal-case-formatter.ts`
- Reuse color constants:
  - `ANNOUNCEMENT_STATUS_BADGE_COLORS` for status badges
- Reuse types:
  - `Announcement` from `webApp/src/types/announcement.ts`
  - `AnnouncementStatus`, `AnnouncementSex` from same file

**Rationale**: Landing page requires a different visual treatment than the announcement list. Creating a new component allows for optimized layout while reusing utility functions and types.

**Alternatives considered**:
- Reuse `AnnouncementCard` as-is → Rejected because layout doesn't match landing page design (horizontal vs vertical)
- Add layout props to `AnnouncementCard` → Rejected because it would complicate the existing component with conditional logic
- Create completely new implementation → Rejected because utilities and types can be reused

---

### 6. Announcement Fetching Hook

**Decision**: Reuse existing `use-announcement-list.ts` hook with filtering/sorting logic in component

**Findings**:
- Existing hook: `webApp/src/hooks/use-announcement-list.ts`
- Already fetches announcements via `announcementService.getAnnouncements()`
- Already integrates with `GeolocationContext` for user location
- Returns: `announcements`, `isLoading`, `error`, `isEmpty`, `loadAnnouncements`, `geolocationError`
- Returns ALL announcements (both MISSING and FOUND)

**Implementation approach**:
- Reuse `useAnnouncementList()` hook in landing page component
- Apply filtering, sorting, and limiting in component:
  ```typescript
  const { announcements, isLoading, error } = useAnnouncementList();
  
  const recentMissingPets = announcements
    .filter(a => a.status === 'MISSING')
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5);
  ```
- Create utility functions for presentation formatting:
  - `/webApp/src/lib/date-utils.ts`: `formatRelativeDate(dateString: string): string`
  - `/webApp/src/lib/distance-utils.ts`: `formatDistance(distanceKm: number | undefined): string`

**Rationale**: Reusing existing hook eliminates code duplication and maintains consistency with announcement list page. Filtering/sorting logic is simple enough to remain in component. Date/distance formatting utilities are pure functions suitable for `/lib` directory.

**Alternatives considered**:
- Create new `use-pets.ts` hook → Rejected because it duplicates `use-announcement-list.ts` functionality
- Create wrapper hook `useRecentMissingPets()` → Rejected because filtering/sorting logic is trivial and doesn't warrant a separate hook
- Move filtering to backend with new endpoint → Rejected because it violates web-only feature scope

### 7. Date and Distance Formatting

**Decision**: Create utility functions in `/webApp/src/lib/` for date and distance formatting

**Findings**:
- Existing utilities:
  - `webApp/src/utils/coordinate-formatter.ts` - formats coordinates
  - `webApp/src/utils/pascal-case-formatter.ts` - converts strings to PascalCase
- Date format in backend: ISO 8601 strings (e.g., `"2025-12-17T10:30:00Z"`)
- Distance calculation: Backend calculates distance when lat/lng provided, but format unknown
- Project convention: Pure utilities in `/webApp/src/lib/` (per constitution, new utilities go in `/lib`, not `/utils`)

**Implementation approach**:
- Create `/webApp/src/lib/date-utils.ts`:
  - `formatRelativeDate(dateString: string): string` - "2 days ago", "1 week ago", etc.
  - Use native JavaScript `Date` API, no external libraries (e.g., date-fns, moment.js)
  - Test with unit tests in `/webApp/src/lib/__test__/date-utils.test.ts`
- Create `/webApp/src/lib/distance-utils.ts`:
  - `formatDistance(distanceKm: number | undefined): string` - "1.5 km away", "500 m away", "Location unknown"
  - Handle edge cases: undefined, null, 0, very large distances
  - Test with unit tests in `/webApp/src/lib/__test__/distance-utils.test.ts`
- Note: Backend may not return distance in response. If not available, display "Location unknown" or only show coordinates

**Rationale**: Date and distance formatting are pure functions suitable for the `/lib` directory per constitution. Native JavaScript Date API is sufficient for relative date formatting without adding dependencies.

**Alternatives considered**:
- Use `date-fns` library → Rejected because it adds unnecessary dependency for simple relative date formatting
- Keep formatting logic in components → Rejected because it violates constitution (business logic must be in `/lib` or `/hooks`)
- Reuse existing `coordinate-formatter` → Rejected because it formats coordinates (lat/lng), not distances

---

### 8. Responsive Layout Strategy

**Decision**: Desktop-first responsive design with tablet breakpoint at 768px

**Findings**:
- Spec requirement: Tablet (768px+) and desktop (1024px+), mobile not supported
- Existing media query in navigation: `@media (max-width: 767px)` hides navigation bar
- Desktop layout:
  - Hero section: 4 feature cards in horizontal row
  - Recent pets: 5 cards in horizontal scrollable row or grid
  - Footer: 3 columns (branding, links, contact)
- Tablet layout:
  - Hero section: Feature cards may stack 2x2 grid
  - Recent pets: Horizontal scrollable or 2-column grid
  - Footer: May stack or use 2-column layout

**Implementation approach**:
- Desktop (1024px+): Default layout
  - Hero: 4-column grid for feature cards
  - Recent pets: 5 cards in horizontal grid (may scroll if needed)
  - Footer: 3-column layout
- Tablet (768px-1023px): Media query adjustments
  - Hero: 2x2 grid for feature cards
  - Recent pets: Horizontal scroll or 2-column grid
  - Footer: 2-column layout or stack
- Mobile (<768px): Not supported, but graceful degradation
  - Hero: Single column stack
  - Recent pets: Single column stack
  - Footer: Single column stack
  - Note: Native mobile apps handle mobile users per spec

**Rationale**: Desktop-first approach aligns with target audience (tablet/desktop users). Media queries at 768px match existing navigation breakpoint.

**Alternatives considered**:
- Mobile-first responsive design → Rejected because mobile is explicitly not supported per spec
- Fixed desktop-only layout → Rejected because tablet support is required
- Use CSS Grid auto-fit → Considered, but explicit media queries provide more control

---

## Summary of Key Decisions

1. **Backend API**: Use existing `/api/v1/announcements` with frontend filtering for `status=MISSING`, limit 5
2. **Navigation**: Replace `Home` component content with `LandingPage` at root route `/`
3. **CSS**: CSS modules per component, reuse color palette from existing components
4. **Icons**: `react-icons` library (Heroicons + Material Design)
5. **Components**: Create new simplified `LandingPageCard` for landing page, reuse utilities and types from `AnnouncementCard`
6. **Hook**: Reuse existing `useAnnouncementList()` hook with filtering/sorting in component
7. **Utilities**: Date and distance formatting in `/webApp/src/lib/` with unit tests
8. **Responsive**: Desktop-first with tablet breakpoint at 768px

## Open Questions / Assumptions

- **Distance calculation**: Assuming backend returns distance when lat/lng provided, but format unknown. Will handle in implementation phase.
- **Footer "Report Found Pet" link**: Spec says "navigate to corresponding feature page/tab" but no route exists yet. Will use placeholder `#` or implement basic route if needed.
- **Empty state**: Spec defines empty state message for no MISSING pets. Will implement as simple text message in landing page section.
- **Loading state**: Spec requires loading indicators. Will implement skeleton loaders for pet cards similar to existing pattern.
- **Error handling**: Spec defines error message for API failures. Will implement error state with retry instruction (no retry button per spec).

## Next Steps

Proceed to Phase 1: Generate data model, API contracts, and quickstart guide.

