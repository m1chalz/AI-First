import SwiftUI

extension FoundPetSummaryView {
    /// Design constants from Figma spec for report created confirmation UI
    struct Constants {
        // Gradient colors
        let gradientStartColor: Color = Color(hex: "#5C33FF")
        let gradientEndColor: Color = Color(hex: "#F84BA1")
        let glowColor: Color = Color(hex: "#FB64B6")
        let glowOpacity: Double = 0.2
        let glowBlurRadius: CGFloat = 24
        
        // Text colors
        let titleColor: Color = Color(hex: "#CC000000")  // 80% black (ARGB format: CC = 80% opacity)
        let bodyColor: Color = Color(hex: "#545F71")
        
        // Button colors
        let buttonBackgroundColor: Color = Color(hex: "#155DFC")
        
        // Corner radii
        let passwordBackgroundCornerRadius: CGFloat = 10
        let buttonCornerRadius: CGFloat = 10
        
        // Typography
        let titleFont: Font = .system(size: 32, weight: .regular)
        let bodyFont: Font = .system(size: 16, weight: .regular)
        let bodyLineSpacing: CGFloat = 6.4  // 16px * 0.4 for 1.4 line height
        let passwordFont: Font = .custom("Arial", size: 60)
        let passwordKerning: CGFloat = -1.5
        let buttonFont: Font = .system(size: 18, weight: .semibold)
        
        // Spacing (FR-004)
        let horizontalPadding: CGFloat = 22
        let verticalSpacing: CGFloat = 24
        let topSafeAreaInset: CGFloat = 32
        let bottomSafeAreaInset: CGFloat = 16
        
        // Dimensions
        let passwordContainerWidth: CGFloat = 328
        let passwordContainerHeight: CGFloat = 90
        let buttonWidth: CGFloat = 327
        let buttonHeight: CGFloat = 52
    }
}

