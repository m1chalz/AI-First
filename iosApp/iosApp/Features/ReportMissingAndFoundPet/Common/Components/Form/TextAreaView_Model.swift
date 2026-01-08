import Foundation

extension TextAreaView {
    struct Model {
        let label: String
        let placeholder: String
        let maxLength: Int
        let characterCountText: String      // e.g., "123/500" (formatted in ViewModel)
        let accessibilityID: String
    }
}

