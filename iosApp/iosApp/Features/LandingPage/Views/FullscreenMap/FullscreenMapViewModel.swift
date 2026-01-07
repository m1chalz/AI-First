import Foundation
import MapKit

/// ViewModel for fullscreen map screen with pin loading.
/// Receives user location and repository via constructor injection from coordinator.
///
/// **Features**:
/// - Fetches announcements from repository and displays as pins on map
/// - Silent error handling - failures keep existing pins without user notification
/// - Task cancellation for rapid gesture scenarios
///
/// **Properties**:
/// - `mapRegion`: Pre-calculated from user location for city-level zoom (~10km radius)
/// - `legendModel`: Legend-only configuration (no title - navigation bar provides context)
/// - `pins`: Array of map pins derived from announcements
/// - `isLoading`: Internal loading state (no UI indicator per spec)
@MainActor
class FullscreenMapViewModel: ObservableObject {
    /// Map region centered on user's location with city-level zoom (~10km radius).
    let mapRegion: MKCoordinateRegion
    
    /// Legend configuration for fullscreen map (reuses existing component).
    /// Shows Missing (red) and Found (blue) legend items without title.
    let legendModel: MapSectionHeaderView.Model
    
    /// Displayed pins on map. Updated after fetching announcements.
    @Published private(set) var pins: [MapPin] = []
    
    /// Internal loading state. No visible indicator per spec FR-006.
    @Published private(set) var isLoading = false
    
    /// Repository for fetching announcements.
    private let repository: AnnouncementRepositoryProtocol
    
    /// Current fetch task for cancellation support.
    private var fetchTask: Task<Void, Never>?
    
    /// Creates ViewModel with dependencies injected by coordinator.
    ///
    /// - Parameters:
    ///   - userLocation: User's current location (from landing page)
    ///   - repository: Repository for fetching announcements (from HomeCoordinator)
    init(userLocation: Coordinate, repository: AnnouncementRepositoryProtocol) {
        self.mapRegion = userLocation.mapRegion()
        self.legendModel = .fullscreenMap()
        self.repository = repository
    }
    
    // MARK: - Pin Loading
    
    /// Loads pins for the initial map region on view appear.
    /// Called via `.task` modifier when view loads.
    func loadPins() async {
        await fetchPins(for: mapRegion)
    }
    
    /// Handles map region change after pan/zoom gesture ends.
    /// Called via `.onMapCameraChange(frequency: .onEnd)` modifier.
    ///
    /// - Parameter region: New visible map region after gesture
    func handleRegionChange(_ region: MKCoordinateRegion) async {
        await fetchPins(for: region)
    }
    
    /// Fetches pins for a given region from repository.
    /// Cancels any in-flight task before starting new fetch.
    ///
    /// - Parameter region: Map region to fetch pins for
    private func fetchPins(for region: MKCoordinateRegion) async {
        fetchTask?.cancel()
        
        fetchTask = Task {
            isLoading = true
            defer { isLoading = false }
            
            let center = Coordinate(
                latitude: region.center.latitude,
                longitude: region.center.longitude
            )
            
            do {
                let announcements = try await repository.getAnnouncements(
                    near: center,
                    range: region.radiusInKilometers
                )
                
                // Map all announcements to pins (no status filtering per spec)
                let newPins = announcements.map { MapPin(from: $0) }
                
                // Instant update without animation per spec FR-010
                self.pins = newPins
                print("Pin fetch count: \(newPins.count)")
            } catch {
                // Silent failure - keep existing pins per spec FR-011
                print("Pin fetch error: \(error)")
            }
        }
        
        await fetchTask?.value
    }
}
