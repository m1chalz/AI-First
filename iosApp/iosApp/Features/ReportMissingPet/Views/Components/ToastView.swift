import SwiftUI

/// Reusable pill-style toast matching the Animal Photo design system.
struct ToastView: View {
    let model: Model
    
    var body: some View {
        Text(model.text)
            .font(.system(size: 14, weight: .semibold))
            .foregroundColor(.white)
            .padding(.horizontal, 20)
            .padding(.vertical, 12)
            .background(Color(hex: "#2D2D2D"))
            .cornerRadius(12)
    }
}

