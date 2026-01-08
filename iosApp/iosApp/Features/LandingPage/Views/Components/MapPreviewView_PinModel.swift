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
        
        /// Pin color based on announcement status
        var pinColor: Color {
            switch status {
            case .active:
                return .red
            case .found:
                return .blue
            case .closed:
                return .gray
            }
        }
        
        /// Pin icon SF Symbol name based on status
        var pinIcon: String {
            switch status {
            case .active:
                return "exclamationmark"
            case .found:
                return "checkmark"
            case .closed:
                return "xmark"
            }
        }
    }
}

