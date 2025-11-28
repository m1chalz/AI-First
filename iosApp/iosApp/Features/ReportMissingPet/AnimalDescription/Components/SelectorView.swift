import SwiftUI

/// Generic radio button selector component accepting [String] options.
/// Uses Model pattern for pure presentation without @Published properties.
struct SelectorView: View {
    let model: Model
    @Binding var selectedIndex: Int?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(model.label)
                .font(.headline)
            
            HStack(spacing: 16) {
                ForEach(model.options.indices, id: \.self) { index in
                    Button(action: { selectedIndex = index }) {
                        HStack {
                            Image(systemName: selectedIndex == index ? "circle.fill" : "circle")
                                .foregroundColor(selectedIndex == index ? .blue : .gray)
                            Text(model.options[index])
                                .foregroundColor(.primary)
                        }
                    }
                    .accessibilityIdentifier("\(model.accessibilityIDPrefix).\(model.options[index].lowercased()).tap")
                }
            }
            
            if let error = model.errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
    }
}

extension SelectorView {
    struct Model {
        let label: String
        let options: [String]
        let errorMessage: String?
        let accessibilityIDPrefix: String
    }
}

