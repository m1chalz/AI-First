import SwiftUI

/// Coordinate input component with two text fields side by side.
/// Uses Model pattern for pure presentation without @Published properties.
struct CoordinateInputView: View {
    let model: Model
    @Binding var latitude: String
    @Binding var longitude: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            // Two text fields side by side
            HStack(spacing: 10) {
                // Latitude field
                TextField(model.latitudePlaceholder, text: $latitude)
                    .font(.custom("Hind-Regular", size: 16))
                    .foregroundColor(Color(hex: "#364153"))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .frame(height: 41)
                    .background(Color.white)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(Color(hex: "#D1D5DC"), lineWidth: 0.667)
                    )
                    .keyboardType(.decimalPad)
                    .accessibilityIdentifier(model.latitudeAccessibilityID)
                
                // Longitude field
                TextField(model.longitudePlaceholder, text: $longitude)
                    .font(.custom("Hind-Regular", size: 16))
                    .foregroundColor(Color(hex: "#364153"))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .frame(height: 41)
                    .background(Color.white)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(Color(hex: "#D1D5DC"), lineWidth: 0.667)
                    )
                    .keyboardType(.decimalPad)
                    .accessibilityIdentifier(model.longitudeAccessibilityID)
            }
            
            // Error messages
            if let latError = model.latitudeError {
                Text(latError)
                    .font(.custom("Hind-Regular", size: 12))
                    .foregroundColor(.red)
            }
            
            if let longError = model.longitudeError {
                Text(longError)
                    .font(.custom("Hind-Regular", size: 12))
                    .foregroundColor(.red)
            }
        }
    }
}

extension CoordinateInputView {
    struct Model {
        let label: String
        let latitudePlaceholder: String
        let longitudePlaceholder: String
        let latitudeError: String?
        let longitudeError: String?
        let latitudeAccessibilityID: String
        let longitudeAccessibilityID: String
    }
}

