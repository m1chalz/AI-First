# Contracts: Animal Description Screen (iOS)

This directory contains interface contracts and type definitions for the Animal Description screen feature.

## Purpose

Contracts serve as:
- **Interface documentation**: Public APIs for ViewModel and components
- **Type contracts**: Model structures for component communication
- **Test boundaries**: Clear interfaces for test doubles (fakes, mocks)
- **Integration points**: Coordinator-ViewModel communication contracts

## Files

### AnimalDescriptionViewModel-interface.swift
Public interface for AnimalDescriptionViewModel showing:
- Published state properties
- User action methods
- Coordinator callback closures
- Dependency injection requirements

### Component-models.swift
Model structures for all reusable form components:
- ValidatedTextField.Model
- SpeciesDropdown.Model
- GenderSelector.Model
- LocationCoordinateFields.Model
- DescriptionTextArea.Model

## Usage

These contracts are **reference documentation** for implementation. They are NOT compiled into the iOS app—actual implementations live in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/`.

## Testing

Unit tests SHOULD use these contracts to verify:
- ViewModel exposes correct public API
- Component models have expected properties
- Coordinator callbacks are invoked with correct parameters

