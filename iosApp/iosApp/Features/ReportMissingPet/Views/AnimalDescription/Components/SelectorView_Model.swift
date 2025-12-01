import Foundation

extension SelectorView {
    struct Model {
        let label: String
        let options: [(value: T, displayName: String)]
        let errorMessage: String?
        let accessibilityIDPrefix: String
    }
}

