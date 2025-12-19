# API Contracts: iOS Map Preview - Display Missing Pet Pins

**Feature Branch**: `KAN-30-ios-show-pins-on-the-map`  
**Created**: 2025-12-19

## No New Contracts

This feature does not introduce new API contracts. It consumes existing backend endpoints:

### Existing Endpoint Used

**GET /api/v1/announcements**

Query parameters (already supported):
- `lat` - Latitude of user location
- `lng` - Longitude of user location
- `range` - Search radius in kilometers (iOS will use `10` for landing page)

Response: Existing `AnnouncementsListResponse` with array of `AnnouncementDTO`.

### iOS Changes Only

This specification affects only iOS client code:
1. `MapPreviewView` - renders pins on static map
2. `LandingPageViewModel` - creates pin models from announcements
3. `AnnouncementCardsListViewModel` - exposes pin-eligible announcements
4. `AnnouncementListQuery` - adds `range` parameter for 10km query

No backend changes required.

