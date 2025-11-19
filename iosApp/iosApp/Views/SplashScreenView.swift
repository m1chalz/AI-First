import SwiftUI

/// Initial splash screen displayed on app launch.
/// Shows a 100px red circle on black background.
struct SplashScreenView: View {
    var body: some View {
        ZStack {
            Color.black
                .ignoresSafeArea()
            
            Circle()
                .fill(Color.red)
                .frame(width: 100, height: 100)
        }
    }
}

#Preview {
    SplashScreenView()
}

