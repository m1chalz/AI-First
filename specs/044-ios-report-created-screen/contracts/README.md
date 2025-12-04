# API Contracts

**Feature**: iOS Report Created Confirmation Screen  
**Status**: No API changes required

## Overview

This feature is an **iOS-only UI implementation** with no backend or API changes. The management password displayed on the confirmation screen is already provided by the existing backend API during report submission.

## Existing API Contract (No Changes)

### POST /announcements

**Endpoint**: `/api/announcements` (existing)

**Response** (relevant fields):
```json
{
  "id": 123,
  "managementPassword": "5216577",
  "createdAt": "2025-12-03T10:30:00Z",
  ...
}
```

**Notes**:
- Backend already generates and returns `managementPassword` in the response
- Backend already sends management password via email to user (per spec Update Notes 2025-12-03)
- No new API endpoints required
- No existing API modifications required

## Summary

No OpenAPI/Swagger specifications to add. This directory exists for consistency with the plan template structure but contains no contract definitions for this feature.

