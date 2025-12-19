import Foundation

// MARK: - Landing Page Factory

extension HeroPanelView.Model {
    /// Creates hero panel model for Landing Page (Home tab).
    /// - Parameters:
    ///   - onLostPetTap: Called when Lost Pet button is tapped
    ///   - onFoundPetTap: Called when Found Pet button is tapped
    /// - Returns: Configured HeroPanelView.Model for landing page
    static func landingPage(
        onLostPetTap: @escaping () -> Void = {},
        onFoundPetTap: @escaping () -> Void = {}
    ) -> HeroPanelView.Model {
        HeroPanelView.Model(
            title: L10n.LandingPage.Hero.title,
            leftButton: FloatingActionButtonModel(
                title: L10n.LandingPage.Hero.lostPetButton,
                style: .secondary,
                iconSource: .sfSymbol("exclamationmark.triangle"),
                iconPosition: .left,
                expandsHorizontally: true
            ),
            rightButton: FloatingActionButtonModel(
                title: L10n.LandingPage.Hero.foundPetButton,
                style: .primary,
                iconSource: .sfSymbol("checkmark"),
                iconPosition: .left,
                expandsHorizontally: true
            ),
            onLeftButtonTap: onLostPetTap,
            onRightButtonTap: onFoundPetTap,
            titleAccessibilityId: "home.hero.title",
            leftButtonAccessibilityId: "home.hero.lostPetButton",
            rightButtonAccessibilityId: "home.hero.foundPetButton"
        )
    }
}
