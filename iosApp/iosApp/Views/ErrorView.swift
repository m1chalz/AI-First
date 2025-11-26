import SwiftUI

/// Reusable error view component with icon, title, message and optional retry button
struct ErrorView: View {
    let model: Model
    
    var body: some View {
        ZStack {
            Color(hex: "#FAFAFA")
                .ignoresSafeArea()
            
            VStack(spacing: 20) {
                Image(systemName: "exclamationmark.triangle")
                    .font(.system(size: 48))
                    .foregroundColor(Color(hex: "#fb2c36"))
                
                Text(model.title)
                    .font(.system(size: 20, weight: .semibold))
                    .foregroundColor(Color(hex: "#101828"))
                    .multilineTextAlignment(.center)
                
                Text(model.message)
                    .font(.system(size: 16))
                    .foregroundColor(Color(hex: "#6a7282"))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                    .accessibilityIdentifier("\(model.accessibilityIdentifier).message")
                
                if let onRetry = model.onRetry {
                    Button(action: onRetry) {
                        Text(model.retryButtonTitle)
                            .font(.system(size: 16))
                            .foregroundColor(.white)
                            .padding(.horizontal, 32)
                            .padding(.vertical, 12)
                            .background(Color(hex: "#155dfc"))
                            .cornerRadius(10)
                    }
                    .buttonStyle(.plain)
                    .accessibilityIdentifier("\(model.accessibilityIdentifier).retry.button")
                }
            }
            .padding()
        }
    }
}

// MARK: - Previews

#if DEBUG
struct ErrorView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            // Full error with retry
            ErrorView(model: .init(
                title: "Failed to load",
                message: "Network connection lost. Please check your settings.",
                onRetry: { print("Retry tapped") }
            ))
            .previewDisplayName("With Retry")
            
            // Error message only
            ErrorView(model: .init(
                title: "Error",
                message: "Something went wrong"
            ))
            .previewDisplayName("Message Only")
        }
    }
}
#endif

