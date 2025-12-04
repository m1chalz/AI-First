# Research: Pet Details Screen (Web UI)

**Feature**: 027-web-pet-details-screen  
**Date**: 2025-11-27  
**Status**: ✅ Complete

## Overview

This document consolidates research findings and implementation decisions for the web pet details modal feature. All technical decisions have been made based on existing codebase patterns, React best practices, and the feature specification.

## Research Findings

### 1. Modal Implementation Pattern

**Decision**: Use React Portal with custom modal component

**Rationale**:
- React Portal (`ReactDOM.createPortal`) enables rendering modal outside normal DOM hierarchy
- Prevents z-index conflicts and ensures modal appears above all content
- Standard React pattern for modals and overlays
- No external library needed (built into React)

**Alternatives Considered**:
- **React Modal libraries (react-modal, @reach/dialog)**: Adds dependency, but provides accessibility features out-of-the-box
- **CSS-only modals**: Limited programmatic control, harder to manage focus/scroll lock

**Implementation Approach**:
```typescript
// Modal component using React Portal
const Modal = ({ isOpen, onClose, children }) => {
  if (!isOpen) return null;
  
  return ReactDOM.createPortal(
    <div className={styles.backdrop} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        {children}
      </div>
    </div>,
    document.body
  );
};
```

---

### 2. State Management for Modal

**Decision**: React state (useState) for modal open/close and selected pet ID

**Rationale**:
- Simple and sufficient for modal state management
- No need for URL query parameters (modal is transient UI element)
- Avoids URL pollution and browser history entries
- Matches clarification decision from spec

**Alternatives Considered**:
- **URL query parameters (?petId=xxx)**: Enables shareable links, but adds complexity and URL changes
- **Global state management (Redux, Zustand)**: Overkill for simple modal state

**Implementation Approach**:
```typescript
const [isModalOpen, setIsModalOpen] = useState(false);
const [selectedPetId, setSelectedPetId] = useState<string | null>(null);

const openModal = (petId: string) => {
  setSelectedPetId(petId);
  setIsModalOpen(true);
};

const closeModal = () => {
  setIsModalOpen(false);
  setSelectedPetId(null);
};
```

---

### 3. Focus Management and Accessibility

**Decision**: Manual focus trap and body scroll lock implementation

**Rationale**:
- Full control over focus behavior
- No external dependencies
- Matches project's minimal dependency philosophy
- Can be implemented with native browser APIs

**Alternatives Considered**:
- **Focus trap libraries (focus-trap-react, react-focus-lock)**: Provides accessibility features, but adds dependency
- **Body scroll lock libraries (body-scroll-lock)**: Handles edge cases, but adds dependency

**Implementation Approach**:
```typescript
// Focus trap: Track focusable elements, cycle focus within modal
useEffect(() => {
  if (!isOpen) return;
  
  const focusableElements = modalRef.current?.querySelectorAll(
    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
  );
  
  const firstElement = focusableElements?.[0] as HTMLElement;
  const lastElement = focusableElements?.[focusableElements.length - 1] as HTMLElement;
  
  const handleTab = (e: KeyboardEvent) => {
    if (e.key !== 'Tab') return;
    
    if (e.shiftKey && document.activeElement === firstElement) {
      e.preventDefault();
      lastElement?.focus();
    } else if (!e.shiftKey && document.activeElement === lastElement) {
      e.preventDefault();
      firstElement?.focus();
    }
  };
  
  firstElement?.focus();
  document.addEventListener('keydown', handleTab);
  
  return () => document.removeEventListener('keydown', handleTab);
}, [isOpen]);

// Body scroll lock: Prevent background scrolling when modal is open
useEffect(() => {
  if (!isOpen) return;
  
  const originalOverflow = document.body.style.overflow;
  document.body.style.overflow = 'hidden';
  
  return () => {
    document.body.style.overflow = originalOverflow;
  };
}, [isOpen]);
```

---

### 4. Data Formatting Utilities

**Decision**: Create separate utility functions for date, coordinate, and microchip formatting

**Rationale**:
- Reusable across components
- Testable in isolation
- Follows single responsibility principle
- Matches clarification decisions from spec

**Implementation Approach**:
```typescript
// date-formatter.ts
export function formatDate(isoDate: string): string {
  const date = new Date(isoDate);
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  }); // "Nov 18, 2025"
}

// coordinate-formatter.ts
export function formatCoordinates(lat: number, lng: number): string {
  const latDir = lat >= 0 ? 'N' : 'S';
  const lngDir = lng >= 0 ? 'E' : 'W';
  return `${Math.abs(lat).toFixed(4)}° ${latDir}, ${Math.abs(lng).toFixed(4)}° ${lngDir}`;
}

// microchip-formatter.ts
export function formatMicrochip(raw: string): string {
  // Format: "000-000-000-000"
  const cleaned = raw.replace(/\D/g, '');
  return cleaned.replace(/(\d{3})(\d{3})(\d{3})(\d{3})/, '$1-$2-$3-$4');
}
```

---

### 5. External Map Integration

**Decision**: Open external map (Google Maps/OpenStreetMap) in new browser tab

**Rationale**:
- Simple implementation (URL link)
- No need for map libraries or API keys
- Better user experience (full-featured map application)
- Matches clarification decision from spec

**Alternatives Considered**:
- **Embedded map (Google Maps Embed API, Leaflet)**: Requires API keys, adds complexity, larger bundle size
- **In-app map component**: Significant development effort, requires map library

**Implementation Approach**:
```typescript
// map-url-builder.ts
export function buildGoogleMapsUrl(lat: number, lng: number): string {
  return `https://www.google.com/maps?q=${lat},${lng}`;
}

export function buildOpenStreetMapUrl(lat: number, lng: number): string {
  return `https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}&zoom=15`;
}

// Usage in component
<a
  href={buildGoogleMapsUrl(pet.latitude, pet.longitude)}
  target="_blank"
  rel="noopener noreferrer"
  data-testid="petDetails.showMapButton.click"
>
  Show on the map
</a>
```

---

### 6. Error Handling Strategy

**Decision**: Generic error message with retry button for all API errors

**Rationale**:
- Simplified user experience (no need to understand different error types)
- Retry mechanism handles transient failures
- Matches clarification decision from spec
- Consistent error handling across the application

**Implementation Approach**:
```typescript
const [error, setError] = useState<string | null>(null);

const loadPetDetails = async () => {
  setIsLoading(true);
  setError(null);
  
  try {
    const pet = await petService.getPetById(selectedPetId);
    setPetDetails(pet);
  } catch (err) {
    setError('Failed to load pet details');
  } finally {
    setIsLoading(false);
  }
};

// Error state UI
{error && (
  <div className={styles.errorState}>
    <p>{error}</p>
    <button onClick={loadPetDetails} data-testid="petDetails.retryButton.click">
      Retry
    </button>
  </div>
)}
```

---

### 7. Responsive Design Strategy

**Decision**: CSS media queries with mobile-first approach

**Rationale**:
- Standard web development practice
- No JavaScript needed for responsive behavior
- Better performance (CSS handles layout changes)
- Matches specification requirements

**Implementation Approach**:
```css
/* Mobile-first: full-screen modal */
.modal {
  width: 100%;
  height: 100%;
  max-width: none;
  border-radius: 0;
}

/* Tablet: centered dialog */
@media (min-width: 768px) {
  .modal {
    width: 100%;
    max-width: 640px;
    height: auto;
    border-radius: 8px;
  }
  
  .backdrop {
    background-color: rgba(0, 0, 0, 0.5);
  }
}

/* Desktop: larger centered dialog */
@media (min-width: 1024px) {
  .modal {
    max-width: 768px;
  }
}
```

---

### 8. API Integration

**Decision**: Use existing AnimalRepository with new method for pet details

**Rationale**:
- Consistent with existing codebase patterns
- Reuses existing HTTP client configuration
- No need for new service layer

**Implementation Approach**:
```typescript
// Extend existing AnimalRepository
class AnimalRepository {
  // ... existing methods ...
  
  async getPetById(id: string): Promise<PetDetails> {
    const response = await fetch(`${API_BASE_URL}/api/v1/announcements/${id}`);
    if (!response.ok) {
      throw new Error('Failed to fetch pet details');
    }
    return response.json();
  }
}
```

---

## Summary

All technical decisions have been made based on:
- Existing codebase patterns and architecture
- React best practices
- Feature specification clarifications
- Minimal dependency philosophy
- Accessibility requirements (WCAG AA)

No external research tasks remain. Implementation can proceed to Phase 1 (Design & Contracts).

