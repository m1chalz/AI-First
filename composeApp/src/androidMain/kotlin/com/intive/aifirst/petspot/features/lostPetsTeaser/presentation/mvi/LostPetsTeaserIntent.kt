package com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi

/**
 * Sealed interface for user intents in Lost Pets Teaser component.
 * Represents all possible user actions in the MVI loop.
 */
sealed interface LostPetsTeaserIntent {
    /** Triggers initial data fetch on first composition. */
    data object LoadData : LostPetsTeaserIntent

    /** Triggers data refresh (for error recovery via retry button). */
    data object RefreshData : LostPetsTeaserIntent

    /** User tapped a pet card in the teaser. */
    data class PetClicked(val petId: String) : LostPetsTeaserIntent

    /** User tapped "View All Lost Pets" button. */
    data object ViewAllClicked : LostPetsTeaserIntent
}
