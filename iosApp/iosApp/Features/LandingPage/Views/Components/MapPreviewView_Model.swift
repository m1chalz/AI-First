import MapKit

extension MapPreviewView {
    /// Model determining what MapPreviewView renders.
    ///
    /// This is a simple value type passed to MapPreviewView.
    /// LandingPageViewModel creates and updates it based on location permission state.
    ///
    /// **Cases**:
    /// - `loading`: Initial state before location is determined
    /// - `map`: Show interactive map centered on region with tap handler
    /// - `permissionRequired`: Location permission not granted, show recovery UI
    ///
    /// **Equatable**: Compares by coordinates/messages, ignores closures (for SwiftUI diffing).
    enum Model: Equatable {
        /// Initial loading state (before location is determined)
        case loading
        
        /// Show map centered on region. Tap triggers callback for future fullscreen navigation.
        case map(region: MKCoordinateRegion, onTap: () -> Void)
        
        /// Location permission not granted. Shows message and settings button.
        case permissionRequired(message: String, onGoToSettings: () -> Void)
        
        // MARK: - Equatable (ignores closures)
        
        static func == (lhs: Model, rhs: Model) -> Bool {
            switch (lhs, rhs) {
            case (.loading, .loading):
                return true
            case let (.map(lhsRegion, _), .map(rhsRegion, _)):
                return lhsRegion.center.latitude == rhsRegion.center.latitude
                    && lhsRegion.center.longitude == rhsRegion.center.longitude
            case let (.permissionRequired(lhsMsg, _), .permissionRequired(rhsMsg, _)):
                return lhsMsg == rhsMsg
            default:
                return false
            }
        }
    }
}

