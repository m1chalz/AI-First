import SwiftUI

/// Location coordinate input component with GPS capture button.
/// Composes two ValidatedTextField instances for latitude and longitude.
struct LocationCoordinateView: View {
    let model: Model
    @Binding var latitude: String
    @Binding var longitude: String
    let onGPSButtonTap: () async -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Latitude text field (composed ValidatedTextField)
            ValidatedTextField(model: model.latitudeField, text: $latitude)
            
            // Longitude text field (composed ValidatedTextField)
            ValidatedTextField(model: model.longitudeField, text: $longitude)
            
            // GPS capture button
            Button(action: {
                Task {
                    await onGPSButtonTap()
                }
            }) {
                HStack {
                    Image(systemName: "location.circle.fill")
                    Text(model.gpsButtonTitle)
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .accessibilityIdentifier(model.gpsButtonAccessibilityID)
            
            // Optional helper text (e.g., "GPS capture successful")
            if let helperText = model.helperText {
                Text(helperText)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
    }
}

extension LocationCoordinateView {
    struct Model {
        let latitudeField: ValidatedTextField.Model
        let longitudeField: ValidatedTextField.Model
        let gpsButtonTitle: String
        let gpsButtonAccessibilityID: String
        let helperText: String?
    }
}

