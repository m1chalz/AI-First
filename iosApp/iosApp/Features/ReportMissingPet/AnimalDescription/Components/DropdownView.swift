import SwiftUI

/// Generic dropdown component accepting [String] options for maximum reusability.
/// Uses Model pattern for pure presentation without @Published properties.
struct DropdownView: View {
    let model: Model
    @Binding var selectedIndex: Int?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(model.label)
                .font(.headline)
            
            Menu {
                ForEach(model.options.indices, id: \.self) { index in
                    Button(model.options[index]) {
                        selectedIndex = index
                    }
                }
            } label: {
                HStack {
                    Text(selectedIndex.map { model.options[$0] } ?? model.placeholder)
                        .foregroundColor(selectedIndex == nil ? .secondary : .primary)
                    Spacer()
                    Image(systemName: "chevron.down")
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(8)
            }
            .accessibilityIdentifier(model.accessibilityID)
            
            if let error = model.errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
    }
}

extension DropdownView {
    struct Model {
        let label: String
        let placeholder: String
        let options: [String]
        let errorMessage: String?
        let accessibilityID: String
    }
}

