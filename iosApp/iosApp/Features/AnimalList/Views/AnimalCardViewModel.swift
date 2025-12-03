import Foundation

/**
 * Actions that can be performed on an announcement card.
 * Used for communication between card ViewModel and parent ViewModel.
 */
enum AnnouncementAction {
    case selected(String)
    // Future actions can be added here (e.g., toggleFavorite, share)
}

/**
 * ViewModel for a single announcement card.
 * Manages card-specific state and handles user interactions.
 * Transforms raw Announcement data into presentation-ready properties.
 * Communicates with parent ViewModel via action callback.
 *
 * Note: Instance is owned and cached by AnnouncementListViewModel.
 * Uses @ObservedObject in view (not @StateObject) since lifecycle is managed externally.
 */
@MainActor
class AnnouncementCardViewModel: ObservableObject {
    // MARK: - Published Properties
    
    /// Card-specific UI state (e.g., for future animations, expanded state)
    @Published var isExpanded = false
    
    // MARK: - Internal Properties (accessible for coordinator/parent)
    
    /// Raw announcement data (internal - views should use computed properties)
    @Published private(set) var announcement: Announcement
    
    // MARK: - Computed Properties (Presentation Layer)
    
    /// Photo URL
    var photoUrl: String {
        announcement.photoUrl
    }
    
    /// Formatted location text with coordinates
    var locationText: String {
        let lat = announcement.coordinate.latitude
        let lon = announcement.coordinate.longitude
        let latDir = lat >= 0 ? "N" : "S"
        let lonDir = lon >= 0 ? "E" : "W"
        return String(format: "%.4f° %@, %.4f° %@", abs(lat), latDir, abs(lon), lonDir)
    }
    
    /// Species display name
    var speciesName: String {
        announcement.species.displayName
    }
    
    /// Breed name
    var breedName: String {
        announcement.breed ?? "-"
    }
    
    /// Status badge text
    var statusText: String {
        announcement.status.displayName
    }
    
    /// Status badge color as hex string (e.g., "#FF6B6B")
    var statusColorHex: String {
        announcement.status.badgeColorHex
    }
    
    /// Formatted date text
    var dateText: String {
        announcement.lastSeenDate
        // TODO: Format properly when date format is finalized
        // DateFormatter.shared.format(announcement.lastSeenDate)
    }
    
    /// Unique identifier for SwiftUI ForEach
    var id: String {
        announcement.id
    }
    
    // MARK: - Private Properties
    
    /// Callback to communicate actions to parent ViewModel
    private let onAction: (AnnouncementAction) -> Void
    
    // MARK: - Initialization
    
    /**
     * Initializes card ViewModel with announcement data and action callback.
     *
     * - Parameter announcement: Announcement entity to display
     * - Parameter onAction: Callback invoked when user performs actions on card
     */
    init(announcement: Announcement, onAction: @escaping (AnnouncementAction) -> Void) {
        self.announcement = announcement
        self.onAction = onAction
    }
    
    // MARK: - Public Methods
    
    /**
     * Updates card with new announcement data.
     * Called by parent ViewModel when data refreshes.
     *
     * - Parameter announcement: Updated announcement entity
     */
    func update(with announcement: Announcement) {
        self.announcement = announcement
    }
    
    /**
     * Handles card tap gesture.
     * Notifies parent ViewModel via action callback.
     */
    func handleTap() {
        onAction(.selected(announcement.id))
    }
}

