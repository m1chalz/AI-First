import SwiftUI

/**
 * Main SwiftUI view for displaying list of animals.
 * Follows MVVM-C architecture with ViewModel managing state.
 *
 * Features:
 * - Scrollable list of animal cards (LazyVStack for performance)
 * - Loading indicator
 * - Error message display
 * - Empty state message
 * - "Report a Missing Animal" button (fixed at bottom)
 * - Reserved space for future search component
 *
 * Layout per FR-010: This is the primary entry point screen.
 *
 * - Parameter viewModel: ViewModel injected by coordinator
 */
struct AnimalListView: View {
    @ObservedObject var viewModel: AnimalListViewModel
    
    var body: some View {
        ZStack {
            Color(hex: "#FAFAFA") // Background color
                .ignoresSafeArea()
            
            // Content area with scrollable list
            if viewModel.isLoading {
                // Loading indicator
                LoadingView(model: .init(
                    message: L10n.AnimalList.Loading.message,
                    accessibilityIdentifier: "animalList.loading"
                ))
            } else if let errorMessage = viewModel.errorMessage {
                // Error message
                ErrorView(model: .init(
                    title: L10n.AnimalList.Error.title,
                    message: L10n.AnimalList.Error.prefix(errorMessage),
                    onRetry: {
                        Task {
                            await viewModel.loadAnimals()
                        }
                    },
                    accessibilityIdentifier: "animalList.error"
                ))
            } else if viewModel.isEmpty {
                // Empty state
                EmptyStateView(model: .default)
            } else {
                // Animal list
                ScrollView {
                    LazyVStack(spacing: 8) { // 8pt gap per Figma
                        // Reserved space for search component (FR-004)
                        Color.clear
                            .frame(height: 56) // 48-56pt height per spec
                        
                        // Animal cards
                        ForEach(viewModel.cardViewModels, id: \.id) { cardViewModel in
                            AnimalCardView(viewModel: cardViewModel)
                        }
                        
                        // Bottom spacing for floating buttons
                        Color.clear
                            .frame(height: 200)
                    }
                    .padding(.horizontal, 16)
                }
                .accessibilityIdentifier("animalList.list")
            }
            
            floatingButtonsSection
        }
        // User Story 3: Custom permission denied popup (recovery path)
        .alert(
            L10n.Location.Permission.Popup.title,
            isPresented: $viewModel.showPermissionDeniedAlert,
            actions: {
                Button(L10n.Location.Permission.Popup.Settings.button) {
                    viewModel.openSettings()  // Delegates to ViewModel → Coordinator (MVVM-C pattern)
                }
                .accessibilityIdentifier("startup.permissionPopup.goToSettings")
                
                Button(L10n.Location.Permission.Popup.Cancel.button, role: .cancel) {
                    Task {
                        await viewModel.continueWithoutLocation()
                    }
                }
                .accessibilityIdentifier("startup.permissionPopup.cancel")
            },
            message: {
                Text(L10n.Location.Permission.Popup.message)
                    .accessibilityIdentifier("startup.permissionPopup.message")
            }
        )
    }
    
    // MARK: - Floating Buttons Section
    
    /**
     * Floating action buttons section positioned at bottom-right.
     * Contains primary "Report Missing" button and optional "Report Found" button.
     */
    @ViewBuilder
    private var floatingButtonsSection: some View {
        VStack(alignment: .trailing, spacing: 30) {
            Spacer()
            
            // Report Found Animal button (optional - not in MVP mobile)
            // Uncomment when ready to enable
            /*
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: L10n.AnimalList.Button.reportFound,
                    style: .secondary
                ),
                action: { viewModel.reportFound() }
            )
            .accessibilityIdentifier("animalList.reportFoundButton")
            */
            
            // Report a Missing Animal button (primary action)
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: L10n.AnimalList.Button.reportMissing,
                    style: .primary,
                    icon: "ic_report_missing_animal"
                ),
                action: { viewModel.reportMissing() }
            )
            .accessibilityIdentifier("animalList.reportMissingButton")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottomTrailing)
        .padding(.trailing, 19)
        .padding(.bottom, 32)
    }
}

