import SwiftUI

/**
 * Main SwiftUI view for displaying list of announcements.
 * Composes `AnnouncementCardsListView` (autonomous component) with feature-specific UI.
 *
 * **Composite View Pattern**:
 * - Delegates list rendering to `AnnouncementCardsListView` (handles loading/error/empty states)
 * - Adds feature-specific overlays: floating buttons
 * - Parent ViewModel handles location fetching and button callbacks
 *
 * Features:
 * - Scrollable list of announcement cards (via AnnouncementCardsListView)
 * - Loading indicator (via AnnouncementCardsListView)
 * - Error message display (via AnnouncementCardsListView)
 * - Empty state message (via AnnouncementCardsListView)
 * - "Report a Missing Animal" button (fixed at bottom)
 *
 * **Note**: Permission popup is handled by LandingPage (Home tab), not here.
 *
 * Layout per FR-010: This is the primary entry point screen.
 *
 * - Parameter viewModel: ViewModel injected by coordinator
 */
struct AnnouncementListView: View {
    @ObservedObject var viewModel: AnnouncementListViewModel
    
    var body: some View {
        ZStack {
            // Autonomous list component handles loading/error/empty/list states
            AnnouncementCardsListView(
                viewModel: viewModel.listViewModel,
                emptyStateModel: viewModel.emptyStateModel,
                listAccessibilityId: viewModel.listAccessibilityId
            )
            
            // Feature-specific overlay: floating action buttons
            floatingButtonsSection
        }
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
            
            // Report Found Animal button
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: L10n.AnnouncementList.Button.reportFound,
                    style: .secondary
                ),
                action: { viewModel.reportFound() }
            )
            .accessibilityIdentifier("animalList.reportFoundButton")
            
            // Report a Missing Animal button (primary action)
            FloatingActionButton(
                model: FloatingActionButtonModel(
                    title: L10n.AnnouncementList.Button.reportMissing,
                    style: .primary,
                    iconSource: .asset("ic_report_missing_animal"),
                    iconPosition: .right
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

