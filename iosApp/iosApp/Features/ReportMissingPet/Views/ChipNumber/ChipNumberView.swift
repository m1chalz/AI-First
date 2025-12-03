import SwiftUI

/// View for Chip Number screen (Step 1/4).
struct ChipNumberView: View {
    @ObservedObject var viewModel: ChipNumberViewModel
    
    private enum Layout {
        static let horizontalPadding: CGFloat = 24
        static let contentTopPadding: CGFloat = 32
        static let contentBottomPadding: CGFloat = 160
        static let sectionSpacing: CGFloat = 24
        static let headerSpacing: CGFloat = 16
        static let fieldLabelSpacing: CGFloat = 8
        static let textFieldVerticalPadding: CGFloat = 12
        static let textFieldHorizontalPadding: CGFloat = 16
        static let textFieldCornerRadius: CGFloat = 10
        static let buttonVerticalPadding: CGFloat = 16
        static let buttonBottomPadding: CGFloat = 32
        static let buttonCornerRadius: CGFloat = 10
    }
    
    var body: some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(alignment: .leading, spacing: Layout.sectionSpacing) {
                    header
                    inputField
                }
                .padding(.horizontal, Layout.horizontalPadding)
                .padding(.top, Layout.contentTopPadding)
                .padding(.bottom, Layout.contentBottomPadding)
            }
            .background(Color.white)
            
            VStack(spacing: 0) {
                continueButton
            }
            .padding(.horizontal, Layout.horizontalPadding)
            .padding(.vertical, 24)
            .frame(maxWidth: .infinity)
            .background(Color.white.ignoresSafeArea(edges: .bottom))
        }
        .background(Color.white.ignoresSafeArea())
    }
    
    private var header: some View {
        VStack(alignment: .leading, spacing: Layout.headerSpacing) {
            Text(L10n.MicrochipNumber.heading)
                .font(.system(size: 32, weight: .regular, design: .default))
                .foregroundColor(Color(hex: "#2D2D2D"))
            
            Text(L10n.MicrochipNumber.description)
                .font(.system(size: 16, weight: .regular))
                .foregroundColor(Color(hex: "#545F71"))
                .fixedSize(horizontal: false, vertical: true)
        }
    }
    
    private var inputField: some View {
        VStack(alignment: .leading, spacing: Layout.fieldLabelSpacing) {
            Text(L10n.MicrochipNumber.fieldLabel)
                .font(.system(size: 16, weight: .regular))
                .foregroundColor(Color(hex: "#364153"))
            
            TextField("", text: $viewModel.chipNumber, prompt: Text(L10n.MicrochipNumber.fieldPlaceholder))
                .keyboardType(.numberPad)
                .textContentType(.oneTimeCode)
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .padding(.vertical, Layout.textFieldVerticalPadding)
                .padding(.horizontal, Layout.textFieldHorizontalPadding)
                .background(
                    RoundedRectangle(cornerRadius: Layout.textFieldCornerRadius)
                        .stroke(Color(hex: "#D1D5DC"), lineWidth: 1)
                        .background(
                            RoundedRectangle(cornerRadius: Layout.textFieldCornerRadius)
                                .fill(Color.white)
                        )
                )
                .accessibilityIdentifier("missingPet.microchip.input")
                .onChange(of: viewModel.chipNumber) { _, newValue in
                    viewModel.formatChipNumber(newValue)
                }
        }
    }
    
    private var continueButton: some View {
        Button(action: viewModel.handleNext) {
            Text(L10n.MicrochipNumber.continueButton)
                .font(.system(size: 18, weight: .semibold))
                .frame(maxWidth: .infinity)
                .padding(.vertical, Layout.buttonVerticalPadding)
                .foregroundColor(.white)
                .background(Color(hex: "#155DFC"))
                .cornerRadius(Layout.buttonCornerRadius)
        }
        .accessibilityIdentifier("missingPet.microchip.continueButton")
    }
}

