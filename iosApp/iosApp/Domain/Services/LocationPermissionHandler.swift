import Foundation
import UIKit

/// Handler for location permission logic and lifecycle management.
/// Provides reusable location permission flow across ViewModels.
/// Testable through dependency injection (LocationService, NotificationCenter).
///
/// Architecture: Pure business logic handler (no @Published properties).
/// ViewModels use this handler and manage their own @Published UI state.
/// Not @MainActor - async methods are thread-safe, ViewModels handle main thread updates.
class LocationPermissionHandler {
    // MARK: - Dependencies
    
    private let locationService: LocationServiceProtocol
    private let notificationCenter: NotificationCenter
    
    // MARK: - State
    
    private var foregroundObserver: NSObjectProtocol?
    private var streamObserverTask: Task<Void, Never>?
    private var lastKnownStatus: LocationPermissionStatus?
    private var onStatusChangeCallback: ((_ status: LocationPermissionStatus, _ didBecomeAuthorized: Bool) -> Void)?
    
    // MARK: - Initialization
    
    /// Production initializer with default NotificationCenter.
    convenience init(locationService: LocationServiceProtocol) {
        self.init(
            locationService: locationService,
            notificationCenter: .default
        )
    }
    
    /// Testable initializer with injectable NotificationCenter.
    /// - Parameters:
    ///   - locationService: Service for location permissions and fetching
    ///   - notificationCenter: Notification center for app lifecycle events (injectable for tests)
    init(
        locationService: LocationServiceProtocol,
        notificationCenter: NotificationCenter
    ) {
        self.locationService = locationService
        self.notificationCenter = notificationCenter
    }
    
    deinit {
        stopObservingLocationPermissionChanges()
    }
    
    // MARK: - Public API
    
    /// Result of location request with permission handling.
    struct LocationRequestResult {
        let location: Coordinate?
        let status: LocationPermissionStatus
    }
    
    /// Requests location with full permission handling flow.
    /// - Checks current permission status
    /// - Requests permission if status is .notDetermined
    /// - Fetches location if status is authorized
    /// - Returns result with optional location and final status
    ///
    /// ViewModel decides whether to show alert based on returned status.
    func requestLocationWithPermissions() async -> LocationRequestResult {
        var status = await locationService.authorizationStatus
        
        // Request permission if not determined (iOS shows system alert)
        if status == .notDetermined {
            status = await locationService.requestWhenInUseAuthorization()
        }
        
        // Store status for change detection
        lastKnownStatus = status
        
        // Fetch location if authorized
        let location: Coordinate? = if status.isAuthorized {
            await locationService.requestLocation()
        } else {
            nil
        }
        
        return LocationRequestResult(
            location: location,
            status: status
        )
    }
    
    /// Checks if permission status changed (useful when app returns from Settings).
    /// Compares current status with last known status.
    /// - Returns: Tuple with new status and flag indicating if changed from unauthorized → authorized
    func checkPermissionStatusChange() async -> (status: LocationPermissionStatus, didBecomeAuthorized: Bool) {
        let previousStatus = lastKnownStatus
        let currentStatus = await locationService.authorizationStatus
        
        // Update last known status
        lastKnownStatus = currentStatus
        
        // Detect change from unauthorized → authorized
        let wasUnauthorized = previousStatus.map { !$0.isAuthorized } ?? true
        let didBecomeAuthorized = wasUnauthorized && currentStatus.isAuthorized
        
        return (status: currentStatus, didBecomeAuthorized: didBecomeAuthorized)
    }
    
    // MARK: - Permission Change Observer (opt-in)
    
    /// Starts observing location permission changes from TWO sources:
    /// 1. **Real-time stream**: When user responds to system permission dialog
    /// 2. **Foreground notification**: When app returns from background (user may have changed permissions in Settings)
    ///
    /// Call this from ViewModel that needs dynamic permission change handling.
    /// Separated from init for better testability and explicit opt-in.
    ///
    /// - Parameter onStatusChange: Callback invoked on MainActor with status and change detection
    func startObservingLocationPermissionChanges(
        onStatusChange: @escaping (_ status: LocationPermissionStatus, _ didBecomeAuthorized: Bool) -> Void
    ) {
        // Prevent double registration
        guard foregroundObserver == nil && streamObserverTask == nil else { return }
        
        onStatusChangeCallback = onStatusChange
        
        // Source 1: Real-time stream from LocationService (user responds to system dialog)
        let stream = locationService.authorizationStatusStream
        streamObserverTask = Task { [weak self] in
            var previousStatus: LocationPermissionStatus?
            
            for await status in stream {
                guard !Task.isCancelled else { break }
                guard let self else { break }
                
                // Detect unauthorized → authorized transition
                let wasUnauthorized = previousStatus.map { !$0.isAuthorized } ?? true
                let didBecomeAuthorized = wasUnauthorized && status.isAuthorized
                
                previousStatus = status
                self.lastKnownStatus = status
                
                // Invoke callback on MainActor
                await MainActor.run {
                    self.onStatusChangeCallback?(status, didBecomeAuthorized)
                }
            }
        }
        
        // Source 2: Foreground notification (app returns from Settings)
        foregroundObserver = notificationCenter.addObserver(
            forName: UIApplication.willEnterForegroundNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            Task { [weak self] in
                guard let self else { return }
                let result = await self.checkPermissionStatusChange()
                await MainActor.run {
                    self.onStatusChangeCallback?(result.status, result.didBecomeAuthorized)
                }
            }
        }
    }
    
    /// Stops observing location permission changes.
    /// Call this in ViewModel deinit or when observation is no longer needed.
    func stopObservingLocationPermissionChanges() {
        // Stop stream observer
        streamObserverTask?.cancel()
        streamObserverTask = nil
        
        // Stop foreground observer
        if let observer = foregroundObserver {
            notificationCenter.removeObserver(observer)
            foregroundObserver = nil
        }
        
        onStatusChangeCallback = nil
    }
}

