import SwiftUI

/// Autonomous announcement cards list component.
/// Observes `AnnouncementCardsListViewModel` for state changes and renders appropriate UI.
///
/// **Autonomous Component Pattern**:
/// - Observes own ViewModel for state (loading, error, empty, success)
/// - Handles states internally (loading spinner, error view, empty state, list)
/// - Reusable in multiple contexts (full list with scroll, embedded without scroll)
/// - Parent View triggers load via parent ViewModel setting `listViewModel.query`
///
/// **Scroll Architecture**:
/// - `hasOwnScrollView: true` (default) - wraps content in ScrollView (for standalone usage)
/// - `hasOwnScrollView: false` - no ScrollView wrapper (for embedding in parent ScrollView)
///
/// **Does NOT trigger loading** - parent View's `.task` calls parent ViewModel's `loadData()`,
/// which then sets `listViewModel.query` to trigger the actual load.
struct AnnouncementCardsListView: View {
    @ObservedObject var viewModel: AnnouncementCardsListViewModel
    
    /// Model for empty state display
    let emptyStateModel: EmptyStateView.Model
    
    /// Base accessibility identifier for list elements
    let listAccessibilityId: String
    
    /// Whether this view manages its own scroll container.
    /// Set to `false` when embedding in a parent ScrollView (e.g., LandingPageView).
    /// Default: `true` (backwards compatible with existing usages).
    let hasOwnScrollView: Bool
    
    init(
        viewModel: AnnouncementCardsListViewModel,
        emptyStateModel: EmptyStateView.Model,
        listAccessibilityId: String,
        hasOwnScrollView: Bool = true
    ) {
        self.viewModel = viewModel
        self.emptyStateModel = emptyStateModel
        self.listAccessibilityId = listAccessibilityId
        self.hasOwnScrollView = hasOwnScrollView
    }
    
    // MARK: - Computed Properties
    
    private var isEmpty: Bool {
        viewModel.cardViewModels.isEmpty && !viewModel.isLoading && viewModel.errorMessage == nil
    }
    
    // MARK: - Body
    
    var body: some View {
        ZStack {
            // Background color matching app design
            Color(hex: "#FAFAFA").ignoresSafeArea()
            
            if viewModel.isLoading {
                LoadingView(model: LoadingView.Model(
                    message: L10n.AnnouncementList.Loading.message,
                    accessibilityIdentifier: "\(listAccessibilityId).loading"
                ))
            } else if let errorMessage = viewModel.errorMessage {
                ErrorView(model: ErrorView.Model(
                    title: L10n.AnnouncementList.Error.title,
                    message: errorMessage,
                    onRetry: {
                        viewModel.onRetryTapped()
                    },
                    accessibilityIdentifier: "\(listAccessibilityId).error"
                ))
            } else if isEmpty {
                EmptyStateView(model: emptyStateModel)
            } else {
                // Conditionally wrap in ScrollView based on hasOwnScrollView parameter
                if hasOwnScrollView {
                    ScrollView {
                        listContent
                    }
                    .accessibilityIdentifier("\(listAccessibilityId).list")
                } else {
                    // Parent provides scroll container (e.g., LandingPageView)
                    listContent
                        .accessibilityIdentifier("\(listAccessibilityId).list")
                }
            }
        }
    }
    
    // MARK: - Private Views
    
    /// Extracted list content for conditional scroll wrapping
    private var listContent: some View {
        LazyVStack(spacing: 8) {
            ForEach(viewModel.cardViewModels, id: \.id) { cardViewModel in
                AnnouncementCardView(viewModel: cardViewModel)
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 8)
    }
}

// MARK: - Preview

#Preview("Loading State") {
    let viewModel = AnnouncementCardsListViewModel(
        repository: FakePreviewRepository(),
        onAnnouncementTapped: { _ in }
    )
    // Query not set - list shows empty state (no loading triggered)
    AnnouncementCardsListView(
        viewModel: viewModel,
        emptyStateModel: .default,
        listAccessibilityId: "preview"
    )
}

// MARK: - Preview Repository

/// Fake repository for SwiftUI previews
private class FakePreviewRepository: AnnouncementRepositoryProtocol {
    func getAnnouncements(near location: Coordinate?, range: Int) async throws -> [Announcement] {
        []
    }
    
    func getPetDetails(id: String) async throws -> PetDetails {
        throw NSError(domain: "Preview", code: -1)
    }
    
    func createAnnouncement(data: CreateAnnouncementData) async throws -> AnnouncementResult {
        throw NSError(domain: "Preview", code: -1)
    }
    
    func uploadPhoto(announcementId: String, photo: PhotoAttachmentMetadata, managementPassword: String) async throws {
        throw NSError(domain: "Preview", code: -1)
    }
}

