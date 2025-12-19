package com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi

/**
 * Sealed class for one-off navigation events in Lost Pets Teaser.
 * Effects abstract the "what" (navigate to pet X) from the "how" (tab switch + push).
 */
sealed class LostPetsTeaserEffect {
    /** Navigate to pet details within Lost Pet tab. */
    data class NavigateToPetDetails(val petId: String) : LostPetsTeaserEffect()

    /** Navigate to Lost Pet tab (full list). */
    data object NavigateToLostPetsList : LostPetsTeaserEffect()
}
