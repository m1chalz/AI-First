import SwiftUI

/// Custom date input component matching Figma design.
/// Displays formatted date in text field style with calendar icon.
/// Tapping opens native date picker in sheet.
struct DateInputView: View {
    let model: Model
    @Binding var date: Date
    @State private var showDatePicker = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Label
            Text(model.label)
                .font(.custom("Hind-Regular", size: 16))
                .foregroundColor(Color(hex: "#364153"))
            
            // Input field
            Button(action: {
                showDatePicker = true
            }) {
                HStack(spacing: 0) {
                    // Formatted date text
                    Text(formattedDate)
                        .font(.custom("Hind-Regular", size: 16))
                        .foregroundColor(Color(hex: "#364153"))
                    
                    Spacer()
                    
                    // Calendar icon
                    Image(systemName: "calendar")
                        .font(.system(size: 24))
                        .foregroundColor(Color(hex: "#364153"))
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .frame(height: 49.333)
                .background(Color.white)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color(hex: "#D1D5DC"), lineWidth: 0.667)
                )
            }
            .accessibilityIdentifier(model.accessibilityID)
            
            // Error message
            if let error = model.errorMessage {
                Text(error)
                    .font(.custom("Hind-Regular", size: 12))
                    .foregroundColor(.red)
            }
        }
        .sheet(isPresented: $showDatePicker) {
            DatePickerSheet(
                date: $date,
                dateRange: model.dateRange,
                onDismiss: {
                    showDatePicker = false
                }
            )
        }
    }
    
    /// Formats date as DD/MM/YYYY
    private var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd/MM/yyyy"
        return formatter.string(from: date)
    }
}

// MARK: - Date Picker Sheet

/// Sheet containing native iOS date picker
private struct DatePickerSheet: View {
    @Binding var date: Date
    let dateRange: PartialRangeThrough<Date>?
    let onDismiss: () -> Void
    
    @State private var temporaryDate: Date
    
    init(date: Binding<Date>, dateRange: PartialRangeThrough<Date>?, onDismiss: @escaping () -> Void) {
        self._date = date
        self.dateRange = dateRange
        self.onDismiss = onDismiss
        // Initialize temporary date with current value
        self._temporaryDate = State(initialValue: date.wrappedValue)
    }
    
    var body: some View {
        NavigationView {
            Group {
                if let range = dateRange {
                    DatePicker(
                        L10n.AnimalDescription.DatePicker.title,
                        selection: $temporaryDate,
                        in: range,
                        displayedComponents: .date
                    )
                    .datePickerStyle(.graphical)
                } else {
                    DatePicker(
                        L10n.AnimalDescription.DatePicker.title,
                        selection: $temporaryDate,
                        displayedComponents: .date
                    )
                    .datePickerStyle(.graphical)
                }
            }
            .padding()
            .navigationTitle(L10n.AnimalDescription.DatePicker.title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(L10n.Common.cancel) {
                        onDismiss()
                    }
                    .foregroundColor(Color(hex: "#2D2D2D"))
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(L10n.AnimalDescription.DatePicker.done) {
                        // Commit the temporary date to the binding
                        date = temporaryDate
                        onDismiss()
                    }
                    .foregroundColor(Color(hex: "#2D2D2D"))
                }
            }
        }
        .tint(Color(hex: "#2D2D2D")) // Apply to chevrons in calendar
        .presentationDetents([.medium])
    }
}
