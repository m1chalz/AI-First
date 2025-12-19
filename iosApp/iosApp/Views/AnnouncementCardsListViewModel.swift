import Foundation

/// Autonomous ViewModel for announcement cards list component.
/// Manages list state (loading, error, success) and fetches announcements from repository.
///
/// **Autonomous Component Pattern**:
/// - Self-contained state and behavior (no external state management)
/// - Configured via `AnnouncementListQuery` (limit, sortBy, location)
/// - Reusable in multiple contexts (full list, landing page, search, etc.)
/// - Parent ViewModels set `query` property to trigger loads with updated configuration
///
/// **Public API**:
/// - `query`: Setting triggers automatic reload (called by parent ViewModel)
/// - `onRetryTapped()`: Handles retry from error screen (calls private reload)
///
/// **Private Implementation**:
/// - `loadAnnouncements()` is private - only triggered via `query` setter or `onRetryTapped()`
@MainActor
class AnnouncementCardsListViewModel: ObservableObject {
    // MARK: - Published Properties (UI State)
    
    /// Array of card ViewModels (filtered/sorted per query)
    @Published private(set) var cardViewModels: [AnnouncementCardViewModel] = []
    
    /// Loading indicator state
    @Published private(set) var isLoading: Bool = false
    
    /// Error message text (nil when no error)
    @Published private(set) var errorMessage: String?
    
    // MARK: - Dependencies
    
    private let repository: AnnouncementRepositoryProtocol
    
    /// Callback for card tap events
    private let onAnnouncementTapped: (String) -> Void
    
    // MARK: - Query Configuration
    
    /// Query configuration - nil means "don't fetch yet".
    /// Use `loadWithQuery(_:)` to set query and load data.
    private var query: AnnouncementListQuery?
    
    // MARK: - Task Management
    
    /// Active load task for cancellation support
    private var loadTask: Task<Void, Never>?
    
    // MARK: - Initialization
    
    /// Creates autonomous list ViewModel with repository.
    ///
    /// - Parameters:
    ///   - repository: Repository for fetching announcements
    ///   - onAnnouncementTapped: Closure invoked when user taps announcement card
    ///
    /// - Note: Set `query` property to start loading. Until then, list remains empty.
    init(
        repository: AnnouncementRepositoryProtocol,
        onAnnouncementTapped: @escaping (String) -> Void
    ) {
        self.repository = repository
        self.onAnnouncementTapped = onAnnouncementTapped
    }
    
    deinit {
        loadTask?.cancel()
    }
    
    // MARK: - Public Methods
    
    /// Sets query and loads announcements.
    /// Awaits until loading completes, so caller can use results immediately after.
    ///
    /// - Parameter query: Query configuration for filtering and sorting
    func loadWithQuery(_ query: AnnouncementListQuery) async {
        loadTask?.cancel()
        self.query = query
        await loadAnnouncements()
    }
    
    // MARK: - User Actions
    
    /// Handles retry button tap from error screen.
    func onRetryTapped() {
        reload()
    }
    
    // MARK: - Private Methods
    
    /// Reloads announcements with current query.
    /// Does nothing if query is not set.
    private func reload() {
        guard query != nil else { return }
        loadTask?.cancel()
        loadTask = Task { await loadAnnouncements() }
    }
    
    /// Loads announcements from repository, applies query filters/sorting, and creates card ViewModels.
    /// Does nothing if query is nil.
    private func loadAnnouncements() async {
        guard let query = query else {
            cardViewModels = []
            return
        }
        
        isLoading = true
        errorMessage = nil
        
        do {
            // Fetch announcements with location from query (parent prepared location)
            // TODO: let this method take query (see in which layer query should be)
            let allAnnouncements = try await repository.getAnnouncements(near: query.location)
            
            // Check for cancellation after async operation
            try Task.checkCancellation()
            
            // Apply query filters and sorting
            let processedAnnouncements = applyQuery(query, to: allAnnouncements)
            
            // Convert to card ViewModels
            updateCardViewModels(with: processedAnnouncements)
            
            isLoading = false
        } catch is CancellationError {
            // Task cancelled - normal, don't show error
        } catch {
            errorMessage = L10n.AnnouncementList.Error.loadingFailed
            cardViewModels = []
            isLoading = false
        }
    }
    
    /// Applies query configuration (filtering, sorting) to announcements.
    ///
    /// - Parameters:
    ///   - query: Query configuration with sorting and limit options
    ///   - announcements: Raw announcements from repository
    /// - Returns: Filtered and sorted announcements per query configuration
    private func applyQuery(_ query: AnnouncementListQuery, to announcements: [Announcement]) -> [Announcement] {
        var result = announcements
        
        // Apply sorting
        switch query.sortBy {
        case .createdAtDescending:
            // Sort by lastSeenDate descending (newest first)
            // Note: lastSeenDate is String in DD/MM/YYYY format, we parse for proper sorting
            result = result.sorted { lhs, rhs in
                parseDate(lhs.lastSeenDate) > parseDate(rhs.lastSeenDate)
            }
        case .createdAtAscending:
            result = result.sorted { lhs, rhs in
                parseDate(lhs.lastSeenDate) < parseDate(rhs.lastSeenDate)
            }
        case .distanceFromUser:
            // Future: sort by distance from user location
            result = result.sorted { lhs, rhs in
                parseDate(lhs.lastSeenDate) > parseDate(rhs.lastSeenDate)
            }
        }
        
        // Apply limit
        if let limit = query.limit {
            result = Array(result.prefix(limit))
        }
        
        return result
    }
    
    /// Parses date string in DD/MM/YYYY format to Date for comparison.
    /// Returns distant past if parsing fails (pushes invalid dates to end when sorting descending).
    ///
    /// - Parameter dateString: Date string in DD/MM/YYYY format
    /// - Returns: Parsed Date or distant past
    private func parseDate(_ dateString: String) -> Date {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd/MM/yyyy"
        return formatter.date(from: dateString) ?? .distantPast
    }
    
    /// Updates card ViewModels array from announcements.
    /// Reuses existing ViewModels when possible for better performance.
    ///
    /// - Parameter announcements: Announcements to display
    private func updateCardViewModels(with announcements: [Announcement]) {
        // Deduplicate announcements by ID (keep first occurrence)
        var seenIDs = Set<String>()
        let uniqueAnnouncements = announcements.filter { announcement in
            seenIDs.insert(announcement.id).inserted
        }
        
        // Create dictionary of existing ViewModels by ID for fast lookup
        let existingVMsByID = Dictionary(uniqueKeysWithValues: cardViewModels.map { ($0.id, $0) })
        
        // Build new array maintaining order, reusing or creating ViewModels
        cardViewModels = uniqueAnnouncements.map { announcement in
            if let existingVM = existingVMsByID[announcement.id] {
                // Reuse and update existing ViewModel
                existingVM.update(with: announcement)
                return existingVM
            } else {
                // Create new ViewModel for new announcement
                return AnnouncementCardViewModel(
                    announcement: announcement,
                    onAction: handleAnnouncementAction
                )
            }
        }
    }
    
    /// Handles actions from announcement cards (taps, etc.)
    ///
    /// - Parameter action: Action performed on card
    private func handleAnnouncementAction(_ action: AnnouncementAction) {
        switch action {
        case .selected(let id):
            onAnnouncementTapped(id)
        }
    }
}

