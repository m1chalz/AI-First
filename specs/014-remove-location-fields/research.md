# Research: Remove Location Fields

**Feature**: Remove Location Fields  
**Date**: 2025-01-27  
**Status**: Complete

## Research Summary

This is a straightforward refactoring task with no significant unknowns. The implementation involves removing deprecated fields from the data model, which is a standard database migration and code cleanup operation.

## Decisions

### Decision 1: Database Migration Strategy

**Decision**: Drop columns directly without data transformation or logging.

**Rationale**: 
- Clarification from spec: "Simply drop the columns (data is permanently lost, migration is fast and simple)"
- Historical location data can be safely removed per assumptions
- Location information is already represented by latitude/longitude coordinates
- Simplest migration approach reduces complexity and risk

**Alternatives Considered**:
- **Option B**: Log data before dropping - Rejected because no business need for audit trail
- **Option C**: Transform/migrate data - Rejected because no target format needed

### Decision 2: Validation Error Handling

**Decision**: Use existing Zod strict mode to reject location fields as unknown fields (`INVALID_FIELD` code).

**Rationale**:
- Clarification from spec: "Treat as unknown fields (existing INVALID_FIELD code via Zod strict mode, no custom handling needed)"
- Consistent with existing validation pattern
- No custom error handling required
- Simplest implementation approach

**Alternatives Considered**:
- **Option B**: Custom error code for deprecated fields - Rejected because adds unnecessary complexity

### Decision 3: Migration Rollback Strategy

**Decision**: Migration includes `down()` function to restore columns (standard Knex pattern).

**Rationale**:
- Standard practice for database migrations
- Enables rollback if needed during deployment
- Knex supports reversible migrations
- No data recovery needed (columns recreated as nullable)

**Implementation**: Migration `down()` function will recreate columns as nullable to allow rollback without data loss concerns.

## Technology Choices

### Knex Migration

**Decision**: Use Knex `table.dropColumn()` for column removal.

**Rationale**:
- Standard Knex API for column removal
- Supports reversible migrations via `down()` function
- Already used in project for all migrations
- No additional dependencies needed

**Reference**: Knex.js documentation for `dropColumn()` method.

## No Additional Research Required

This refactoring task is straightforward and follows established patterns:
- Standard database migration (drop columns)
- TypeScript type updates (remove fields from interfaces)
- Validation schema updates (remove fields, strict mode handles rejection)
- Repository layer updates (remove field mapping)
- Service layer updates (remove field processing)
- Test updates (remove field references)
- Documentation updates (remove field descriptions)

All implementation details are clear from the specification and existing codebase patterns.

