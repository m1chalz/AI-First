import Foundation

extension DateInputView {
    struct Model {
        let label: String
        let dateRange: PartialRangeThrough<Date>?
        let errorMessage: String?
        let accessibilityID: String
        
        init(
            label: String,
            dateRange: PartialRangeThrough<Date>? = nil,
            errorMessage: String? = nil,
            accessibilityID: String
        ) {
            self.label = label
            self.dateRange = dateRange
            self.errorMessage = errorMessage
            self.accessibilityID = accessibilityID
        }
    }
}

