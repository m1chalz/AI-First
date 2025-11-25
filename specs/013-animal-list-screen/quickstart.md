# Quickstart: Android Animal List Screen Layout Update

## Goal

Refresh the Android `AnimalListScreen` Jetpack Compose UI to match the new Figma design "Missing animals list app" (node-id=297-7556), without changing behaviour, navigation, or backend integration.

## Key Visual Changes

| Element | Before | After |
|---------|--------|-------|
| Title | "Missing animals list" (centered) | "PetSpot" (left-aligned, 32px Hind) |
| Card layout | Vertical stack | Three-column (photo \| info \| status) |
| Card border | 4dp radius, shadow | 14dp radius, 1px border |
| Button | Full-width bottom bar | Floating pill-shaped |

## Steps for Implementation

### 1. Review design and spec

- Open Figma frame "Missing animals list app" (node-id=297-7556)
- Read `specs/013-animal-list-screen/spec.md` and `data-model.md` to confirm scope

### 2. Update AnimalCard.kt

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalCard.kt`

Key changes:
```kotlin
// Update Card shape and border
Card(
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, Color(0xFFE5E9EC)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    // ...
)

// Restructure to three-column Row layout
Row {
    // Left: 64dp circular photo
    Box(modifier = Modifier.size(64.dp).clip(CircleShape)) { ... }
    
    // Middle: Info column
    Column {
        // Row 1: Location icon + name + "•" + distance
        Row {
            Icon(Icons.Default.LocationOn, ...)
            Text("Central Park", color = Color(0xFF4A5565), fontSize = 13.sp)
            Text(" • ", ...)
            Text("2.5 km", ...)
        }
        // Row 2: Species + "•" + breed
        Row {
            Text("Dog", color = Color(0xFF101828), fontSize = 14.sp)
            Text(" • ", ...)
            Text("Golden Retriever", ...)
        }
    }
    
    // Right: Status and date
    Column(horizontalAlignment = Alignment.End) {
        StatusBadge(status) // Red for MISSING, Blue for FOUND
        Text(date, color = Color(0xFF6A7282), fontSize = 14.sp)
    }
}

// Update test tag
modifier = modifier.testTag("animalList.cardItem")
```

### 3. Update AnimalListContent.kt

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListContent.kt`

Key changes:
```kotlin
// Remove TopAppBar, use Box with Column layout
Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.padding(horizontal = 23.dp, top = 44.dp)
    ) {
        // Title: "PetSpot" left-aligned
        Text(
            text = "PetSpot",
            fontFamily = hindFontFamily,
            fontSize = 32.sp,
            color = Color(0xFF000000).copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Animal list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(animals) { animal ->
                AnimalCard(animal = animal, onClick = { onAnimalClick(animal.id) })
            }
        }
    }
    
    // Floating button at bottom center
    FloatingReportButton(
        onClick = onReportMissing,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 32.dp)
            .testTag("animalList.reportButton")
    )
}
```

### 4. Create FloatingReportButton composable

```kotlin
@Composable
private fun FloatingReportButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF155DFC)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        ),
        contentPadding = PaddingValues(horizontal = 21.dp, vertical = 21.dp)
    ) {
        Text(
            text = "Report a Missing Animal",
            fontFamily = hindFontFamily,
            fontSize = 14.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Pets, // or custom icon
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
```

### 5. Add Hind font

Add Hind font family to the project:
- Download from Google Fonts
- Add to `res/font/` directory
- Create FontFamily reference:

```kotlin
val hindFontFamily = FontFamily(
    Font(R.font.hind_regular, FontWeight.Normal)
)
```

### 6. Update test tags

Ensure these test tags are present:
- `animalList.cardItem` - on each animal card
- `animalList.reportButton` - on the floating button

### 7. Preserve existing states

Keep loading, empty, and error state handling unchanged:
- Loading: `CircularProgressIndicator` centered
- Empty: Empty state message
- Error: Error message with retry button

### 8. Testing

```bash
# Run Android unit tests
./gradlew :composeApp:testDebugUnitTest

# Run mobile E2E tests
npm run test:mobile:android
```

## Color Reference

| Name | Hex | Usage |
|------|-----|-------|
| Primary Blue | #155DFC | Button, FOUND badge |
| Missing Red | #FF0000 | MISSING badge |
| Text Dark | #101828 | Species/breed |
| Text Gray | #4A5565 | Location |
| Text Light | #6A7282 | Date |
| Border | #E5E9EC | Card border |

## Typography Reference

| Element | Font | Size | Color |
|---------|------|------|-------|
| Title | Hind | 32px | rgba(0,0,0,0.8) |
| Location | Arial | 13px | #4A5565 |
| Species | Arial | 14px | #101828 |
| Status | Arial | 13px | White |
| Date | Arial | 14px | #6A7282 |
| Button | Hind | 14px | White |
