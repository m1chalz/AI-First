import SwiftUI

/// Landing page view for Home tab displaying hero panel, map preview, list header, and recent pet announcements.
/// Uses single ScrollView for continuous scrolling of all sections.
///
/// **Layout (top to bottom)**:
/// 1. HeroPanelView - "Find Your Pet" title + "Lost Pet" / "Found Pet" buttons
/// 2. MapPreviewView - Static map centered on user location (16:9 aspect ratio)
/// 3. ListHeaderRowView - "Recent Reports" title + "View All" action
/// 4. AnnouncementCardsListView - announcement cards (inline content, no nested scroll)
///
/// **Scroll Architecture**:
/// - Single outer ScrollView with LazyVStack containing all sections
/// - No nested scroll regions (hero, header, and list scroll together)
/// - Loading, error, and empty states render inline in scroll content
///
/// **Navigation**:
/// - NO NavigationView - coordinator manages UINavigationController
/// - Navigation bar title set by coordinator via UIHostingController
/// - Hero button taps trigger tab navigation via ViewModel closures
///
/// **Loading Flow**:
/// 1. View appears → `.task { await viewModel.loadData() }`
/// 2. Parent ViewModel fetches location and sets `listViewModel.query`
/// 3. Shows permission popup if needed (once per session)
/// 4. Child ViewModel loads announcements and updates state
/// 5. `AnnouncementCardsListView` observes child ViewModel and renders UI inline
struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        // NO NavigationView - coordinator manages UINavigationController
        // Single ScrollView for continuous scrolling of all sections
        ScrollView {
            LazyVStack(spacing: 0) {
                // Hero panel - "Find Your Pet" + action buttons
                HeroPanelView(
                    model: .landingPage(
                        onLostPetTap: {
                            viewModel.onSwitchToLostPetTab?(nil)
                        },
                        onFoundPetTap: {
                            viewModel.onSwitchToFoundPetTab?()
                        }
                    )
                )
                
                // Map preview - static map centered on user location
                MapPreviewView(model: viewModel.mapPreviewModel)
                    .aspectRatio(16/9, contentMode: .fit)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 16)
                
                // List header row - "Recent Reports" / "View All"
                ListHeaderRowView(
                    model: .recentReports(
                        onViewAllTap: {
                            viewModel.onSwitchToLostPetTab?(nil)
                        }
                    )
                )
                
                // Pass hasOwnScrollView: false to disable nested ScrollView
                // Parent ScrollView (this view) handles scrolling
                AnnouncementCardsListView(
                    viewModel: viewModel.listViewModel,
                    emptyStateModel: viewModel.emptyStateModel,
                    listAccessibilityId: viewModel.listAccessibilityId,
                    hasOwnScrollView: false
                )
            }
        }
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
