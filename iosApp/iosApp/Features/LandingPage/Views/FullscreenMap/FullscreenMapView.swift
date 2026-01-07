import SwiftUI

/// Fullscreen map placeholder view.
/// Currently displays empty content - MapKit integration in future ticket.
struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    
    var body: some View {
        Color(.systemBackground)
            .accessibilityIdentifier("fullscreenMap.container")
    }
}

