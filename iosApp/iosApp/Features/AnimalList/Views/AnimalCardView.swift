import SwiftUI
import Shared

/**
 * SwiftUI view for displaying a single animal card in the list.
 * Layout: [Image] [Location + Species|Breed] [Status Badge + Date]
 *
 * Design matches Figma specifications (node 52:6541):
 * - Card background: #FAFAFA
 * - Card border radius: 4pt
 * - Card shadow: 1pt offset, 4pt blur
 * - Card padding: 8pt
 * - Image placeholder: 63pt circular (#EEEEEE background)
 * - Location icon before text (13pt)
 * - Species|Breed with separator (16pt | 14pt)
 * - Status badge: 12pt text, 10pt radius
 * - Date: 13pt text (#545F71)
 *
 * - Parameter animal: Animal entity to display
 * - Parameter onTap: Callback when card is tapped
 */
struct AnimalCardView: View {
    let animal: Animal
    let onTap: () -> Void
    
    var body: some View {
        HStack(alignment: .center, spacing: 10) {
            // Photo placeholder (63pt circular)
            ZStack {
                Circle()
                    .fill(Color(hex: "#EEEEEE")) // Light gray placeholder
                    .frame(width: 63, height: 63)
                
                // Animal icon placeholder
                Image(systemName: "pawprint.fill")
                    .font(.system(size: 24))
                    .foregroundColor(Color(hex: "#93A2B4")) // Tertiary text color
            }
            
            // Animal info column (location + species/breed)
            VStack(alignment: .leading, spacing: 8) {
                // Location with icon
                HStack(spacing: 4) {
                    Image(systemName: "mappin")
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#545F71")) // Secondary text color
                    
                    Text("\(animal.location.city), +\(animal.location.radiusKm)km")
                        .font(.system(size: 13))
                        .foregroundColor(Color(hex: "#545F71")) // Secondary text color
                }
                
                // Species | Breed
                HStack(spacing: 4) {
                    Text(animal.species.displayName)
                        .font(.system(size: 16))
                        .foregroundColor(Color(hex: "#2D2D2D")) // Primary text color
                    
                    Text("|")
                        .font(.system(size: 16))
                        .foregroundColor(Color(hex: "#93A2B4")) // Tertiary text color
                    
                    Text(animal.breed)
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: "#2D2D2D")) // Primary text color
                }
            }
            
            Spacer()
            
            // Status column (badge + date) - right aligned
            VStack(alignment: .trailing, spacing: 4) {
                // Status badge
                Text(animal.status.displayName)
                    .font(.system(size: 12))
                    .foregroundColor(.white)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 2)
                    .background(Color(hex: animal.status.badgeColor))
                    .cornerRadius(10)
                
                // Date
                Text(animal.lastSeenDate)
                    .font(.system(size: 13))
                    .foregroundColor(Color(hex: "#545F71")) // Secondary text color
            }
            .padding(.vertical, 8)
        }
        .padding(8)
        .background(Color(hex: "#FAFAFA"))
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

