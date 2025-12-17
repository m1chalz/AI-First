import SwiftUI

/// Landing page view for Home tab displaying recent pet announcements.
/// Composes `AnnouncementCardsListView` (autonomous component) with landing page context.
///
/// **Composite View Pattern**:
/// - List is only part of the screen (future: header, sections, promotions, etc.)
/// - Delegates list rendering to `AnnouncementCardsListView`
/// - Child view handles its own empty/error/loading states internally
///
/// **Navigation**:
/// - NO NavigationView - coordinator manages UINavigationController
/// - Navigation bar title set by coordinator via UIHostingController
///
/// **Loading Flow**:
/// 1. View appears → `.task { await viewModel.loadData() }`
/// 2. Parent ViewModel fetches location and sets `listViewModel.query`
/// 3. Shows permission popup if needed (once per session)
/// 4. Child ViewModel loads announcements and updates state
/// 5. `AnnouncementCardsListView` observes child ViewModel and renders UI
struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        // NO NavigationView - coordinator manages UINavigationController
        AnnouncementCardsListView(
            viewModel: viewModel.listViewModel,
            emptyStateModel: viewModel.emptyStateModel,
            listAccessibilityId: viewModel.listAccessibilityId
        )
        .task {
            await viewModel.loadData()
        }
        // Custom permission denied popup (recovery path)
        .alert(
            L10n.Location.Permission.Popup.title,
            isPresented: $viewModel.showPermissionDeniedAlert,
            actions: {
                Button(L10n.Location.Permission.Popup.Settings.button) {
                    viewModel.openSettings()  // Delegates to ViewModel → Coordinator (MVVM-C pattern)
                }
                .accessibilityIdentifier("startup.permissionPopup.goToSettings")
                
                Button(L10n.Location.Permission.Popup.Cancel.button, role: .cancel) {
                    viewModel.continueWithoutLocation()
                }
                .accessibilityIdentifier("startup.permissionPopup.cancel")
            },
            message: {
                Text(L10n.Location.Permission.Popup.message)
                    .accessibilityIdentifier("startup.permissionPopup.message")
            }
        )
    }
}

// MARK: - Preview

#Preview("Landing Page") {
    LandingPageView(
        viewModel: LandingPageViewModel(
            repository: PreviewAnnouncementRepository(),
            locationHandler: LocationPermissionHandler(locationService: LocationService()),
            onAnnouncementTapped: { id in
                print("Tapped announcement: \(id)")
            }
        )
    )
}

// MARK: - Preview Repository

/// Preview repository for SwiftUI previews
private class PreviewAnnouncementRepository: AnnouncementRepositoryProtocol {
    func getAnnouncements(near location: Coordinate?, range: Int) async throws -> [Announcement] {
        // Return sample announcements for preview
        return [
            Announcement(
                id: "preview-1",
                name: "Buddy",
                photoUrl: "https://example.com/dog.jpg",
                coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
                species: .dog,
                breed: "Golden Retriever",
                gender: .male,
                status: .active,
                lastSeenDate: "20/12/2025",
                description: "Friendly golden retriever",
                email: "owner@example.com",
                phone: "+48123456789"
            )
        ]
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

