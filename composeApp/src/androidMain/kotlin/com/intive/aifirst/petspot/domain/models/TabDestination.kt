package com.intive.aifirst.petspot.domain.models

import androidx.annotation.DrawableRes
import com.intive.aifirst.petspot.R
import com.intive.aifirst.petspot.navigation.TabRoute

/**
 * Enum representing the 5 tab destinations in bottom navigation.
 * Provides UI configuration (label, icon resource, testId) and maps to type-safe routes.
 */
enum class TabDestination(
    val label: String,
    @DrawableRes val iconRes: Int,
    val testId: String,
) {
    HOME(
        label = "Home",
        iconRes = R.drawable.ic_home,
        testId = "homeTab",
    ),
    LOST_PET(
        label = "Lost Pet",
        iconRes = R.drawable.ic_lost,
        testId = "lostPetTab",
    ),
    FOUND_PET(
        label = "Found Pet",
        iconRes = R.drawable.ic_found,
        testId = "foundPetTab",
    ),
    CONTACT_US(
        label = "Contact Us",
        iconRes = R.drawable.ic_contact,
        testId = "contactTab",
    ),
    ACCOUNT(
        label = "Account",
        iconRes = R.drawable.ic_account,
        testId = "accountTab",
    ),
    ;

    /**
     * Maps this UI enum to type-safe navigation route.
     * Used when triggering navigation via NavController.
     */
    fun toRoute(): TabRoute =
        when (this) {
            HOME -> TabRoute.Home
            LOST_PET -> TabRoute.LostPet
            FOUND_PET -> TabRoute.FoundPet
            CONTACT_US -> TabRoute.Contact
            ACCOUNT -> TabRoute.Account
        }
}
