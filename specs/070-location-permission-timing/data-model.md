# Data Model: Location Permission Request Timing

**Feature**: 070-location-permission-timing  
**Date**: 2026-01-08  
**Updated**: 2026-01-08 (simplified approach)

## Overview

**No new data models required.**

The simplified approach uses Android's system-managed permission state directly. Each screen checks permission status via Accompanist's `rememberMultiplePermissionsState` - no custom state sharing needed.

## Existing Entities (Unchanged)

### RationaleDialogType

Already exists at `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/models/RationaleDialogType.kt`. No changes needed.

```kotlin
sealed interface RationaleDialogType {
    data object Educational : RationaleDialogType
    data object Informational : RationaleDialogType
}
```

### AnimalListUiState

No changes needed. Permission status continues to be managed as before.

### AnimalListIntent / AnimalListEffect

No changes needed. Existing permission-related intents and effects remain functional.

## Why No New Data Models

| Originally Planned | Why Not Needed |
|-------------------|----------------|
| LocationPermissionState | System manages permission state; screens check via Accompanist |
| LocationPermissionUiState | No centralized ViewModel needed |
| LocationPermissionIntent | Existing screen-level intents sufficient |
| LocationPermissionEffect | Existing screen-level effects sufficient |
| CompositionLocal | No state sharing required |

## State Flow

```
┌─────────────────────────────────────────────────────┐
│  Android System (Source of Truth)                   │
│  - Permission granted/denied state                  │
│  - shouldShowRequestPermissionRationale()           │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  Accompanist (Wrapper)                              │
│  rememberMultiplePermissionsState()                 │
│  - allPermissionsGranted                            │
│  - shouldShowRationale                              │
│  - launchMultiplePermissionRequest()                │
└──────────────────────┬──────────────────────────────┘
                       │
         ┌─────────────┴─────────────┐
         ▼                           ▼
┌─────────────────────┐   ┌─────────────────────┐
│  MainScaffold       │   │  AnimalListScreen   │
│  (fire-and-forget)  │   │  (check + rationale)│
└─────────────────────┘   └─────────────────────┘
```

Each screen independently accesses Accompanist state. No custom data models or state synchronization needed.
