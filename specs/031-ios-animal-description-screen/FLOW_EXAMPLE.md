# Przykładowy Przepływ: Odblokowanie Race TextField

**Scenariusz**: User wybiera species "Dog" z dropdowna → race text field się odblokowuje

---

## Stan Początkowy

```swift
// ViewModel state
viewModel.formData.selectedSpecies = nil         // ← brak species
viewModel.formData.race = ""                     // ← puste
viewModel.validationErrors.race = nil            // ← brak błędu
```

```swift
// Computed property
var raceTextFieldModel: ValidatedTextField.Model {
    ValidatedTextField.Model(
        label: "Race",
        placeholder: "e.g., Golden Retriever",
        errorMessage: nil,                              // ← nil (no error)
        isDisabled: formData.selectedSpecies == nil,   // ← TRUE! (disabled)
        keyboardType: .default,
        accessibilityID: "animalDescription.raceTextField.input"
    )
}
```

```swift
// View renderuje
ValidatedTextField(
    model: viewModel.raceTextFieldModel,  // ← Model z isDisabled = true
    text: $viewModel.formData.race        // ← Binding to ""
)
// TextField jest DISABLED (szare, nie można pisać)
```

---

## KROK 1: User Tapuje Species Dropdown

```swift
// View
DropdownView(
    model: viewModel.speciesDropdownModel,  // options: ["Dog", "Cat", "Bird", ...]
    selection: $viewModel.selectedSpeciesIndex  // ← Currently nil
)
```

User wybiera "Dog" (index 0)

---

## KROK 2: SwiftUI Binding Aktualizuje Index

```swift
// Picker internal logic:
viewModel.selectedSpeciesIndex = 0  // ← @Published! (triggers view update)
```

---

## KROK 3: onChange Handler Wywołuje się

```swift
.onChange(of: viewModel.selectedSpeciesIndex) { newIndex in
    if let index = newIndex {  // index = 0
        // Pobierz SpeciesTaxonomyOption z internal array
        let species = viewModel.speciesOptions[index]  // ← SpeciesTaxonomyOption(id: "dog", displayName: "Dog")
        
        // Call ViewModel method
        viewModel.selectSpecies(species)  // ← Kluczowa linia!
    }
}
```

---

## KROK 4: ViewModel selectSpecies() Mutuje State

```swift
func selectSpecies(_ species: SpeciesTaxonomyOption) {
    formData.selectedSpecies = species  // ← @Published formData zmienia się!
    formData.race = ""                  // ← Clear race (per spec)
    validationErrors.race = nil         // ← Clear error
}
```

**Co się dzieje**:
- `@Published var formData: FormData` zmienia się
- SwiftUI wykrywa zmianę
- View re-renderuje się

---

## KROK 5: View Re-renderuje się

SwiftUI zauważa że `formData` się zmieniło (@Published), więc:

```swift
// Computed property wywołuje się PONOWNIE
var raceTextFieldModel: ValidatedTextField.Model {
    ValidatedTextField.Model(
        label: "Race",
        placeholder: "e.g., Golden Retriever",
        errorMessage: nil,
        isDisabled: formData.selectedSpecies == nil,   // ← FALSE! (species = "Dog")
        keyboardType: .default,
        accessibilityID: "animalDescription.raceTextField.input"
    )
}
```

Teraz `isDisabled = false` bo `selectedSpecies != nil`!

---

## KROK 6: ValidatedTextField Dostaje Nowy Model

```swift
ValidatedTextField(
    model: viewModel.raceTextFieldModel,  // ← NOWY Model z isDisabled = false
    text: $viewModel.formData.race        // ← Binding do "" (nadal puste)
)
```

SwiftUI porównuje stary Model vs nowy Model:
- Stary: `isDisabled = true`
- Nowy: `isDisabled = false`
- **Różnica wykryta** → TextField re-renderuje się

---

## KROK 7: TextField Jest Odblokowany

```swift
TextField(model.placeholder, text: $text)
    .disabled(model.isDisabled)  // ← FALSE!
```

TextField przestaje być disabled:
- Zmienia kolor z szarego na czarny
- Cursor się pojawia
- User może pisać

---

## KROK 8: User Pisze "Golden Retriever"

```swift
// User types: "Golden Retriever"
TextField(model.placeholder, text: $text)  // $text = $viewModel.formData.race
```

Każda litera → SwiftUI aktualizuje binding:

```
User pisze "G"
    ↓
$viewModel.formData.race = "G"  (@Published!)
    ↓
View re-renderuje (ale model NIE zmienia się, więc TextField nie migocze)
    ↓
User pisze "o"
    ↓
$viewModel.formData.race = "Go"  (@Published!)
    ↓
... itd
```

---

## Pełny Flow - Timeline

```
[T0] Initial State:
     selectedSpecies = nil
     race = ""
     raceTextFieldModel.isDisabled = true
     → TextField DISABLED (szare)

[T1] User taps "Dog" in dropdown

[T2] selectedSpeciesIndex = 0  (@Published trigger)

[T3] onChange fires:
     viewModel.selectSpecies(speciesOptions[0])

[T4] selectSpecies() mutates:
     formData.selectedSpecies = SpeciesTaxonomyOption(dog)  (@Published trigger)
     formData.race = ""
     validationErrors.race = nil

[T5] View re-renders (formData changed)

[T6] raceTextFieldModel recomputes:
     isDisabled = (selectedSpecies == nil)  → FALSE
     
[T7] ValidatedTextField receives new model:
     old.isDisabled = true
     new.isDisabled = false
     → TextField re-renders as ENABLED

[T8] User can now type in race field!
```

---

## Kluczowe Punkty

### 1. Binding na Index

```swift
selection: $viewModel.selectedSpeciesIndex
```
- Dwukierunkowy binding
- User wybiera → index się zmienia → @Published trigger

### 2. onChange Handler

```swift
.onChange(of: viewModel.selectedSpeciesIndex) { newIndex in
    viewModel.selectSpecies(viewModel.speciesOptions[newIndex!])
}
```
- Mapuje index → domain model (SpeciesTaxonomyOption)
- Wywołuje ViewModel method

### 3. ViewModel Mutuje @Published State

```swift
func selectSpecies(_ species: SpeciesTaxonomyOption) {
    formData.selectedSpecies = species  // ← @Published!
    // ...
}
```
- Mutacja `formData` triggeruje view update

### 4. Computed Property Reaguje

```swift
var raceTextFieldModel: ValidatedTextField.Model {
    ValidatedTextField.Model(
        isDisabled: formData.selectedSpecies == nil  // ← Recomputes!
    )
}
```
- Automatycznie wywołuje się gdy `formData` się zmienia
- Zwraca nowy Model z `isDisabled = false`

### 5. SwiftUI Diff & Re-render

```swift
ValidatedTextField(model: viewModel.raceTextFieldModel, ...)
```
- SwiftUI widzi że model się zmienił
- TextField re-renderuje z nowym stanem (enabled)

---

## Dlaczego To Działa?

✅ **Reactive**: `@Published` automatycznie triggeruje view updates
✅ **Computed Properties**: `raceTextFieldModel` automatycznie recompute się
✅ **SwiftUI Diffing**: Porównuje stary vs nowy model, renderuje tylko różnice
✅ **Unidirectional Data Flow**: User action → ViewModel → State → View

---

## Debugowanie (gdyby nie działało)

```swift
func selectSpecies(_ species: SpeciesTaxonomyOption) {
    print("🔵 selectSpecies called: \(species.displayName)")
    
    formData.selectedSpecies = species
    print("🔵 formData.selectedSpecies = \(species.displayName)")
    
    formData.race = ""
    validationErrors.race = nil
    
    print("🔵 raceTextFieldModel.isDisabled = \(raceTextFieldModel.isDisabled)")
    // Should print: false
}
```

Jeśli coś nie działa, sprawdź:
1. Czy `@Published var formData` jest naprawdę `@Published`?
2. Czy `raceTextFieldModel` jest computed property (nie stored property)?
3. Czy ValidatedTextField używa `$viewModel.formData.race` (a nie lokalnego state)?
4. Czy onChange jest podłączony do DropdownView?

---

## Podsumowanie

**Sekwencja**:
1. User wybiera species
2. Binding aktualizuje index
3. onChange mapuje index → SpeciesTaxonomyOption
4. ViewModel mutuje `formData.selectedSpecies`
5. `@Published` triggeruje view update
6. Computed property `raceTextFieldModel` recompute
7. TextField dostaje nowy model z `isDisabled = false`
8. TextField się odblokowuje ✅

**Kluczowe elementy**:
- `@Published var formData`
- Computed property `raceTextFieldModel`
- `onChange` handler na dropdown
- Two-way binding `$viewModel.formData.race`

