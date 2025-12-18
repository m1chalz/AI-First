import Foundation

// MARK: - Landing Page Factory

extension ListHeaderRowView.Model {
    /// Creates list header model for Landing Page "Recent Reports" section.
    /// - Parameter onViewAllTap: Called when "View All" is tapped (switches to Lost Pet tab)
    /// - Returns: Configured ListHeaderRowView.Model for landing page
    static func recentReports(
        onViewAllTap: @escaping () -> Void = {}
    ) -> ListHeaderRowView.Model {
        ListHeaderRowView.Model(
            title: L10n.LandingPage.ListHeader.title,
            actionTitle: L10n.LandingPage.ListHeader.viewAll,
            onActionTap: onViewAllTap,
            titleAccessibilityId: "home.recentReports.title",
            actionAccessibilityId: "home.recentReports.viewAll"
        )
    }
}

