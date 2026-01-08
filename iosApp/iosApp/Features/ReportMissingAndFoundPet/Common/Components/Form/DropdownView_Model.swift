import Foundation

extension DropdownView {
    struct Model {
        let label: String
        let placeholder: String
        let options: [(value: T, displayName: String)]
        let errorMessage: String?
        let accessibilityID: String
    }
}

