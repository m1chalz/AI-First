import SwiftUI

/// Reusable loading view component with optional message
/// Displays a centered circular progress indicator and optional text
struct LoadingView: View {
    let model: Model
    
    var body: some View {
        VStack(spacing: 20) {
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: Color(hex: "#2D2D2D")))
                .scaleEffect(1.5)
                .accessibilityIdentifier(model.accessibilityIdentifier)
            
            if let message = model.message {
                Text(message)
                    .font(.system(size: 16))
                    .foregroundColor(Color(hex: "#6a7282"))
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(hex: "#FAFAFA"))
        .ignoresSafeArea()
    }
}

// MARK: - Previews

#if DEBUG
struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            // With message
            LoadingView(model: .init(message: "Loading data..."))
                .previewDisplayName("With Message")
            
            // Without message
            LoadingView(model: .init())
                .previewDisplayName("Spinner Only")
        }
    }
}
#endif

