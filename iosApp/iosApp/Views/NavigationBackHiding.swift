import SwiftUI

/// View modifier that hides the default navigation bar back button.
/// Used when implementing custom back button in navigation bar.
struct NavigationBackHiding<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        content
            .navigationBarBackButtonHidden(true)
    }
}

