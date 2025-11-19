import SwiftUI
import Shared

/**
 * SwiftUI view for displaying a single animal card in the list.
 * Shows animal photo placeholder, species, breed, location, status badge, and date.
 *
 * Design matches Figma specifications:
 * - Card border radius: 4pt
 * - Card shadow: 1pt offset, 4pt blur
 * - Padding: 16pt horizontal
 * - Image placeholder: 63pt circular
 * - Status badge radius: 10pt
 *
 * - Parameter animal: Animal entity to display
 * - Parameter onTap: Callback when card is tapped
 */
struct AnimalCardView: View {
    let animal: Animal
    let onTap: () -> Void
    
    var body: some View {
        HStack(alignment: .center, spacing: 16) {
            // Photo placeholder (63pt circular)
            ZStack {
                Circle()
                    .fill(Color(hex: "#EEEEEE")) // Light gray placeholder
                    .frame(width: 63, height: 63)
                
                Text(String(animal.species.displayName.prefix(1)))
                    .font(.system(size: 24))
                    .foregroundColor(Color(hex: "#93A2B4")) // Tertiary text color
            }
            
            // Animal info column
            VStack(alignment: .leading, spacing: 4) {
                // Species | Breed
                Text("\(animal.species.displayName) | \(animal.breed)")
                    .font(.system(size: 16))
                    .foregroundColor(Color(hex: "#2D2D2D")) // Primary text color
                
                // Location
                Text("\(animal.location.city), +\(animal.location.radiusKm)km")
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#545F71")) // Secondary text color
                
                // Status badge
                Text(animal.status.displayName)
                    .font(.system(size: 12))
                    .foregroundColor(.white)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color(hex: animal.status.badgeColor))
                    .cornerRadius(10)
                
                // Last seen date
                Text("Last seen: \(animal.lastSeenDate)")
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#93A2B4")) // Tertiary text color
            }
            
            Spacer()
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(4)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 1)
        .accessibilityIdentifier("animalList.item.\(animal.id)")
        .onTapGesture(perform: onTap)
    }
}

// MARK: - Color Extension for Hex Support

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

