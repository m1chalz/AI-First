import Foundation
import CoreLocation
import SwiftUI

extension MapPreviewView {
    /// Lightweight model for map pin placement.
    /// Represents a single announcement location on the map preview.
    ///
    /// **Pin colors**:
    /// - Red: Missing pets (status == .active)
    /// - Blue: Found pets (status == .found)
    struct PinModel: Identifiable, Equatable {
        let id: String
        let coordinate: Coordinate
        let status: AnnouncementStatus
        
        var clLocationCoordinate: CLLocationCoordinate2D {
            CLLocationCoordinate2D(
                latitude: coordinate.latitude,
                longitude: coordinate.longitude
            )
        }
        
        /// Converts announcement status to TeardropPin display mode.
        var displayMode: TeardropPin.Mode {
            switch status {
            case .active:
                return .active
            case .found:
                return .found
            case .closed:
                return .closed
            }
        }
    }
}

