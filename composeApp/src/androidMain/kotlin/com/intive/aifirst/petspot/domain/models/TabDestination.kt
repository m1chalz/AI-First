package com.intive.aifirst.petspot.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector
import com.intive.aifirst.petspot.navigation.TabRoute

/**
 * Enum representing the 5 tab destinations in bottom navigation.
 * Provides UI configuration (label, icon, testId) and maps to type-safe routes.
 */
enum class TabDestination(
    val label: String,
    val icon: ImageVector,
    val testId: String,
) {
    HOME(
        label = "Home",
        icon = Icons.Filled.Home,
        testId = "homeTab",
    ),
    LOST_PET(
        label = "Lost Pet",
        icon = Icons.Filled.Pets,
        testId = "lostPetTab",
    ),
    FOUND_PET(
        label = "Found Pet",
        icon = Icons.Filled.Pets,
        testId = "foundPetTab",
    ),
    CONTACT_US(
        label = "Contact Us",
        icon = Icons.AutoMirrored.Filled.ContactSupport,
        testId = "contactTab",
    ),
    ACCOUNT(
        label = "Account",
        icon = Icons.Filled.AccountCircle,
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
