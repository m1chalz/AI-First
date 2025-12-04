# Quickstart: Report Created Confirmation Screen

**Feature**: iOS Report Created Confirmation Screen  
**Phase**: 1 (Design & Contracts)  
**Date**: 2025-12-03  
**Target**: iOS (Swift + SwiftUI)

## Overview

This feature **updates existing `SummaryView`/`SummaryViewModel`** (currently placeholder with TODO comment) to display report confirmation UI. No new views or coordinators are created - we're implementing the "future iteration" mentioned in the TODO.

## Prerequisites

- Xcode 14+ with iOS 15+ SDK
- Existing `SummaryView`/`SummaryViewModel` at `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/`
- Existing `ReportMissingPetFlowState` at `/iosApp/iosApp/Features/ReportMissingPet/Models/`
- Existing `ToastView` component at `/iosApp/iosApp/Features/ReportMissingPet/Views/Components/ToastView.swift`
- Existing `ToastScheduler` service at `/iosApp/iosApp/Features/ReportMissingPet/Services/ToastScheduler.swift`
- Existing `ToastSchedulerFake` at `/iosApp/iosAppTests/Features/ReportMissingPet/Support/ToastSchedulerFake.swift`
- SwiftGen configured and operational (`swiftgen.yml`)
- Existing `Color+Hex` extension at `/iosApp/iosApp/FoundationAdditions/Color+Hex.swift`

---

## Implementation Steps

### Step 1: Add Localization Keys

**File**: `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`

```strings
/* Report Created Screen (Summary Step 5) */
"report_created.title" = "Report created";
"report_created.body_paragraph_1" = "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately.";
"report_created.body_paragraph_2" = "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address";
"report_created.code_copied" = "Code copied to clipboard";
```

**File**: `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings`

```strings
/* Report Created Screen (Summary Step 5) */
"report_created.title" = "Raport utworzony";
"report_created.body_paragraph_1" = "Twój raport został utworzony, a Twoje zagubione zwierzę zostało dodane do bazy danych. Jeśli Twój pupil zostanie odnaleziony, natychmiast otrzymasz powiadomienie.";
"report_created.body_paragraph_2" = "Jeśli chcesz usunąć swój raport z bazy danych, użyj kodu podanego poniżej w formularzu usuwania. Ten kod został również wysłany na Twój adres e-mail";
"report_created.code_copied" = "Skopiowano kod do schowka";
```

**Note**: Close button already has localization key `L10n.ReportMissingPet.Button.close` (reuse existing).

**Action**: Run SwiftGen to regenerate `L10n.swift`
```bash
swiftgen
```

---

### Step 2: Verify ReportMissingPetFlowState (Already on Branch)

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift`

**Verify these exist** (should already be on branch):

```swift
// MARK: - Step 5: Summary (Report Created)
@Published var managementPassword: String?

// In clear() method:
managementPassword = nil
```

**NO CHANGES NEEDED** - this property and clear logic should already exist on the branch. If not, add them, but expect them to be there.

---

### Step 3: Create Design Constants Extension

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryView+Constants.swift` (NEW FILE)

```swift
import SwiftUI

extension SummaryView {
    /// Design constants from Figma spec for report created confirmation UI
    struct Constants {
        // Gradient colors
        let gradientStartColor: Color = Color(hex: "#5C33FF")
        let gradientEndColor: Color = Color(hex: "#F84BA1")
        let glowColor: Color = Color(hex: "#FB64B6")
        let glowOpacity: Double = 0.2
        let glowBlurRadius: CGFloat = 20
        
        // Text colors
        let titleColor: Color = Color(hex: "#CC000000")  // 80% black (ARGB format: CC = 80% opacity)
        let bodyColor: Color = Color(hex: "#545F71")
        
        // Button colors (reuse existing blue)
        let buttonBackgroundColor: Color = Color(hex: "#155DFC")
        
        // Corner radii
        let passwordBackgroundCornerRadius: CGFloat = 10
        let buttonCornerRadius: CGFloat = 10
        
        // Typography
        let titleFont: Font = .system(size: 32, weight: .regular)
        let bodyFont: Font = .system(size: 16, weight: .regular)
        let bodyLineSpacing: CGFloat = 6.4  // 16px * 0.4 for 1.4 line height
        let passwordFont: Font = .custom("Arial", size: 60)
        let passwordKerning: CGFloat = -1.5
        let buttonFont: Font = .system(size: 18, weight: .semibold)
        
        // Spacing (FR-004)
        let horizontalPadding: CGFloat = 22
        let verticalSpacing: CGFloat = 24
        let topSafeAreaInset: CGFloat = 32
        let bottomSafeAreaInset: CGFloat = 16
        
        // Dimensions
        let passwordContainerWidth: CGFloat = 328
        let passwordContainerHeight: CGFloat = 90
        let buttonWidth: CGFloat = 327
        let buttonHeight: CGFloat = 52
    }
}
```

---

### Step 4: Update SummaryViewModel (Add Password Logic)

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryViewModel.swift`

**Add computed property after existing properties**:

```swift
// MARK: - Dependencies

private let toastScheduler: ToastSchedulerProtocol

// MARK: - Published Properties

@Published var showsCodeCopiedToast = false

// MARK: - Computed Properties

/// Management password for display (empty string if nil)
var displayPassword: String {
    flowState.managementPassword ?? ""
}

// MARK: - Actions (existing)

func handleSubmit() {
    onSubmit?()
}

// MARK: - Actions (new)

/// Copies management password to clipboard and shows toast confirmation
func copyPasswordToClipboard() {
    let password = displayPassword
    guard !password.isEmpty else { return }
    
    UIPasteboard.general.string = password
    toastScheduler.cancel()
    showsCodeCopiedToast = true
    toastScheduler.schedule(duration: 2.0) { [weak self] in
        Task { @MainActor in
            self?.showsCodeCopiedToast = false
        }
    }
}

// MARK: - Deinitialization

deinit {
    toastScheduler.cancel()
    print("deinit SummaryViewModel")
}
```

**Update init to accept toastScheduler**:
```swift
init(flowState: ReportMissingPetFlowState, toastScheduler: ToastSchedulerProtocol) {
    self.flowState = flowState
    self.toastScheduler = toastScheduler
}
```

---

### Step 5: Replace SummaryView Implementation

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryView.swift`

**Replace entire file contents**:

```swift
import SwiftUI

/// View for Summary screen (Step 5 - Report Created Confirmation).
/// Displays confirmation messaging, management password, and Close button.
struct SummaryView: View {
    // MARK: - Properties
    
    private let constants = Constants()
    @ObservedObject var viewModel: SummaryViewModel
    
    // MARK: - Body
    
    var body: some View {
        ZStack {
            Color.white
                .edgesIgnoringSafeArea(.all)
            
            VStack(spacing: 0) {
                // Scrollable content
                ScrollView {
                    VStack(spacing: constants.verticalSpacing) {
                        // Title
                        Text(L10n.ReportCreated.title)
                            .font(constants.titleFont)
                            .foregroundColor(constants.titleColor)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .accessibilityIdentifier("summary.title")
                        
                        // Body Paragraph 1
                        Text(L10n.ReportCreated.bodyParagraph1)
                            .font(constants.bodyFont)
                            .foregroundColor(constants.bodyColor)
                            .lineSpacing(constants.bodyLineSpacing)
                            .frame(maxWidth: 325, alignment: .leading)
                            .fixedSize(horizontal: false, vertical: true)
                            .accessibilityIdentifier("summary.bodyParagraph1")
                        
                        // Body Paragraph 2
                        Text(L10n.ReportCreated.bodyParagraph2)
                            .font(constants.bodyFont)
                            .foregroundColor(constants.bodyColor)
                            .lineSpacing(constants.bodyLineSpacing)
                            .frame(maxWidth: 325, alignment: .leading)
                            .fixedSize(horizontal: false, vertical: true)
                            .accessibilityIdentifier("summary.bodyParagraph2")
                        
                        // Management Password Module
                        Button(action: viewModel.copyPasswordToClipboard) {
                            passwordContainer
                        }
                        .buttonStyle(PlainButtonStyle())
                        
                        Spacer(minLength: 40)
                    }
                    .padding(.horizontal, constants.horizontalPadding)
                    .padding(.top, constants.topSafeAreaInset)
                }
                .safeAreaInset(edge: .bottom) {
                    Color.clear.frame(height: 120)
                }
                
                // Toast + Close Button (fixed at bottom - matching PhotoView pattern)
                VStack(spacing: 12) {
                    if viewModel.showsCodeCopiedToast {
                        ToastView(model: .init(text: L10n.ReportCreated.codeCopied))
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                            .accessibilityIdentifier("summary.toast")
                    }
                    
                    Button(action: viewModel.handleSubmit) {
                        Text(L10n.ReportMissingPet.Button.close)
                            .font(constants.buttonFont)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(constants.buttonBackgroundColor)
                            .cornerRadius(constants.buttonCornerRadius)
                    }
                    .accessibilityIdentifier("summary.closeButton")
                }
                .padding(.horizontal, 22)
                .padding(.vertical, 24)
                .frame(maxWidth: .infinity)
                .background(Color.white.ignoresSafeArea(edges: .bottom))
            }
        }
        .background(Color.white.ignoresSafeArea())
    }
    
    // MARK: - Subviews
    
    private var passwordContainer: some View {
        ZStack {
            // Gradient background
            RoundedRectangle(cornerRadius: constants.passwordBackgroundCornerRadius)
                .fill(
                    LinearGradient(
                        colors: [
                            constants.gradientStartColor,
                            constants.gradientEndColor
                        ],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
            
            // Glow overlay
            RoundedRectangle(cornerRadius: constants.passwordBackgroundCornerRadius)
                .fill(constants.glowColor.opacity(constants.glowOpacity))
                .blur(radius: constants.glowBlurRadius)
            
            // Password text
            Text(viewModel.displayPassword)
                .font(constants.passwordFont)
                .kerning(constants.passwordKerning)
                .foregroundColor(.white)
                .accessibilityIdentifier("summary.password")
        }
        .frame(width: constants.passwordContainerWidth, height: constants.passwordContainerHeight)
    }
    
// MARK: - Preview

#if DEBUG
struct SummaryView_Previews: PreviewProvider {
    private final class PreviewToastScheduler: ToastSchedulerProtocol {
        func schedule(duration: TimeInterval, handler: @escaping () -> Void) {}
        func cancel() {}
    }
    
    static var previews: some View {
        Group {
            // With password
            SummaryView(
                viewModel: {
                    let cache = PhotoAttachmentCacheFake()
                    let flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
                    flowState.managementPassword = "5216577"
                    let vm = SummaryViewModel(
                        flowState: flowState,
                        toastScheduler: PreviewToastScheduler()
                    )
                    return vm
                }()
            )
            .previewDisplayName("With Password")
            
            // Without password (nil)
            SummaryView(
                viewModel: {
                    let cache = PhotoAttachmentCacheFake()
                    let flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
                    flowState.managementPassword = nil
                    let vm = SummaryViewModel(
                        flowState: flowState,
                        toastScheduler: PreviewToastScheduler()
                    )
                    return vm
                }()
            )
            .previewDisplayName("Password Nil")
        }
    }
}
#endif
```

**Note**: If `PhotoAttachmentCacheFake` is not accessible in preview, use a mock or remove preview temporarily.

---

### Step 6: Verify Color+Hex Extension Exists

**Check file**: `/iosApp/iosApp/Features/Shared/Extensions/Color+Hex.swift`

**Note**: Extension already exists at `/iosApp/iosApp/FoundationAdditions/Color+Hex.swift`. Supports:
- 6-char RGB: `Color(hex: "#5C33FF")` 
- 8-char ARGB: `Color(hex: "#CC000000")` (alpha first: `#AARRGGBB`)

No action needed - extension already exists and supports required formats.

---

### Step 7: Minimal Coordinator Update

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/ReportMissingPetCoordinator.swift`

**ONLY change: Pass toastScheduler to SummaryViewModel constructor**

Find where `SummaryViewModel` is created and update init:

```swift
// BEFORE:
let viewModel = SummaryViewModel(flowState: flowState)

// AFTER:
let viewModel = SummaryViewModel(
    flowState: flowState,
    toastScheduler: ToastScheduler()  // Or inject existing instance if coordinator has one
)
```

**Note**: `managementPassword` is already being set elsewhere on this branch - don't add it again to avoid merge conflicts.

---

### Step 8: Update Unit Tests

**File**: `/iosApp/iosAppTests/Features/ReportMissingPet/Views/SummaryViewModelTests.swift`

**Update existing test file** (setUp + new tests):

```swift
import XCTest
@testable import PetSpot

final class SummaryViewModelTests: XCTestCase {
    private var flowState: ReportMissingPetFlowState!
    private var cache: PhotoAttachmentCacheFake!
    private var toastScheduler: ToastSchedulerFake!
    private var sut: SummaryViewModel!
    
    override func setUp() {
        super.setUp()
        cache = PhotoAttachmentCacheFake()
        toastScheduler = ToastSchedulerFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: cache)
        sut = SummaryViewModel(
            flowState: flowState,
            toastScheduler: toastScheduler
        )
    }
    
    override func tearDown() {
        sut = nil
        toastScheduler = nil
        flowState = nil
        cache = nil
        super.tearDown()
    }
    
    // MARK: - Management Password Display Tests
    
    func testDisplayPassword_whenPasswordIsNil_shouldReturnEmptyString() {
        // Given: FlowState with nil password
        flowState.managementPassword = nil
        
        // When: displayPassword is accessed
        let result = sut.displayPassword
        
        // Then: Returns empty string
        XCTAssertEqual(result, "")
    }
    
    func testDisplayPassword_whenPasswordExists_shouldReturnPassword() {
        // Given: FlowState with password
        let expectedPassword = "5216577"
        flowState.managementPassword = expectedPassword
        
        // When: displayPassword is accessed
        let result = sut.displayPassword
        
        // Then: Returns password
        XCTAssertEqual(result, expectedPassword)
    }
    
    // MARK: - Copy Password Tests
    
    func testCopyPasswordToClipboard_whenPasswordExists_shouldCopyAndShowToast() {
        // Given: FlowState with password
        let expectedPassword = "5216577"
        flowState.managementPassword = expectedPassword
        
        // When: copyPasswordToClipboard is called
        sut.copyPasswordToClipboard()
        
        // Then: Password is copied to clipboard
        XCTAssertEqual(UIPasteboard.general.string, expectedPassword)
        
        // And: Toast is shown
        XCTAssertTrue(sut.showsCodeCopiedToast)
        
        // And: Toast is scheduled to hide after 2 seconds
        XCTAssertEqual(toastScheduler.scheduledDurations, [2.0])
    }
    
    func testCopyPasswordToClipboard_whenPasswordIsNil_shouldNotCopy() {
        // Given: FlowState with nil password
        flowState.managementPassword = nil
        UIPasteboard.general.string = "previous content"
        
        // When: copyPasswordToClipboard is called
        sut.copyPasswordToClipboard()
        
        // Then: Clipboard is unchanged
        XCTAssertEqual(UIPasteboard.general.string, "previous content")
        
        // And: Toast is not shown
        XCTAssertFalse(sut.showsCodeCopiedToast)
    }
}
```

**Note**: If `SummaryViewModelTests` doesn't exist yet, create it following existing test file patterns.

**Run Tests**: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`

---

### Step 9: Add E2E Tests

**File**: `/e2e-tests/mobile/screens/SummaryScreen.ts` (UPDATE if exists, CREATE if not)

```typescript
import { WebdriverIOElement } from '@wdio/types';

export class SummaryScreen {
    // Locators (iOS accessibility identifiers)
    private get title(): WebdriverIOElement {
        return $('~summary.title');
    }

    private get bodyParagraph1(): WebdriverIOElement {
        return $('~summary.bodyParagraph1');
    }

    private get bodyParagraph2(): WebdriverIOElement {
        return $('~summary.bodyParagraph2');
    }

    private get password(): WebdriverIOElement {
        return $('~summary.password');
    }

    private get closeButton(): WebdriverIOElement {
        return $('~summary.closeButton');
    }

    // Actions
    async waitForScreenToLoad(): Promise<void> {
        await this.title.waitForDisplayed({ timeout: 5000 });
    }

    async getTitleText(): Promise<string> {
        return await this.title.getText();
    }

    async getPasswordText(): Promise<string> {
        return await this.password.getText();
    }

    async tapPassword(): Promise<void> {
        await this.password.click();
    }

    async tapCloseButton(): Promise<void> {
        await this.closeButton.click();
    }

    async isPasswordDisplayed(): Promise<boolean> {
        return await this.password.isDisplayed();
    }

    async isCloseButtonDisplayed(): Promise<boolean> {
        return await this.closeButton.isDisplayed();
    }
}
```

**File**: `/e2e-tests/mobile/specs/report-created-confirmation.spec.ts` (NEW)

```typescript
import { SummaryScreen } from '../screens/SummaryScreen';
import { createMissingAnimalReport, verifyHomeScreenDisplayed } from '../steps/report-helpers';

describe('Report Created Confirmation (Summary Screen)', () => {
    const summaryScreen = new SummaryScreen();

    beforeEach(async () => {
        // Setup: Navigate through report flow to reach summary screen
        await createMissingAnimalReport();
    });

    it('should display confirmation messaging and management password', async () => {
        // Given - report submission completed
        await summaryScreen.waitForScreenToLoad();

        // When - screen is displayed
        const title = await summaryScreen.getTitleText();
        const passwordDisplayed = await summaryScreen.isPasswordDisplayed();

        // Then - confirmation messaging and password are visible
        expect(title).toBe('Report created');
        expect(passwordDisplayed).toBe(true);
    });

    it('should copy management password to clipboard when tapped', async () => {
        // Given - summary screen with password
        await summaryScreen.waitForScreenToLoad();
        const passwordBefore = await summaryScreen.getPasswordText();

        // When - user taps password
        await summaryScreen.tapPassword();

        // Then - password interaction completes (toast should appear)
        expect(passwordBefore).not.toBe('');
        // Note: Actual clipboard verification requires device-specific APIs
    });

    it('should dismiss flow when Close button is tapped', async () => {
        // Given - summary screen displayed
        await summaryScreen.waitForScreenToLoad();

        // When - user taps Close button
        await summaryScreen.tapCloseButton();

        // Then - flow is dismissed (verify home screen or dashboard)
        await verifyHomeScreenDisplayed();
    });
});
```

**Run E2E Tests**: 
```bash
cd e2e-tests
npm run test:mobile:ios
```

---

## Verification Checklist

- [ ] Localization keys added to both `en.strings` and `pl.strings` with `report_created.*` prefix
- [ ] SwiftGen regenerated (`swiftgen` command executed)
- [ ] `ReportMissingPetFlowState` updated with `@Published var managementPassword: String?` (already on branch)
- [ ] `ReportMissingPetFlowState.clear()` updated to clear managementPassword (already on branch)
- [ ] `SummaryView+Constants.swift` created with Figma design constants
- [ ] `Color+Hex.swift` extension verified (already exists at `/iosApp/iosApp/FoundationAdditions/Color+Hex.swift`)
- [ ] `SummaryView.swift` replaced with report confirmation UI (no more placeholder)
- [ ] `SummaryViewModel.swift` updated with `displayPassword` computed property and `copyPasswordToClipboard()` action
- [ ] `SummaryViewModel` init updated to accept `toastScheduler: ToastSchedulerProtocol`
- [ ] `SummaryView` uses `viewModel.showsCodeCopiedToast` to display toast (matching PhotoView pattern)
- [ ] Toast displayed in same VStack as Close button (above button)
- [ ] Test identifiers added to all interactive elements (`.accessibilityIdentifier()`)
- [ ] Coordinator: SummaryViewModel init updated to pass `toastScheduler` parameter
- [ ] Unit tests updated ONLY in `SummaryViewModelTests` (password display, copy logic)
- [ ] FlowState and its tests NOT modified (already on branch - avoid conflicts)
- [ ] E2E tests created for all 3 user stories (confirmation, copy, close)
- [ ] Visual QA: Screen matches Figma design (typography, colors, spacing)

---

## Common Issues & Solutions

### Issue 1: SwiftGen L10n not found

**Symptom**: `L10n.ReportCreated.title` shows "Cannot find 'L10n' in scope"

**Solution**: 
1. Run `swiftgen` from repo root to regenerate `L10n.swift`
2. Verify localization keys use correct naming convention (check swiftgen.yml config)
3. Rebuild project

---

### Issue 2: Gradient not displaying correctly

**Symptom**: Password container shows solid color instead of gradient

**Solution**: Verify `Color(hex:)` extension parses hex strings correctly. Check that gradient colors are in `#RRGGBB` format (no alpha channel).

---

### Issue 3: Toast not appearing after clipboard copy

**Symptom**: Password copied but no toast confirmation

**Solution**: 
- Verify `showToast` state is toggled correctly
- Check that toast auto-hide delay (2 seconds) is sufficient
- If using existing `ToastView` component, verify integration pattern

---

### Issue 4: Close button doesn't dismiss flow

**Symptom**: Tapping Close button does nothing or crashes

**Solution**: 
- Verify `viewModel.handleSubmit()` is wired correctly (should already work)
- Check coordinator's `onSubmit` closure clears flowState and navigates correctly
- Ensure `flowState.clear()` includes `managementPassword = nil`

---

### Issue 5: Preview crashes or doesn't compile

**Symptom**: Xcode preview shows error for `SummaryView_Previews`

**Solution**:
- If `PhotoAttachmentCacheFake` is not accessible, create a simple mock or remove preview temporarily
- Verify all required dependencies are available in preview context

---

## Next Steps

After implementation:

1. **Manual QA**: Test on physical device (iPhone 16) to verify:
   - Typography matches Figma (sizes, weights, colors)
   - Gradient and glow effect render correctly
   - Clipboard copy works and toast appears
   - Close button dismisses flow and clears state

2. **Localization Testing**:
   - Switch device language to Polish
   - Verify all text displays correctly in Polish
   - Verify layout doesn't break with longer Polish text

3. **Edge Case Testing**:
   - Test with nil password (should display empty string, no crash)
   - Test with empty string password (should display empty string)
   - Test with very long password (20+ characters - should display without overflow)

4. **Code Review**:
   - Verify constitution compliance (MVVM pattern, test identifiers, Given-When-Then tests)
   - Verify 80%+ test coverage for ViewModel logic and FlowState
   - Verify all Figma design specs implemented exactly

---

## References

- **Feature Spec**: `/specs/044-ios-report-created-screen/spec.md`
- **Data Model**: `/specs/044-ios-report-created-screen/data-model.md`
- **Research**: `/specs/044-ios-report-created-screen/research.md`
- **Figma Design**: [Report created screen](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8193&m=dev)
- **Constitution**: `/.specify/memory/constitution.md` (Principle XI - iOS MVVM-C Architecture)
- **Existing Components**: 
  - `SummaryView.swift` (currently placeholder)
  - `SummaryViewModel.swift` (minimal implementation)
  - `ReportMissingPetFlowState.swift` (shared flow state)
